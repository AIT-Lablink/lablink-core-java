//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync.consumer.impl;

import at.ac.ait.lablink.core.connection.ILlConnection;
import at.ac.ait.lablink.core.connection.encoding.encodeables.Header;
import at.ac.ait.lablink.core.connection.encoding.encodeables.IPayload;
import at.ac.ait.lablink.core.connection.messaging.IMessageCallback;
import at.ac.ait.lablink.core.connection.messaging.MsgHeader;
import at.ac.ait.lablink.core.connection.rpc.IRpcRequester;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;
import at.ac.ait.lablink.core.connection.rpc.reply.IRpcReplyCallback;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestCallback;
import at.ac.ait.lablink.core.connection.topic.MsgSubscription;
import at.ac.ait.lablink.core.connection.topic.RpcDestination;
import at.ac.ait.lablink.core.connection.topic.RpcSubject;
import at.ac.ait.lablink.core.payloads.ErrorMessage;
import at.ac.ait.lablink.core.payloads.StatusMessage;
import at.ac.ait.lablink.core.payloads.StatusMessage.StatusCode;
import at.ac.ait.lablink.core.service.sync.ELlSimulationMode;
import at.ac.ait.lablink.core.service.sync.ELlSyncClientState;
import at.ac.ait.lablink.core.service.sync.ISyncParameter;
import at.ac.ait.lablink.core.service.sync.SyncParticipant;
import at.ac.ait.lablink.core.service.sync.consumer.ISyncClientService;
import at.ac.ait.lablink.core.service.sync.consumer.ISyncConsumer;
import at.ac.ait.lablink.core.service.sync.ex.SyncServiceRuntimeException;
import at.ac.ait.lablink.core.service.sync.impl.SyncHostServiceImpl;
import at.ac.ait.lablink.core.service.sync.payloads.SyncClientConfigMessage;
import at.ac.ait.lablink.core.service.sync.payloads.SyncGoReply;
import at.ac.ait.lablink.core.service.sync.payloads.SyncGoRequest;
import at.ac.ait.lablink.core.service.sync.payloads.SyncParamMessage;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of the SyncClient service interface.
 *
 * <p>The {@link SyncClientServiceImpl} provides functionality for simulators to interact as
 * a synchronized client with AIT Lablink's {@link SyncHostServiceImpl}.
 * The service requires a {@link ILlConnection} and a {@link ISyncConsumer} to be set.
 *
 * <p>The service will interact with the {@link SyncHostServiceImpl} entity, maintain
 * simulation flow, provide
 * simulation-related parameters and call init()/go()/stop() methods provided by the
 * registered {@link ISyncConsumer} implementation.
 * After instantiation of the SyncCa ISyncConsumer must be registered and the
 * {@link #start()} method must be called.
 */
public class SyncClientServiceImpl implements ISyncClientService, Runnable {

  private static Logger logger = LoggerFactory.getLogger(SyncClientServiceImpl.class);

  private ILlConnection lablinkConnection;

  private List<ISyncConsumer> syncConsumers = new ArrayList<ISyncConsumer>();

  private ELlSyncClientState currentSyncState = ELlSyncClientState.NOT_REGISTERED;
  private final long registeringTimeout;

  private SyncParticipant registeredSyncHost;
  private SyncClientConfig syncParams;
  private long startedStamp = 0;

  private long currentSimTime = -1;

  private Thread runnerThread;

  /**
   * Constructor.
   *
   * @param lablinkConnection Lablink connection object that will be used by the service.
   * @param config            Configuration that will be used by the service.
   */
  public SyncClientServiceImpl(ILlConnection lablinkConnection, Configuration config) {

    if (config == null) {
      logger.info("No configuration set. Use default values");
      config = new BaseConfiguration();
    }

    this.lablinkConnection = lablinkConnection;

    lablinkConnection.registerEncodeableFactory(SyncGoRequest.class);
    lablinkConnection.registerEncodeableFactory(SyncGoReply.class);
    lablinkConnection.registerEncodeableFactory(SyncParamMessage.class);
    lablinkConnection.registerEncodeableFactory(SyncClientConfigMessage.class);

    registeringTimeout = config.getLong("syncClient.registeringTimeout", 180) * 1000;
    registerHandlers();

  }


  private void registerHandlers() {
    logger.info("Registering necessary sync client handlers...");

    MsgSubscription
        helloMsgSub =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectElement("sync").addSubjectElement("hello").build();
    lablinkConnection.registerMessageHandler(helloMsgSub, new HelloMsgHandler());

    MsgSubscription
        closeMsgSub =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectElement("sync").addSubjectElement("close").build();
    lablinkConnection.registerMessageHandler(closeMsgSub, new CloseMsgHandler());

    RpcSubject
        initSub =
        RpcSubject.getBuilder().addSubjectElement("sync").addSubjectElement("init").build();
    lablinkConnection.registerRequestHandler(initSub, new SyncInitRpcRequestCallback());

    RpcSubject
        goSub =
        RpcSubject.getBuilder().addSubjectElement("sync").addSubjectElement("go").build();
    lablinkConnection.registerRequestHandler(goSub, new SyncGoRpcRequestCallback());

    RpcSubject
        stopSub =
        RpcSubject.getBuilder().addSubjectElement("sync").addSubjectElement("stop").build();
    lablinkConnection.registerRequestHandler(stopSub, new SyncStopRpcRequestCallback());
  }


  @Override
  public void registerSyncConsumer(ISyncConsumer syncConsumer) {
    this.syncConsumers.add(syncConsumer);
  }

  @Override
  public void unregisterSyncConsumer(ISyncConsumer syncConsumer) {
    this.syncConsumers.remove(syncConsumer);
  }


  @Override
  public void start() {
  }

  @Override
  public void run() {
    //Timeout handling for WAITING_FOR_SIMULATION
    try {
      Thread.sleep(this.registeringTimeout);
    } catch (InterruptedException ex) {
      logger.debug("Interrupting waiting runner thread");
      return;
    }

    if (this.currentSyncState == ELlSyncClientState.WAITING_FOR_SIMULATION) {
      logger.warn(
          "Sync doesn't start simulation within waiting timeout of {}ms. Switch back to non "
              + "registered state.", this.registeringTimeout);
      this.setCurrentSyncState(ELlSyncClientState.NOT_REGISTERED);
    }
  }


  @Override
  public void shutdown() {
    logger.info("Shutdown");

    if (this.currentSyncState == ELlSyncClientState.SIMULATING) {
      executeStop();
    }

    if (this.runnerThread != null) {
      this.runnerThread.interrupt();
    }
  }

  @Override
  public ISyncParameter getSyncParameter() {
    return this.syncParams; //Only available during simulation, otherwise null.
  }

  @Override
  public long getCurrentSimTime() {
    if (currentSyncState != ELlSyncClientState.SIMULATING) {
      return -1;
    } else {
      return this.currentSimTime;
    }
  }

  private synchronized void setCurrentSyncState(ELlSyncClientState currentSyncState) {
    if (this.currentSyncState == currentSyncState) {
      //No change return
      return;
    }
    this.currentSyncState = currentSyncState;

    if (this.currentSyncState == ELlSyncClientState.NOT_REGISTERED) {
      setRegisteredSyncHost(null);
      startedStamp = 0;
      currentSimTime = -1;
      syncParams = null;
    }

    if (this.currentSyncState == ELlSyncClientState.WAITING_FOR_SIMULATION) {
      if (runnerThread != null && !runnerThread.isAlive()) {
        runnerThread.interrupt();
      }
      runnerThread = new Thread(this);
      runnerThread.setName("SyncClientRegisteringRunner");
      runnerThread.setDaemon(true);

      runnerThread.start();

    }
    if (this.currentSyncState == ELlSyncClientState.SIMULATING) {
      runnerThread.interrupt();
    }
  }


  private synchronized void setRegisteredSyncHost(SyncParticipant registeredSyncHost) {
    this.registeredSyncHost = registeredSyncHost;
  }


  private boolean executeInit(List<IPayload> payloads) {

    logger.info("Reading configuration parameters from " + payloads.get(0));

    if (!(payloads.get(0) instanceof SyncParamMessage)) {
      throw new SyncServiceRuntimeException(
          "Can't parse SyncParamMessage. Wrong type '" + payloads.get(0).getType() + "'.");
    }

    if (!(payloads.get(1) instanceof SyncClientConfigMessage)) {
      throw new SyncServiceRuntimeException(
          "Can't parse SyncClientConfigMessage. Wrong type '" + payloads.get(1).getType() + "'.");
    }

    this.syncParams =
        new SyncClientConfig((SyncParamMessage) payloads.get(0),
            (SyncClientConfigMessage) payloads.get(1));
    currentSimTime = syncParams.getSimBeginTime();

    boolean returnVal = true;
    for (ISyncConsumer consumer : syncConsumers) {
      returnVal &= consumer.init(syncParams);
    }

    return returnVal;
  }


  private long executeGo(List<IPayload> payloads) {

    if (startedStamp <= 0) {
      startedStamp = System.currentTimeMillis();
    }

    if (!(payloads.get(0) instanceof SyncGoRequest)) {
      throw new SyncServiceRuntimeException(
          "Unexpected payloads type '" + payloads.get(0).getType() + "' for SyncGoRequest.");
    }

    if (payloads.size() > 1 && payloads.get(1) instanceof SyncParamMessage) {
      //Optional change of sync parameter during simulation
      this.syncParams.setSyncParameter((SyncParamMessage) payloads.get(1));
    }

    SyncGoRequest goRequest = (SyncGoRequest) payloads.get(0);
    long syncParamSimUntil = goRequest.getSimUntil();
    currentSimTime = calculateCurrentSimTime(goRequest.getActualSimTime());

    long syncParamNextSim = -1;

    if (syncConsumers.size() == 0) {
      syncParamNextSim = syncParamSimUntil + syncParams.getStepSize();
    }

    for (ISyncConsumer consumer : syncConsumers) {
      long until = consumer.go(currentSimTime, syncParamSimUntil, syncParams);
      syncParamNextSim = syncParamNextSim > 0 ? Math.min(syncParamNextSim, until) : until;
    }

    if (syncParamNextSim <= currentSimTime) {
      throw new SyncServiceRuntimeException(
          "NextSimTime (" + syncParamNextSim + ") is smaller than current simulation step ("
              + currentSimTime + ")");
    } else if (syncParamNextSim < 1) {
      throw new SyncServiceRuntimeException("Unallowed NextSimTime (" + syncParamNextSim + ").");
    }

    return syncParamNextSim;
  }

  private long calculateCurrentSimTime(long actualTime) {

    if (syncParams.getSimMode() == ELlSimulationMode.SIMULATION) {
      return actualTime;
    } else {
      return syncParams.getSimBeginTime() + (System.currentTimeMillis() - startedStamp) * syncParams
          .getScaleFactor();
    }
  }

  private boolean executeStop() {
    boolean returnVal = true;
    for (ISyncConsumer consumer : syncConsumers) {
      returnVal &= consumer.stop(syncParams);
    }
    currentSimTime = -1;
    return returnVal;
  }

  private class SyncInitRpcRequestCallback extends SyncRpcRequestCallback {

    @Override
    public List<IPayload> handleRequest(RpcHeader header, List<IPayload> payloads) {
      logger.debug("Processing INIT RPC request...");
      List<IPayload> returnValues = super.handleRequest(header, payloads);

      if (returnValues.size() > 0) {
        //Base class already handles the message
        return returnValues;
      }

      setCurrentSyncState(ELlSyncClientState.SIMULATING);
      boolean returnFlag = executeInit(payloads);
      return Collections
          .singletonList((IPayload) new StatusMessage(returnFlag ? StatusCode.OK : StatusCode.NOK));
    }

  }

  private class SyncGoRpcRequestCallback extends SyncRpcRequestCallback {

    @Override
    public List<IPayload> handleRequest(RpcHeader header, List<IPayload> payloads) {
      logger.debug("Processing GO RPC request...");
      List<IPayload> returnValues = super.handleRequest(header, payloads);

      if (returnValues.size() > 0) {
        //Base class already handles the message
        return returnValues;
      }

      IPayload returnPayload;

      if (currentSyncState == ELlSyncClientState.SIMULATING) {
        long returnVal = executeGo(payloads);
        returnPayload = new SyncGoReply(returnVal);
      } else {
        returnPayload =
            new ErrorMessage(ErrorMessage.EErrorCode.SYNC_ERROR,
                "A sync go request is received but client is in state " + currentSyncState
                    + " Expected (" + ELlSyncClientState.SIMULATING + ").");
      }

      return Collections.singletonList(returnPayload);
    }

  }

  private class SyncStopRpcRequestCallback extends SyncRpcRequestCallback {

    @Override
    public List<IPayload> handleRequest(RpcHeader header, List<IPayload> payloads) {
      logger.debug("Processing STOP RPC request...");
      List<IPayload> returnValues = super.handleRequest(header, payloads);

      if (returnValues.size() > 0) {
        //Base class already handles the message
        return returnValues;
      }

      IPayload returnPayload;

      if (currentSyncState == ELlSyncClientState.SIMULATING) {
        boolean returnFlag = executeStop();
        returnPayload = new StatusMessage(returnFlag ? StatusCode.OK : StatusCode.NOK);
        setCurrentSyncState(ELlSyncClientState.NOT_REGISTERED);
      } else {
        returnPayload =
            new ErrorMessage(ErrorMessage.EErrorCode.SYNC_ERROR,
                "A sync stop request is received but client is in state " + currentSyncState
                    + " Expected (" + ELlSyncClientState.SIMULATING + ").");
      }

      return Collections.singletonList(returnPayload);
    }

  }


  private abstract class SyncRpcRequestCallback implements IRpcRequestCallback {


    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {
      logger.error("Sync RPC request from {}  reported errors! Header: {} Errors: {}",
          header.getSourceClientId(), header, errors);
    }

    @Override
    public List<IPayload> handleRequest(RpcHeader header, List<IPayload> payloads) {

      logger.debug("Client handling RPC sync request {}", header.getSubject());

      if (registeredSyncHost == null) {
        logger.warn(
            "A sync request is received from Sync Host {} {} that isn't registered. Maybe the"
                + " host restarted during a running simulation.", header.getSourceGroupId(),
            header.getSourceClientId());
        return Collections.singletonList(
            (IPayload) new ErrorMessage(ErrorMessage.EErrorCode.SYNC_ERROR,
                "A sync request is received from an unregistered Sync Host"));
      }

      //TODO check if registered SyncHost is null and what should I do if the registeredSyncHost
      // is null.
      if (!(registeredSyncHost.getGroupName().equals(header.getSourceGroupId())
          && registeredSyncHost.getClientName().equals(header.getSourceClientId()))) {
        logger.warn("A sync request is received from a wrong Sync Host {} {}",
            header.getSourceGroupId(), header.getSourceClientId());
        return Collections.singletonList(
            (IPayload) new ErrorMessage(ErrorMessage.EErrorCode.SYNC_ERROR,
                "A sync request is received from a wrong Sync Host"));
      }

      if (currentSyncState == ELlSyncClientState.NOT_REGISTERED) {
        logger.warn("A sync request is received within the wrong client state '{}'.",
            currentSyncState);
        return Collections.singletonList(
            (IPayload) new ErrorMessage(ErrorMessage.EErrorCode.SYNC_ERROR,
                "Client isn't in Simulation state."));
      }

      return Collections.emptyList();
    }

  }

  private class HelloMsgHandler implements IMessageCallback {


    @Override
    public void handleMessage(MsgHeader header, List<IPayload> payloads) throws Exception {

      if (currentSyncState != ELlSyncClientState.NOT_REGISTERED) {
        logger.trace("Sync Hello Message received from {} {} but it will be ignored.",
            header.getSourceGroupId(), header.getSourceClientId());
        return;
      }

      RpcSubject
          registeringClientRpcSubject =
          RpcSubject.getBuilder().addSubjectElement("sync").addSubjectElement("register").build();

      IRpcRequester
          localRegisteringRequester =
          lablinkConnection.registerReplyHandler(registeringClientRpcSubject,
              new RegisteringClientReplyHandler());
      IPayload
          errorMsg =
          new StatusMessage(StatusMessage.StatusCode.OK, "SyncClient will be registering");
      RpcDestination
          anotherSyncHost =
          RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_CLIENT)
              .setGroupId(header.getSourceGroupId()).setClientId(header.getSourceClientId())
              .build();
      localRegisteringRequester.sendRequest(anotherSyncHost, errorMsg, 1, 3000);
    }

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {
      logger.warn("HelloMsgHandler received error from {},{}: {}", header.getSourceGroupId(),
          header.getSourceClientId(), errors);
    }

  }

  private class CloseMsgHandler implements IMessageCallback {


    @Override
    public void handleMessage(MsgHeader header, List<IPayload> payloads) throws Exception {
      logger.info("CloseMsg received. Close Sync Client.");

      if (currentSyncState == ELlSyncClientState.SIMULATING) {
        executeStop();
      }

      setCurrentSyncState(ELlSyncClientState.NOT_REGISTERED);
    }

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {
      logger.warn("CloseMsgHandler received error from {},{}: {}", header.getSourceGroupId(),
          header.getSourceClientId(), errors);
    }

  }

  /**
   * Reply Handler used to handle reply of registeringClient.
   *
   * <p>This callback handler will be used by the sync host to inform another sync host about the
   * existing of an already available sync host.
   */
  private class RegisteringClientReplyHandler implements IRpcReplyCallback {

    @Override
    public void handleReply(RpcHeader header, List<IPayload> payloads) {
      if (payloads.size() < 1) {
        logger.warn(
            "Received payloads of RegisterClientNokReplyHandler contains too less payloads objects"
                + ".");
        return;
      }

      if (payloads.get(0) instanceof StatusMessage) {
        StatusMessage statusMessage = (StatusMessage) payloads.get(0);

        if (statusMessage.getStatusCode() == StatusMessage.StatusCode.OK) {
          logger.info("Registered to new SyncHost: {} {}", header.getSourceGroupId(),
              header.getSourceClientId());

          setRegisteredSyncHost(
              new SyncParticipant(header.getSourceGroupId(), header.getSourceClientId()));
          setCurrentSyncState(ELlSyncClientState.WAITING_FOR_SIMULATION);
        }
      } else {
        logger.warn("Received payloads has type '{}' but StatusMessage is expected.",
            payloads.get(0).getType());
      }
    }

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {
      logger.warn("RegisterClientNokReplyHandler received error from {},{}: {}",
          header.getSourceGroupId(), header.getSourceClientId(), errors);
    }

  }

}
