//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.consumer;

import at.ac.ait.lablink.core.connection.encoding.encodables.Header;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.messaging.IMessageCallback;
import at.ac.ait.lablink.core.connection.messaging.MsgHeader;
import at.ac.ait.lablink.core.connection.rpc.IRpcRequester;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;
import at.ac.ait.lablink.core.connection.rpc.reply.IRpcReplyCallback;
import at.ac.ait.lablink.core.connection.topic.RpcDestination;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;
import at.ac.ait.lablink.core.payloads.ErrorMessage;
import at.ac.ait.lablink.core.payloads.StatusMessage;
import at.ac.ait.lablink.core.service.datapoint.ex.DatapointServiceRuntimeException;
import at.ac.ait.lablink.core.service.datapoint.payloads.DataPointProperties;
import at.ac.ait.lablink.core.service.datapoint.payloads.ISimpleValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A generic datapoint consumer.
 */
public class DataPointConsumerGeneric<T> implements IDataPointConsumer<T> {

  private static final Logger logger = LoggerFactory.getLogger(DataPointConsumerGeneric.class);

  private String remoteGroup;
  private String remoteClient;
  private List<String> identifier;


  private ISimpleValue<T> lastValue;
  private ISimpleValue<T> setValue;
  private DataPointProperties props = new DataPointProperties();
  private IDataPointConsumerNotifier<T> notifier;

  private EDataPointConsumerState datapointState = EDataPointConsumerState.NOT_REGISTERED;
  private IDataPointConsumerService publisher;


  private RpcDestination remoteClientDestination;
  private IRpcRequester propsRequester;
  private IRpcRequester updateRequester;
  private IRpcRequester setValueRequester;
  private IRpcRequester statusCheckerRequester;

  private RequestPropertiesReplyCallback
      requestPropertiesReplyCallback =
      new RequestPropertiesReplyCallback();
  private StatusCheckerReplyCallback statusCheckerReplyCallback = new StatusCheckerReplyCallback();
  private ValueUpdateMsgCallback valueUpdateMsgCallback = new ValueUpdateMsgCallback();
  private IMessageCallback statusOkUpdateMsgCallback = new StatusOkUpdateMsgCallback();

  private long connectionCheckInterval = 5000;
  private long statusCheckInterval = 30000;

  ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  ScheduledFuture propertiesRequesterFuture;
  ScheduledFuture statusCheckerRequesterFuture;

  private long lastReceivedTimestamp = -1;

  /**
   * Constructor.
   *
   * @param remoteGroup  Group identifier of the remote datapoint
   * @param remoteClient Client identifier of the remote datapoint
   * @param identifier   Datapoint identifier
   * @param setValue     datapoint value that will be used as set value template.
   */
  public DataPointConsumerGeneric(String remoteGroup, String remoteClient, List<String> identifier,
                                  ISimpleValue<T> setValue) {
    this.remoteGroup = remoteGroup;
    this.remoteClient = remoteClient;
    this.identifier = identifier;

    remoteClientDestination =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_CLIENT)
            .setGroupId(this.remoteGroup).setClientId(this.remoteClient).build();

    this.setValue = setValue;
    this.lastValue = setValue;
  }

  /**
   * Set a new datapoint service.
   *
   * @param dataPointService datapoint service
   */
  public void setDataPointService(IDataPointConsumerService dataPointService) {
    this.publisher = dataPointService;

    if (this.publisher == null) {
      this.setDatapointState(EDataPointConsumerState.NOT_REGISTERED);
    } else {
      this.setDatapointState(EDataPointConsumerState.NOT_CONNECTED);
    }
  }

  @Override
  public void setNotifier(IDataPointConsumerNotifier<T> notifier) {
    this.notifier = notifier;
  }

  private synchronized void setDatapointState(EDataPointConsumerState state) {

    EDataPointConsumerState lastState = this.datapointState;
    if (lastState == state) {
      return; //No change
    }

    this.datapointState = state;

    if (this.notifier != null) {
      logger.debug("Datapoint {} status update {}", identifier, state);
      synchronized (notifier) {
        notifier.stateChanged(this);
      }
    }

    if (state == EDataPointConsumerState.NOT_CONNECTED) {
      propertiesRequesterFuture =
          executor.scheduleWithFixedDelay(new PropertyRequestTimerTask(),
              (long) (Math.random() * 2000.0), connectionCheckInterval, TimeUnit.MILLISECONDS);

    } else {
      if (propertiesRequesterFuture != null) {
        propertiesRequesterFuture.cancel(false);
      }
    }

    if (state == EDataPointConsumerState.CONNECTED) {
      statusCheckerRequesterFuture =
          executor.scheduleWithFixedDelay(new StatusCheckRequestTimerTask(),
              (long) (Math.random() * 2000.0), statusCheckInterval, TimeUnit.MILLISECONDS);

    } else {
      if (statusCheckerRequesterFuture != null) {
        statusCheckerRequesterFuture.cancel(false);
      }
    }
  }

  @Override
  public T getValue() {
    checkErrorState();
    checkConnectedState();

    return lastValue.getValue();
  }

  void sendPropertiesRequest() {
    if (propsRequester != null && publisher.isConnected()) {
      propsRequester.sendRequest(remoteClientDestination,
          new StatusMessage(StatusMessage.StatusCode.NO_PAYLOAD));
    }
  }

  void sendUpdateValueRequest() {
    if (publisher.isConnected()) {
      updateRequester.sendRequest(remoteClientDestination,
          new StatusMessage(StatusMessage.StatusCode.NO_PAYLOAD));
    }
  }

  void sendStatusCheckRequest() {
    if (publisher.isConnected()) {
      statusCheckerRequester.sendRequest(remoteClientDestination,
          new StatusMessage(StatusMessage.StatusCode.NO_PAYLOAD));
    }
  }

  void checkConnectedState() {
    if (datapointState != EDataPointConsumerState.CONNECTED) {
      throw new DatapointServiceRuntimeException(
          "DatapointConsumer (" + remoteGroup + " " + remoteClient + ":" + identifier
              + ") isn't connected to the remote datapoint.");
    }
  }


  private void checkErrorState() {
    if (datapointState == EDataPointConsumerState.ERROR) {
      throw new DatapointServiceRuntimeException(
          "DatapointConsumer (" + remoteGroup + " " + remoteClient + ":" + identifier
              + ") has wrong configuration");
    }
  }

  @Override
  public long getTimestamp() {
    checkErrorState();
    checkConnectedState();

    return lastValue.getTime();
  }

  @Override
  public long getEmulationTimestamp() {
    return lastValue.getEmulationTime();
  }

  @Override
  public void setValue(T value) {
    checkErrorState();
    checkConnectedState();

    if (!props.isWriteable()) {
      throw new LlCoreRuntimeException("Value can only be read.");
    }

    if (publisher.isConnected()) {
      this.setValue.setValue(value);
      this.setValue.setTime(System.currentTimeMillis());
      setValueRequester.sendRequest(remoteClientDestination, (IPayload) this.setValue);
    }
  }

  @Override
  public EDataPointConsumerState getState() {
    return this.datapointState;
  }

  @Override
  public DataPointProperties getProps() {
    checkErrorState();
    checkConnectedState();

    return this.props;
  }

  @Override
  public void requestValueUpdate() {
    this.sendUpdateValueRequest();
  }


  private void externalValueUpdate(ISimpleValue<T> value) {
    this.lastValue = value;
    if (this.notifier != null && datapointState == EDataPointConsumerState.CONNECTED) {
      synchronized (notifier) {
        notifier.valueUpdate(this);
      }
    }
  }

  public void setPropertiesRequester(IRpcRequester propertiesRequester) {
    this.propsRequester = propertiesRequester;
  }

  public void setUpdateRequester(IRpcRequester updateRequester) {
    this.updateRequester = updateRequester;
  }

  public void setSetValueRequester(IRpcRequester setValueRequester) {
    this.setValueRequester = setValueRequester;
  }

  public void setStatusCheckerRequester(IRpcRequester statusCheckerRequester) {
    this.statusCheckerRequester = statusCheckerRequester;
  }

  public IMessageCallback getValueUpdateMsgCallback() {
    return valueUpdateMsgCallback;
  }

  public IMessageCallback getStatusOkUpdateMsgCallback() {
    return statusOkUpdateMsgCallback;
  }

  public IRpcReplyCallback getSetValueReplyCallback() {
    return statusCheckerReplyCallback;
  }

  public IRpcReplyCallback getRequestUpdateReplyCallback() {
    return statusCheckerReplyCallback;
  }

  public IRpcReplyCallback getStatusCheckerPingPongReplyCallback() {
    return statusCheckerReplyCallback;
  }

  public IRpcReplyCallback getRequestPropertiesReplyCallback() {
    return requestPropertiesReplyCallback;
  }

  public List<String> getIdentifier() {
    return identifier;
  }

  public String getRemoteClient() {
    return remoteClient;
  }

  public String getRemoteGroup() {
    return remoteGroup;
  }

  public void setConnectionCheckInterval(long connectionCheckInterval) {
    this.connectionCheckInterval = connectionCheckInterval;
  }

  public void setStatusCheckInterval(long statusCheckInterval) {
    this.statusCheckInterval = statusCheckInterval;
  }


  private class RequestPropertiesReplyCallback implements IRpcReplyCallback {

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {

      for (ErrorMessage error : errors) {
        if (error.getErrorCode() != ErrorMessage.EErrorCode.TIMEOUT_ERROR) {
          logger.error("Request Properties message receives error: {} {}", header.toString(),
              error.toString());
        }
      }
    }

    @Override
    public void handleReply(RpcHeader header, List<IPayload> payloads) {
      lastReceivedTimestamp = System.currentTimeMillis();
      if (remoteGroup.equals(header.getSourceGroupId()) && remoteClient
          .equals(header.getSourceClientId())) {
        if (payloads.size() > 0) {
          props = (DataPointProperties) payloads.get(0);
          if (props.getDatapointType() == lastValue.getClass()) {
            setDatapointState(EDataPointConsumerState.INITIALIZING);
            sendUpdateValueRequest();
          } else {
            setDatapointState(EDataPointConsumerState.ERROR);
          }
        }
      }
    }
  }


  private class StatusCheckerReplyCallback implements IRpcReplyCallback {

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {

      for (ErrorMessage error : errors) {
        if (error.getErrorCode() == ErrorMessage.EErrorCode.TIMEOUT_ERROR) {
          setDatapointState(EDataPointConsumerState.NOT_CONNECTED);
        } else {
          logger.error("Status checker message receives error: {} {}", header.toString(),
              error.toString());
        }
      }

    }

    @Override
    public void handleReply(RpcHeader header, List<IPayload> payloads) {
      lastReceivedTimestamp = System.currentTimeMillis();
      StatusMessage status = (StatusMessage) payloads.get(0);
      if (status.getStatusCode() == StatusMessage.StatusCode.OK) {
        setDatapointState(EDataPointConsumerState.CONNECTED);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private class ValueUpdateMsgCallback implements IMessageCallback {

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {
      logger.error("Value Update Msg message receives error: {} {}", header.toString(),
          errors.toString());
    }

    @Override
    public void handleMessage(MsgHeader header, List<IPayload> payloads) throws Exception {
      lastReceivedTimestamp = System.currentTimeMillis();
      if (payloads.isEmpty()) {
        throw new LlCoreRuntimeException("No IPayload set in request.");
      }

      IPayload value = payloads.get(0);
      externalValueUpdate((ISimpleValue<T>) value);
    }
  }

  @SuppressWarnings("unchecked")
  private class StatusOkUpdateMsgCallback implements IMessageCallback {

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {
      logger.error("StatusOk Update Msg message receives error: {} {}", header.toString(),
          errors.toString());
    }

    @Override
    public void handleMessage(MsgHeader header, List<IPayload> payloads) throws Exception {
      lastReceivedTimestamp = System.currentTimeMillis();
    }
  }

  private class PropertyRequestTimerTask implements Runnable {

    @Override
    public void run() {
      logger.debug("PropertyRequestTimer called for Datapoint: {}", identifier);
      if (datapointState != EDataPointConsumerState.NOT_CONNECTED) {
        return;
      }

      if (publisher != null && publisher.isConnected()) {
        sendPropertiesRequest();
      }
    }
  }

  private class StatusCheckRequestTimerTask implements Runnable {

    @Override
    public void run() {
      logger.debug("StatusCheckRequestTimerTask called for Datapoint: {}", identifier);

      if (lastValue != null
          && (System.currentTimeMillis() - lastReceivedTimestamp) > statusCheckInterval) {
        sendStatusCheckRequest();
      }
    }
  }
}
