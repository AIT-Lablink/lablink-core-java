//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync.impl;

import at.ac.ait.lablink.core.connection.ILlConnection;
import at.ac.ait.lablink.core.connection.encoding.encodeables.Header;
import at.ac.ait.lablink.core.connection.encoding.encodeables.IPayload;
import at.ac.ait.lablink.core.connection.messaging.IMessageCallback;
import at.ac.ait.lablink.core.connection.messaging.MsgHeader;
import at.ac.ait.lablink.core.connection.rpc.IRpcRequester;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;
import at.ac.ait.lablink.core.connection.rpc.reply.IRpcReplyCallback;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestCallback;
import at.ac.ait.lablink.core.connection.topic.MsgSubject;
import at.ac.ait.lablink.core.connection.topic.MsgSubscription;
import at.ac.ait.lablink.core.connection.topic.RpcDestination;
import at.ac.ait.lablink.core.connection.topic.RpcDestination.ERpcDestinationChooser;
import at.ac.ait.lablink.core.connection.topic.RpcSubject;
import at.ac.ait.lablink.core.payloads.ErrorMessage;
import at.ac.ait.lablink.core.payloads.StatusMessage;
import at.ac.ait.lablink.core.service.sync.ELlClientState;
import at.ac.ait.lablink.core.service.sync.ELlSimulationMode;
import at.ac.ait.lablink.core.service.sync.ELlSyncHostState;
import at.ac.ait.lablink.core.service.sync.ISyncHostNotifier;
import at.ac.ait.lablink.core.service.sync.ISyncHostService;
import at.ac.ait.lablink.core.service.sync.SyncConfig;
import at.ac.ait.lablink.core.service.sync.SyncParticipant;
import at.ac.ait.lablink.core.service.sync.ex.SyncServiceRuntimeException;
import at.ac.ait.lablink.core.service.sync.payloads.SyncClientConfigMessage;
import at.ac.ait.lablink.core.service.sync.payloads.SyncGoReply;
import at.ac.ait.lablink.core.service.sync.payloads.SyncGoRequest;
import at.ac.ait.lablink.core.service.sync.payloads.SyncParamMessage;
import at.ac.ait.lablink.core.utility.Utility;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class SyncHostServiceImpl implements ISyncHostService {

  private static Logger logger = LoggerFactory.getLogger(SyncHostServiceImpl.class);

  private long syncHelloInterval = 3000;
  private long simStepAdditionalWait = -1;
  private long simInitAdditionalWait = -1;
  private long syncRequestTimeout = 30000;

  private Map<SyncParticipant, ELlClientState>
      participants =
      new ConcurrentHashMap<SyncParticipant, ELlClientState>();

  private SyncConfig syncConfig;

  private ILlConnection lablinkConnection;
  private Configuration lablinkCfg;

  private IRpcRequester initRequester;
  private IRpcRequester goRequester;
  private IRpcRequester stopRequester;

  private long simActualTime;
  private String currentUniqueScenarioId = "undefined";
  private ELlSyncHostState hostState = ELlSyncHostState.STOPPED;


  private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  ScheduledFuture executorFuture;
  private CountDownLatch participantWaitLatch;
  private final Object hostStateMonitor = new Object();

  private static RpcSubject
      registeringClientRpcSubject =
      RpcSubject.getBuilder().addSubjectElement("sync").addSubjectElement("register").build();
  private String scenarioIdentifier;

  private ISyncHostNotifier syncHostNotifier = null;

  /**
   * Constructor.
   *
   * @param lablinkConnection Lablink connection object that will be used by the service.
   * @param config            Configuration that will be used by the service.
   */
  public SyncHostServiceImpl(ILlConnection lablinkConnection, Configuration config) {

    if (config == null) {
      logger.info("No configuration set. Use default values");
      config = new BaseConfiguration();
    }

    logger.info("Initializing ISyncHostService data...");
    lablinkCfg = config;

    syncRequestTimeout = config.getLong("syncHost.syncRequestTimeout", syncRequestTimeout);
    syncHelloInterval = config.getLong("syncHost.helloInterval", syncHelloInterval);
    simStepAdditionalWait = config.getLong("syncHost.simStepAdditionalWait", simStepAdditionalWait);
    simInitAdditionalWait = config.getLong("syncHost.simInitAdditionalWait", simInitAdditionalWait);

    this.lablinkConnection = lablinkConnection;

    lablinkConnection.registerEncodeableFactory(SyncGoRequest.class);
    lablinkConnection.registerEncodeableFactory(SyncGoReply.class);
    lablinkConnection.registerEncodeableFactory(SyncParamMessage.class);
    lablinkConnection.registerEncodeableFactory(SyncClientConfigMessage.class);
    registerHandlers();
  }

  @Override
  public void start() {

    if (!(hostState == ELlSyncHostState.STOPPED || hostState == ELlSyncHostState.ERROR)) {
      throw new SyncServiceRuntimeException(
          "Host can't be initialized. It is already running a simulation.");
    }

    if (syncConfig == null) {
      logger.warn("Init Method wasn't called. Use default scenario for simulation");
      init("default");
    }

    setHostState(ELlSyncHostState.INIT);
  }


  @Override
  public void init(String simulationConfig) {

    this.scenarioIdentifier = simulationConfig;

    if (!(hostState == ELlSyncHostState.STOPPED || hostState == ELlSyncHostState.ERROR)) {
      throw new SyncServiceRuntimeException(
          "Host can't be initialized. It is already running a simulation.");
    }

    String syncCfgStr = lablinkCfg.getString("syncHost.syncScenarioFile." + simulationConfig);

    if (syncCfgStr == null) {
      throw new SyncServiceRuntimeException(
          "Can't read scenario '" + simulationConfig + "' from configuration.");
    }
    logger.info("Load scenario '{}' with sync config file: {}", simulationConfig, syncCfgStr);
    syncConfig = SyncConfig.readSyncConfigFromFile(syncCfgStr);

  }


  @Override
  public void shutdown() {
    logger.info("Shutdown");

    setHostState(ELlSyncHostState.STOPPED);

  }

  private void setHostState(ELlSyncHostState hostState) {

    synchronized (hostStateMonitor) {

      if (this.hostState == hostState) {
        return;
      }

      this.hostState = hostState;

      logger.info("Change SyncHost State to {}", this.hostState);

      //publish update state change

      if (this.hostState == ELlSyncHostState.STOPPED || this.hostState == ELlSyncHostState.ERROR) {
        stopCurrentRunner();
        participants.clear();
        publishCloseMessage();
        scenarioIdentifier = null;
        syncConfig = null;
        currentUniqueScenarioId = "undefined";
      }

      if (this.hostState == ELlSyncHostState.INIT) {
        stopCurrentRunner();

        participants.clear();

        startRunner(new ClientsWaitingRunner(), "ClientsWaitingRunner");
      }

      if (this.hostState == ELlSyncHostState.SIMULATING) {
        currentUniqueScenarioId =
            scenarioIdentifier + "_" + Utility.unixToIdentifierStr(System.currentTimeMillis());
        stopCurrentRunner();
        startRunner(new SimulationRunner(), "SimulationRunner");
      }
    }

    if (syncHostNotifier != null) {
      syncHostNotifier.stateChanged(this.hostState);
    }
  }

  private void startRunner(Runnable runnable, String name) {
    executorFuture = executor.schedule(runnable, 0, TimeUnit.MILLISECONDS);
  }

  private void stopCurrentRunner() {
    if (executorFuture != null) {
      executorFuture.cancel(true);
    }
  }


  private void registerHandlers() {

    logger.info("Registering necessary sync host handlers...");

    // Register HelloMsg Handler
    MsgSubscription
        helloMsgSub =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectElement("sync").addSubjectElement("hello").build();
    lablinkConnection.registerMessageHandler(helloMsgSub, new HelloMsgHandler());

    // Register Client RequestHandler
    lablinkConnection.registerRequestHandler(SyncHostServiceImpl.registeringClientRpcSubject,
        new RegisterClientRequestHandler());

    // Init RPC Reply Handler
    RpcSubject
        initSubject =
        RpcSubject.getBuilder().addSubjectElement("sync").addSubjectElement("init").build();
    initRequester = lablinkConnection.registerReplyHandler(initSubject, new SyncSimInitReply());

    // Go RPC Reply Handler
    RpcSubject
        goSub =
        RpcSubject.getBuilder().addSubjectElement("sync").addSubjectElement("go").build();
    goRequester = lablinkConnection.registerReplyHandler(goSub, new SyncSimGoReply());

    // STOP RPC Reply Handler
    RpcSubject
        stopSub =
        RpcSubject.getBuilder().addSubjectElement("sync").addSubjectElement("stop").build();
    stopRequester = lablinkConnection.registerReplyHandler(stopSub, new SyncSimStopReply());
  }


  private class ClientsWaitingRunner implements Runnable {

    @Override
    public void run() {

      MsgSubject
          helloSubject =
          MsgSubject.getBuilder().addSubjectElement("sync").addSubjectElement("hello").build();

      while (hostState == ELlSyncHostState.INIT) {
        lablinkConnection
            .publishMessage(helloSubject, new StatusMessage(StatusMessage.StatusCode.OK));

        try {
          Thread.sleep(syncHelloInterval);
        } catch (InterruptedException ex) {
          return;
        }

        boolean allParticipantsAvailable = true;

        for (SyncParticipant neededParticipant : syncConfig.getNeededClients()) {
          if (!participants.containsKey(neededParticipant)) {
            allParticipantsAvailable = false;
            logger.warn("Needed sync participant {} {} didn't answer yet. "
                + "Trying a new check in {} seconds ...",
                neededParticipant.getGroupName(), 
                neededParticipant.getClientName(), 
                syncHelloInterval
            );
          }
        }
        if (allParticipantsAvailable) {
          setHostState(ELlSyncHostState.SIMULATING);
          break;
        }

      }
    }
  }

  private class SimulationRunner implements Runnable {

    @Override
    public void run() {

      participantWaitLatch = new CountDownLatch(participants.size());
      sendInitRequests();

      try {
        participantWaitLatch.await(syncRequestTimeout, TimeUnit.MILLISECONDS);
      } catch (InterruptedException ex) {
        return;
      }
      checkParticipantsInSimulation();

      if (simInitAdditionalWait > 0) {
        try {
          logger.info("Waiting for {} ms after initialization phase", simInitAdditionalWait);
          Thread.sleep(simInitAdditionalWait);
        } catch (InterruptedException ex) {
          return;
        }
      }

      if (logger.isInfoEnabled()) {
        logger.info("Starting " + syncConfig.getSimMode() + ": " + Utility
            .unixToDateStr(syncConfig.getSimBeginTime()) + "-" + Utility
            .unixToDateStr(syncConfig.getSimEndTime()) + ", Step: " + syncConfig.getSimStepSize()
            + " ms; Scale: " + syncConfig.getSimScaleFactor() + "!");
      }

      simActualTime = syncConfig.getSimBeginTime();

      //WHILE GO
      while (hostState == ELlSyncHostState.SIMULATING) {

        long stepStartTime = System.currentTimeMillis();

        if (simActualTime >= syncConfig.getSimEndTime()) {
          logger.info("Finished Simulation");
          break;
        }

        long nextUntil;

        if (syncConfig.getSimMode() == ELlSimulationMode.SIMULATION) {
          nextUntil = syncConfig.getSimStepSize();
        } else {
          nextUntil = syncConfig.getSimStepSize() * syncConfig.getSimScaleFactor();
        }
        nextUntil += simActualTime;

        if (logger.isDebugEnabled()) {
          logger.debug("Processing {}-step (until {}).", syncConfig.getSimMode(),
              Utility.unixToDateStr(nextUntil));
        }

        participantWaitLatch = new CountDownLatch(participants.size());

        syncConfig.updateSyncParameter(simActualTime);

        // send GO as broadcast RPC requests
        sendGoRequests(simActualTime, nextUntil,
            syncConfig.getSimMode() == ELlSimulationMode.SIMULATION ? syncRequestTimeout
                : (long) ((syncConfig.getSimStepSize()) * 0.9));

        try {
          if (syncConfig.getSimMode() == ELlSimulationMode.EMULATION) {
            Thread.sleep(
                (syncConfig.getSimStepSize()) - (System.currentTimeMillis() - stepStartTime));
          } else {
            participantWaitLatch.await(syncRequestTimeout, TimeUnit.MILLISECONDS);
            if (simStepAdditionalWait > 0) {
              Thread.sleep(simStepAdditionalWait);
            }
          }
        } catch (InterruptedException ex) {
          break;
        }

        checkParticipantsInSimulation();

        simActualTime = nextUntil;
      }

      //STOP
      participantWaitLatch = new CountDownLatch(participants.size());
      sendStopRequests();

      try {
        participantWaitLatch.await(syncRequestTimeout, TimeUnit.MILLISECONDS);
      } catch (InterruptedException ex) {
        return;
      }
      checkParticipantsInSimulation();

      setHostState(ELlSyncHostState.STOPPED);
    }

    protected void checkParticipantsInSimulation() {
      for (Map.Entry<SyncParticipant, ELlClientState> participant : participants
          .entrySet()) {
        if (participant.getValue() != ELlClientState.SIMULATING) {
          logger.warn("Participant {} {} not finished processing sync step.",
              participant.getKey().getGroupName(), participant.getKey().getClientName());
        }
      }
    }
  }

  private void publishCloseMessage() {
    logger.debug("Send close message");
    MsgSubject
        closeSubject =
        MsgSubject.getBuilder().addSubjectElement("sync").addSubjectElement("close").build();

    lablinkConnection.publishMessage(closeSubject, new StatusMessage(StatusMessage.StatusCode.OK));
  }

  private void sendInitRequests() {
    logger.debug("Sending individual init-requests to clients.");

    for (Map.Entry<SyncParticipant, ELlClientState> participant : participants
        .entrySet()) {

      RpcDestination initRequestDestination;
      initRequestDestination =
          RpcDestination.getBuilder(ERpcDestinationChooser.SEND_TO_CLIENT)
              .setGroupId(participant.getKey().getGroupName())
              .setClientId(participant.getKey().getClientName()).build();
      String clientConfig = syncConfig.getClientCfgsJson(participant.getKey());

      IPayload
          syncParamMessage =
          new SyncParamMessage(currentUniqueScenarioId, syncConfig.getSimMode(),
              syncConfig.getSimBeginTime(), syncConfig.getSimEndTime(),
              syncConfig.getSimScaleFactor(), syncConfig.getSimStepSize());
      IPayload clientConfigMessage = new SyncClientConfigMessage(clientConfig);

      List<IPayload> pl = Arrays.asList(syncParamMessage, clientConfigMessage);

      logger.debug("Sending INIT request to " + participant + " with config: {}", syncParamMessage);
      participant.setValue(ELlClientState.WAITING_FOR_INIT_REPLY);
      initRequester.sendRequest(initRequestDestination, pl, 1, syncRequestTimeout);
    }
  }

  private void sendGoRequests(long actualSimTime, long until, long timeout) {
    if (logger.isDebugEnabled()) {
      logger.debug("Sending go requests to clients. {}", Utility.unixToDateStr(actualSimTime));
    }
    RpcDestination
        goRpcDestination =
        RpcDestination.getBuilder(ERpcDestinationChooser.SEND_TO_ALL).build();
    IPayload syncGoRequest = new SyncGoRequest(actualSimTime, until);
    IPayload
        syncParamMessage =
        new SyncParamMessage(currentUniqueScenarioId, syncConfig.getSimMode(),
            syncConfig.getSimBeginTime(), syncConfig.getSimEndTime(),
            syncConfig.getSimScaleFactor(), syncConfig.getSimStepSize());
    List<IPayload> payloads = Arrays.asList(syncGoRequest, syncParamMessage);

    for (Map.Entry<SyncParticipant, ELlClientState> participant : participants
        .entrySet()) {
      participant.setValue(ELlClientState.WAITING_FOR_GO_REPLY);
    }
    goRequester.sendRequest(goRpcDestination, syncGoRequest, participants.size(), timeout);
  }

  private void sendStopRequests() {
    logger.debug("Sending stop-requests to clients.");

    RpcDestination
        stopRpcDestination =
        RpcDestination.getBuilder(ERpcDestinationChooser.SEND_TO_ALL).build();
    IPayload syncStopRequest = new StatusMessage(StatusMessage.StatusCode.OK);

    for (Map.Entry<SyncParticipant, ELlClientState> participant : participants
        .entrySet()) {
      participant.setValue(ELlClientState.WAITING_FOR_STOP_REPLY);
    }
    stopRequester
        .sendRequest(stopRpcDestination, syncStopRequest, participants.size(), syncRequestTimeout);
  }


  private abstract class SyncSimulationRpcReply implements IRpcReplyCallback {

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {
      logger.error("Received SyncRPC Error from [{} {}]. Header: {} " + "Errors: {}",
          header.getSourceGroupId(), header.getSourceClientId(), header, errors);

      setHostState(ELlSyncHostState.ERROR);
    }

    @Override
    public void handleReply(RpcHeader header, List<IPayload> payloads) {
      SyncParticipant
          sp =
          new SyncParticipant(header.getSourceGroupId(), header.getSourceClientId());

      logger.debug("Received SyncSimulation reply  from {}!", sp);
      participants.put(sp, ELlClientState.SIMULATING);
      participantWaitLatch.countDown();
    }
  }

  private class SyncSimInitReply extends SyncSimulationRpcReply {

    @Override
    public void handleReply(RpcHeader header, List<IPayload> payloads) {
      super.handleReply(header, payloads);
    }
  }


  private class SyncSimGoReply extends SyncSimulationRpcReply {

    @Override
    public void handleReply(RpcHeader header, List<IPayload> payloads) {
      super.handleReply(header, payloads);
    }
  }

  private class SyncSimStopReply extends SyncSimulationRpcReply {

    @Override
    public void handleReply(RpcHeader header, List<IPayload> payloads) {
      super.handleReply(header, payloads);
    }
  }


  private class HelloMsgHandler implements IMessageCallback {

    @Override
    public void handleMessage(MsgHeader header, List<IPayload> payloads) throws Exception {
      String myGroupId = lablinkConnection.getClientIdentifier().getGroupId();
      String myClientId = lablinkConnection.getClientIdentifier().getClientId();

      if (!(header.getSourceGroupId().equals(myGroupId) && header.getSourceClientId()
          .equals(myClientId))) {
        //The received message is from another SyncHost within the app.

        IRpcRequester
            localRegisteringRequester =
            lablinkConnection.registerReplyHandler(registeringClientRpcSubject,
                new RegisteringClientReplyHandler());
        //Inform him about another available SyncHost
        IPayload
            errorMsg =
            new StatusMessage(StatusMessage.StatusCode.NOK,
                "A SyncHost is already available within the application.");
        RpcDestination
            anotherSyncHost =
            RpcDestination.getBuilder(ERpcDestinationChooser.SEND_TO_CLIENT)
                .setGroupId(header.getSourceGroupId()).setClientId(header.getSourceClientId())
                .build();
        localRegisteringRequester.sendRequest(anotherSyncHost, errorMsg);

      }
    }

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {
      logger.warn("HelloMsgHandler received error from {},{}: {}", header.getSourceGroupId(),
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
          logger.warn("Another SyncHost  {} {} accepted the existence of this SyncHost.",
              header.getSourceGroupId(), header.getSourceClientId());
        }
      }

    }

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {
      logger.warn("RegisterClientNokReplyHandler received error from {},{}: {}",
          header.getSourceGroupId(), header.getSourceClientId(), errors);
    }
  }


  private class RegisterClientRequestHandler implements IRpcRequestCallback {

    @Override
    public List<IPayload> handleRequest(RpcHeader header, List<IPayload> payloads) {
      if (payloads.size() < 1) {
        logger.warn(
            "Received payloads of RegisteringClientReplyHandler contains too less payloads objects"
                + ".");
        return Collections.singletonList(
            (IPayload) new ErrorMessage(ErrorMessage.EErrorCode.EMPTY_PAYLOAD,
                "The requests contains not the required payloads"));
      }

      if (payloads.get(0) instanceof StatusMessage) {
        StatusMessage statusMessage = (StatusMessage) payloads.get(0);

        if (statusMessage.getStatusCode() == StatusMessage.StatusCode.NOK) {
          logger.warn("A not acknowledged hostState message was received from  {} {}: {}",
              header.getSourceGroupId(), header.getSourceClientId(), statusMessage.getMessage());
          logger.warn("Changing into STOP state");
          //TODO change into STOP state

          return Collections.singletonList((IPayload) new StatusMessage(StatusMessage.StatusCode.OK,
              "Accept the existing of another client. I will shutdown."));
        }
      }

      if (hostState != ELlSyncHostState.INIT) {
        logger.warn(
            "SyncHost isn't in initialization state for a new simulation. Registration ignored."
                + ".");
        return Collections.singletonList(
            (IPayload) new ErrorMessage(ErrorMessage.EErrorCode.SYNC_ERROR,
                "SyncHost not in initialization state."));
      }

      SyncParticipant
          newClient =
          new SyncParticipant(header.getSourceGroupId(), header.getSourceClientId());

      logger.info("New SyncClient is registered: {} {}", newClient.getGroupName(),
          newClient.getClientName());

      if (!participants.containsKey(newClient)) {
        participants.put(newClient, ELlClientState.REGISTERED);
      }
      return Collections.singletonList((IPayload) new StatusMessage(StatusMessage.StatusCode.OK));
    }

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {
      logger.warn("RegisteringClientReplyHandler received error from {},{}: {}",
          header.getSourceGroupId(), header.getSourceClientId(), errors);
    }
  }


  public ELlSyncHostState getHostState() {
    return hostState;
  }

  public Collection<SyncParticipant> getRegisteredClients() {
    return participants.keySet();
  }

  public void setSyncHostNotifier(ISyncHostNotifier syncHostNotifier) {
    this.syncHostNotifier = syncHostNotifier;
  }

}
