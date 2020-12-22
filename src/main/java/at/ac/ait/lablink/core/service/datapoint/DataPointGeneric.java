//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint;

import at.ac.ait.lablink.core.connection.encoding.encodables.Header;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestCallback;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;
import at.ac.ait.lablink.core.payloads.ErrorMessage;
import at.ac.ait.lablink.core.payloads.StatusMessage;
import at.ac.ait.lablink.core.service.datapoint.IDataPoint;
import at.ac.ait.lablink.core.service.datapoint.IDataPointNotifier;
import at.ac.ait.lablink.core.service.datapoint.payloads.DataPointProperties;
import at.ac.ait.lablink.core.service.datapoint.payloads.ISimpleValue;

import java.util.Collections;
import java.util.List;

/**
 * Generic implementation of a datapoint that will be used in the datapoint service.
 *
 * <p>The datapoint will start the publishing if a consumer requests the getProperties of the
 * datapoint. This indicates that someone is interested in the datapoint.
 */
public class DataPointGeneric<T> implements IDataPoint<T> {

  /**
   * Properties of the datapoint.
   */
  private DataPointProperties props;

  /**
   * Last value of the datapoint.
   */
  private ISimpleValue<T> lastValue;

  /**
   * Notifier callback that will be used to inform the client about a state change.
   */
  private IDataPointNotifier<T> notifier;

  /**
   * Publisher that will be used to publish the Value updates.
   */
  protected IDataPointService publisher;

  /**
   * Handler Callback for the properties RPC request.
   */
  private RequestPropertiesHandler requestPropertiesHandler = new RequestPropertiesHandler();

  /**
   * Handler callback for the value update RPC request.
   */
  private RequestUpdateHandler requestUpdateHandler = new RequestUpdateHandler();

  /**
   * Handler callback for the set value RPC request.
   */
  private SetValueHandler setValueHandler = new SetValueHandler();

  /**
   * Handler callback for status check ping pong.
   */
  private StatusCheckerPingPongHandler
      getStatusCheckerPingPongCallback =
      new StatusCheckerPingPongHandler();

  // TODO publishing should be prohibit if there is no listener, how to detect restarts of provider
  // and already connected listeners
  // currently every change of the datapoint will be published, it is not important if there is a
  // listener. (shouldPublishChange = true)
  private boolean shouldPublishChange = true;

  /**
   * Constructor:
   *
   * @param identifier Identifier of the datapoint.
   * @param name       Friendly name of the datapoint for additional information.
   * @param unit       Unit of the datapoint.
   * @param writeable  Flag if the datapoint should be writeable from the Lablink
   * @param value      default value type for the datapoint.
   */
  public DataPointGeneric(List<String> identifier, String name, String unit, boolean writeable,
                          ISimpleValue<T> value) {
    props = new DataPointProperties(identifier, name, unit, writeable, value.getClass());
    lastValue = value;
    this.notifier = new LocalNotifier();
  }

  @Override
  public void setNotifier(IDataPointNotifier<T> notifier) {
    this.notifier = notifier;
  }


  @Override
  public T getValue() {
    return lastValue.getValue();
  }

  @Override
  public long getTimestamp() {
    return lastValue.getTime();
  }

  @Override
  public void setValue(T value, long timestamp) {
    this.lastValue.setValue(value);
    this.lastValue.setTime(timestamp);
    //TODO implement emulation Timestamp
    this.lastValue.setEmulationTime(timestamp);
    if (shouldPublishChange) {
      publishValue();
    }
  }

  @Override
  public void setValue(T value) {
    this.setValue(value, System.currentTimeMillis());
  }

  /**
   * Publish the current value of the datapoint.
   */
  public void publishValue() {
    if (publisher != null) {
      publisher.publishValue(props.getIdentifier(), lastValue);
    }
  }

  /**
   * Set an external value to the datapoint.
   *
   * <p>This method will be used if a new set value is received from the datapoint
   *
   * @param value new value to be set to the datapointl
   */
  private void setValueExternal(ISimpleValue<T> value) {

    if (!props.isWriteable()) {
      throw new LlCoreRuntimeException(
          "Datapoint " + props.getIdentifier() + " can only be read.");
    }

    if (notifier != null) {
      notifier.valueSetNotifier(this, value);
    }

  }

  /**
   * Request a value update from the host.
   *
   * <p>This method
   */
  protected void requestValueExternal() {
    if (notifier != null) {
      notifier.requestValueUpdate(this);
    }
    publishValue();
  }


  private class LocalNotifier implements IDataPointNotifier<T> {

    @Override
    public void valueSetNotifier(IDataPoint<T> dataPoint, ISimpleValue<T> setValue) {
      dataPoint.setValue(setValue.getValue(), setValue.getTime());
    }

    @Override
    public void requestValueUpdate(IDataPoint<T> dataPoint) {
      publishValue();
    }
  }


  public DataPointProperties getProps() {
    return props;
  }


  public ISimpleValue<T> getLastValue() {
    return lastValue;
  }

  public RequestPropertiesHandler getRequestPropertiesCallback() {
    return requestPropertiesHandler;
  }


  public RequestUpdateHandler getRequestUpdateCallback() {
    return requestUpdateHandler;
  }


  public SetValueHandler getSetValueCallback() {
    return setValueHandler;
  }

  public StatusCheckerPingPongHandler getStatusCheckerPingPongCallback() {
    return getStatusCheckerPingPongCallback;
  }

  public void setDataPointService(IDataPointService dataPointService) {
    this.publisher = dataPointService;
  }


  private class RequestUpdateHandler implements IRpcRequestCallback {

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {
    }

    @Override
    public List<IPayload> handleRequest(RpcHeader header, List<IPayload> payloads) {
      if (payloads.isEmpty()) {
        throw new LlCoreRuntimeException("No IPayload set in request.");
      }
      IPayload value = payloads.get(0);
      if (value instanceof StatusMessage) {
        StatusMessage status = (StatusMessage) value;
        if (status.getStatusCode() != StatusMessage.StatusCode.NO_PAYLOAD) {
          throw new LlCoreRuntimeException("False IPayload detected");
        }
      }
      shouldPublishChange = true;
      requestValueExternal();
      return Collections.singletonList((IPayload) new StatusMessage(StatusMessage.StatusCode.OK));
    }
  }

  @SuppressWarnings("unchecked")
  private class SetValueHandler implements IRpcRequestCallback {

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {
    }

    @Override
    public List<IPayload> handleRequest(RpcHeader header, List<IPayload> payloads) {
      if (payloads.isEmpty()) {
        throw new LlCoreRuntimeException("No IPayload set in request.");
      }
      IPayload value = payloads.get(0);

      setValueExternal((ISimpleValue<T>) value);
      shouldPublishChange = true;
      return Collections.singletonList((IPayload) new StatusMessage(StatusMessage.StatusCode.OK));
    }
  }

  private class RequestPropertiesHandler implements IRpcRequestCallback {

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {
    }

    @Override
    public List<IPayload> handleRequest(RpcHeader header, List<IPayload> payloads) {
      shouldPublishChange = true;
      return Collections.singletonList((IPayload) getProps());
    }
  }

  private class StatusCheckerPingPongHandler implements IRpcRequestCallback {

    @Override
    public List<IPayload> handleRequest(RpcHeader header, List<IPayload> payloads) {
      List<IPayload>
          returnPayloads =
          Collections.singletonList((IPayload) new StatusMessage(StatusMessage.StatusCode.OK));

      if (publisher != null) {
        publisher.publishMessage(props.getIdentifier(), "statusOk", returnPayloads);
      }
      return returnPayloads;
    }

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {

    }
  }
}
