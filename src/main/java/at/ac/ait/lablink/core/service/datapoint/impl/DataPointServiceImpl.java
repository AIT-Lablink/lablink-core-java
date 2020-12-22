//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.impl;

import at.ac.ait.lablink.core.connection.ILlConnection;
import at.ac.ait.lablink.core.connection.encoding.encodables.Header;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestCallback;
import at.ac.ait.lablink.core.connection.topic.MsgSubject;
import at.ac.ait.lablink.core.connection.topic.RpcSubject;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;
import at.ac.ait.lablink.core.payloads.ErrorMessage;
import at.ac.ait.lablink.core.service.datapoint.DataPointGeneric;
import at.ac.ait.lablink.core.service.datapoint.IDataPoint;
import at.ac.ait.lablink.core.service.datapoint.IDataPointService;
import at.ac.ait.lablink.core.service.datapoint.payloads.BooleanValue;
import at.ac.ait.lablink.core.service.datapoint.payloads.DataPointProperties;
import at.ac.ait.lablink.core.service.datapoint.payloads.DoubleValue;
import at.ac.ait.lablink.core.service.datapoint.payloads.ISimpleValue;
import at.ac.ait.lablink.core.service.datapoint.payloads.LongValue;
import at.ac.ait.lablink.core.service.datapoint.payloads.StringValue;
import at.ac.ait.lablink.core.service.sync.ISyncParameter;
import at.ac.ait.lablink.core.service.sync.consumer.ISyncConsumer;

import org.apache.commons.configuration.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of a DatapointService.
 */
public class DataPointServiceImpl implements IDataPointService {

  private ILlConnection lablinkConnection;

  private DataPointServiceSyncConsumer syncConsumer = new DataPointServiceSyncConsumer();
  /**
   * Additional prefix that identify the service.
   */
  private List<String> prefix;


  private Map<String, DataPointGeneric>
      dataPoints =
      new ConcurrentHashMap<String, DataPointGeneric>();

  /**
   * Constructor.
   *
   * @param lablinkConnection ILlConnection that should be used for the service hosting.
   * @param config            Configuration for the IDataPointService.
   */
  public DataPointServiceImpl(ILlConnection lablinkConnection, Configuration config) {
    this(lablinkConnection, Collections.singletonList("DP"), config);
  }

  private DataPointServiceImpl(ILlConnection lablinkConnection, List<String> prefix,
                               Configuration config) {
    this.lablinkConnection = lablinkConnection;

    lablinkConnection.registerEncodableFactory(DataPointProperties.class);
    lablinkConnection.registerEncodableFactory(StringValue.class);
    lablinkConnection.registerEncodableFactory(LongValue.class);
    lablinkConnection.registerEncodableFactory(DoubleValue.class);
    lablinkConnection.registerEncodableFactory(BooleanValue.class);

    this.prefix = prefix;

    RpcSubject
        subject =
        RpcSubject.getBuilder().addSubjectElement("services").addSubjectElement("datapoints")
            .addSubjectElement("availableDatapoints").build();
    lablinkConnection.registerRequestHandler(subject, new GetAvailableDataPointsRequestHandler());
  }

  @Override
  public void registerDatapoint(DataPointGeneric dataPoint) {

    dataPoint.setDataPointService(this);
    DataPointProperties props = dataPoint.getProps();

    try {
      RpcSubject
          subject =
          RpcSubject.getBuilder().addSubjectElements(prefix).addSubjectElement("requestProperties")
              .addSubjectElements(props.getIdentifier()).build();
      lablinkConnection.registerRequestHandler(subject, dataPoint.getRequestPropertiesCallback());

      subject =
          RpcSubject.getBuilder().addSubjectElements(prefix).addSubjectElement("requestUpdate")
              .addSubjectElements(props.getIdentifier()).build();
      lablinkConnection.registerRequestHandler(subject, dataPoint.getRequestUpdateCallback());

      subject =
          RpcSubject.getBuilder().addSubjectElements(prefix).addSubjectElement("setValue")
              .addSubjectElements(props.getIdentifier()).build();
      lablinkConnection.registerRequestHandler(subject, dataPoint.getSetValueCallback());

      subject =
          RpcSubject.getBuilder().addSubjectElements(prefix)
              .addSubjectElement("statusCheckPingPong").addSubjectElements(props.getIdentifier())
              .build();
      lablinkConnection
          .registerRequestHandler(subject, dataPoint.getStatusCheckerPingPongCallback());

    } catch (LlCoreRuntimeException ex) {
      throw new LlCoreRuntimeException("Can't register Datapoint (" + props.getIdentifier()
          + "). It isn't allowed to register the same identifier twice.", ex);
    }

    dataPoints.put(createDatapointIdentifier(props.getIdentifier()), dataPoint);

  }

  @Override
  public void unregisterDatapoint(DataPointGeneric dataPoint) {
    dataPoint.setDataPointService(null);
    DataPointProperties props = dataPoint.getProps();

    RpcSubject
        subject =
        RpcSubject.getBuilder().addSubjectElements(prefix).addSubjectElement("requestProperties")
            .addSubjectElements(props.getIdentifier()).build();
    lablinkConnection.unregisterRequestHandler(subject, dataPoint.getRequestPropertiesCallback());

    subject =
        RpcSubject.getBuilder().addSubjectElements(prefix).addSubjectElement("requestUpdate")
            .addSubjectElements(props.getIdentifier()).build();
    lablinkConnection.unregisterRequestHandler(subject, dataPoint.getRequestUpdateCallback());

    subject =
        RpcSubject.getBuilder().addSubjectElements(prefix).addSubjectElement("setValue")
            .addSubjectElements(props.getIdentifier()).build();
    lablinkConnection.unregisterRequestHandler(subject, dataPoint.getSetValueCallback());

    subject =
        RpcSubject.getBuilder().addSubjectElements(prefix).addSubjectElement("statusCheckPingPong")
            .addSubjectElements(props.getIdentifier()).build();
    lablinkConnection
        .unregisterRequestHandler(subject, dataPoint.getStatusCheckerPingPongCallback());

    dataPoints.remove(createDatapointIdentifier(props.getIdentifier()));
  }

  @Override
  public void start() {

  }

  @Override
  public void shutdown() {
    for (DataPointGeneric dataPoint : this.dataPoints.values()) {
      unregisterDatapoint(dataPoint);
    }
  }

  private String createDatapointIdentifier(List<String> identifier) {
    StringBuilder id = new StringBuilder();

    for (String idPart : identifier) {
      id.append(idPart);
    }
    return id.toString();
  }

  public ISyncConsumer getSyncConsumer() {
    return syncConsumer;
  }

  /**
   * Publish a value using the Lablink connection.
   *
   * @param dataPointIdentifier Identifier of the datapoint.
   * @param payload             Value that should be published.
   */
  public void publishValue(List<String> dataPointIdentifier, ISimpleValue payload) {
    MsgSubject
        subject =
        MsgSubject.getBuilder().addSubjectElements(prefix).addSubjectElement("update")
            .addSubjectElements(dataPointIdentifier).build();

    payload.setEmulationTime(syncConsumer.getCurrentSimTime());
    lablinkConnection.publishMessage(subject, (IPayload) payload);
  }

  /**
   * Publish a message over Lablink.
   *
   * @param dataPointIdentifier Identifier of the datapoint.
   * @param command             Commend of the Message.
   * @param payloads            Value that should be published.
   */
  public void publishMessage(List<String> dataPointIdentifier, String command,
                             List<IPayload> payloads) {
    MsgSubject
        subject =
        MsgSubject.getBuilder().addSubjectElements(prefix).addSubjectElement(command)
            .addSubjectElements(dataPointIdentifier).build();

    lablinkConnection.publishMessage(subject, payloads);
  }


  private class DataPointServiceSyncConsumer implements ISyncConsumer {

    private long currentSimTime = -1;

    @Override
    public boolean init(ISyncParameter scs) {
      this.currentSimTime = scs.getSimBeginTime();
      return true;
    }

    @Override
    public long go(long currentSimTime, long until, ISyncParameter scs) {
      this.currentSimTime = currentSimTime;
      return until + scs.getStepSize();
    }

    @Override
    public boolean stop(ISyncParameter scs) {
      this.currentSimTime = -1;
      return true;
    }

    public long getCurrentSimTime() {
      return currentSimTime;
    }
  }


  private class GetAvailableDataPointsRequestHandler implements IRpcRequestCallback {

    @Override
    public List<IPayload> handleRequest(RpcHeader header, List<IPayload> payloads) {

      List<IPayload> returnValues = new ArrayList<IPayload>();

      for (DataPointGeneric dataPoint : dataPoints.values()) {
        returnValues.add(dataPoint.getProps());
      }

      return returnValues;
    }

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {

    }
  }
}
