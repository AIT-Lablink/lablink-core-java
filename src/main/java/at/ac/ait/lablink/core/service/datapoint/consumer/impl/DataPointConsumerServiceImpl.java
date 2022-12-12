//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.consumer.impl;

import at.ac.ait.lablink.core.connection.ILlConnection;
import at.ac.ait.lablink.core.connection.rpc.IRpcRequester;
import at.ac.ait.lablink.core.connection.topic.MsgSubscription;
import at.ac.ait.lablink.core.connection.topic.RpcSubject;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;
import at.ac.ait.lablink.core.service.datapoint.consumer.DataPointAvailableRequester;
import at.ac.ait.lablink.core.service.datapoint.consumer.DataPointConsumerGeneric;
import at.ac.ait.lablink.core.service.datapoint.consumer.DataPointInfo;
import at.ac.ait.lablink.core.service.datapoint.consumer.EDataPointConsumerState;
import at.ac.ait.lablink.core.service.datapoint.consumer.IDataPointConsumerService;
import at.ac.ait.lablink.core.service.datapoint.ex.DatapointServiceRuntimeException;
import at.ac.ait.lablink.core.service.datapoint.payloads.BooleanValue;
import at.ac.ait.lablink.core.service.datapoint.payloads.ComplexValue;
import at.ac.ait.lablink.core.service.datapoint.payloads.DataPointProperties;
import at.ac.ait.lablink.core.service.datapoint.payloads.DoubleValue;
import at.ac.ait.lablink.core.service.datapoint.payloads.LongValue;
import at.ac.ait.lablink.core.service.datapoint.payloads.StringValue;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Implementation of a datapoint service consumer.
 */
public class DataPointConsumerServiceImpl implements IDataPointConsumerService {

  private static Logger logger = LoggerFactory.getLogger(DataPointConsumerServiceImpl.class);
  private long availableDatapointRequestTimeout;

  private ILlConnection lablinkConnection;

  private List<String> prefix;

  private long connectionCheckRequestIntervalMs = 5000;

  private long connectionChecksPeriodMs = 500;
  private long statusCheckIntervalMs = 30000;
  private long connectionChecksTimeoutMs = 30000;

  private Map<String, DataPointConsumerGeneric>
      consumers =
      new ConcurrentHashMap<String, DataPointConsumerGeneric>();

  /**
   * Constructor
   *
   * @param lablinkConnection ILlConnection that should be used for the service hosting.
   * @param config            Configuration for the IDataPointConsumerService.
   */
  public DataPointConsumerServiceImpl(ILlConnection lablinkConnection, Configuration config) {
    this(lablinkConnection, Collections.singletonList("DP"), config);
  }

  private DataPointConsumerServiceImpl(ILlConnection lablinkConnection, List<String> prefix,
                                       Configuration config) {

    if (config == null) {
      logger.info("No configuration set. Use default values");
      config = new BaseConfiguration();
    }

    connectionCheckRequestIntervalMs =
        config.getLong("datapoint.consumer.connCheckInterval", connectionCheckRequestIntervalMs);
    connectionChecksTimeoutMs =
        config.getLong("datapoint.consumer.connCheckTimeout", connectionChecksTimeoutMs);
    statusCheckIntervalMs =
        config.getLong("datapoint.consumer.statusCheckInterval", statusCheckIntervalMs);

    availableDatapointRequestTimeout =
        config.getLong("datapoint.consumer.availableDatapointRequestTimeout", 10000);

    this.lablinkConnection = lablinkConnection;

    lablinkConnection.registerEncodableFactory(DataPointProperties.class);
    lablinkConnection.registerEncodableFactory(StringValue.class);
    lablinkConnection.registerEncodableFactory(LongValue.class);
    lablinkConnection.registerEncodableFactory(DoubleValue.class);
    lablinkConnection.registerEncodableFactory(BooleanValue.class);
    lablinkConnection.registerEncodableFactory(ComplexValue.class);

    this.prefix = prefix;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void registerDatapointConsumer(DataPointConsumerGeneric dataPoint) {

    List<String> identifier = dataPoint.getIdentifier();
    String remoteClient = dataPoint.getRemoteClient();
    String remoteGroup = dataPoint.getRemoteGroup();

    try {
      RpcSubject
          subject =
          RpcSubject.getBuilder().addSubjectElements(prefix).addSubjectElement("requestProperties")
              .addSubjectElements(identifier).build();
      IRpcRequester
          requester =
          lablinkConnection
              .registerReplyHandler(subject, dataPoint.getRequestPropertiesReplyCallback());
      dataPoint.setPropertiesRequester(requester);

      subject =
          RpcSubject.getBuilder().addSubjectElements(prefix).addSubjectElement("requestUpdate")
              .addSubjectElements(identifier).build();
      requester =
          lablinkConnection
              .registerReplyHandler(subject, dataPoint.getRequestUpdateReplyCallback());
      dataPoint.setUpdateRequester(requester);

      subject =
          RpcSubject.getBuilder().addSubjectElements(prefix).addSubjectElement("setValue")
              .addSubjectElements(identifier).build();
      requester =
          lablinkConnection.registerReplyHandler(subject, dataPoint.getSetValueReplyCallback());
      dataPoint.setSetValueRequester(requester);

      subject =
          RpcSubject.getBuilder().addSubjectElements(prefix)
              .addSubjectElement("statusCheckPingPong").addSubjectElements(identifier).build();
      requester =
          lablinkConnection
              .registerReplyHandler(subject, dataPoint.getStatusCheckerPingPongReplyCallback());
      dataPoint.setStatusCheckerRequester(requester);

      MsgSubscription
          subscription =
          MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_CLIENT)
              .setSrcGroupId(remoteGroup).setSrcClientId(remoteClient).addSubjectElements(prefix)
              .addSubjectElement("update").addSubjectElements(identifier).build();
      lablinkConnection.registerMessageHandler(subscription, dataPoint.getValueUpdateMsgCallback());

      subscription =
          MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_CLIENT)
              .setSrcGroupId(remoteGroup).setSrcClientId(remoteClient).addSubjectElements(prefix)
              .addSubjectElement("statusOk").addSubjectElements(identifier).build();
      lablinkConnection
          .registerMessageHandler(subscription, dataPoint.getStatusOkUpdateMsgCallback());


    } catch (LlCoreRuntimeException ex) {
      throw new LlCoreRuntimeException("Can't register Datapoint (" + identifier
          + "). It isn't allowed to register the same identifier twice.", ex);
    }

    dataPoint.setConnectionCheckInterval(connectionCheckRequestIntervalMs);
    dataPoint.setStatusCheckInterval(statusCheckIntervalMs);
    dataPoint.setDataPointService(this);

    consumers
        .put(createDatapointConsumerIdentifier(remoteGroup, remoteClient, identifier), dataPoint);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void unregisterDatapointConsumer(DataPointConsumerGeneric dataPoint) {

    List<String> identifier = dataPoint.getIdentifier();
    String remoteClient = dataPoint.getRemoteClient();
    String remoteGroup = dataPoint.getRemoteGroup();

    try {
      dataPoint.setPropertiesRequester(null);
      dataPoint.setUpdateRequester(null);
      dataPoint.setSetValueRequester(null);
      dataPoint.setDataPointService(null);
      dataPoint.setStatusCheckerRequester(null);
      MsgSubscription
          subscription =
          MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_CLIENT)
              .setSrcGroupId(remoteGroup).setSrcClientId(remoteClient).addSubjectElements(prefix)
              .addSubjectElement("update").addSubjectElements(identifier).build();
      lablinkConnection
          .unregisterMessageHandler(subscription, dataPoint.getValueUpdateMsgCallback());

      subscription =
          MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_CLIENT)
              .setSrcGroupId(remoteGroup).setSrcClientId(remoteClient).addSubjectElements(prefix)
              .addSubjectElement("statusOk").addSubjectElements(identifier).build();
      lablinkConnection
          .unregisterMessageHandler(subscription, dataPoint.getStatusOkUpdateMsgCallback());

    } catch (LlCoreRuntimeException ex) {
      throw new LlCoreRuntimeException("Can't register Datapoint (" + identifier
          + "). It isn't allowed to register the same identifier twice.", ex);
    }

    consumers.remove(createDatapointConsumerIdentifier(remoteGroup, remoteClient, identifier));
  }

  private String createDatapointConsumerIdentifier(String group, String client,
                                                   List<String> identifier) {
    StringBuilder id = new StringBuilder(group + client);
    for (String idPart : identifier) {
      id.append(idPart);
    }
    return id.toString();
  }

  @Override
  public void waitForAllDatapointConnections() {
    long absoluteTimeout = System.currentTimeMillis() + this.connectionChecksTimeoutMs;
    logger.debug("Wait for datapoint consumers ({} ms)", this.connectionChecksTimeoutMs);
    try {

      while (System.currentTimeMillis() < absoluteTimeout) {

        boolean allConnected = true;
        for (DataPointConsumerGeneric consumer : consumers.values()) {
          allConnected &= (consumer.getState() == EDataPointConsumerState.CONNECTED);
        }
        if (allConnected) {
          return;
        }
        Thread.sleep(this.connectionChecksPeriodMs);
      }
      //Timeout exceeds
      if (logger.isWarnEnabled()) {
        for (DataPointConsumerGeneric consumer : consumers.values()) {
          if (consumer.getState() != EDataPointConsumerState.CONNECTED) {
            logger.warn("DatapointConsumer {} {} {} isn't connected.", consumer.getRemoteGroup(),
                consumer.getRemoteClient(), consumer.getIdentifier());
          }
        }
      }
      throw new DatapointServiceRuntimeException(
          "Not all datapoint consumers are connected to their datapoints");
    } catch (InterruptedException ex) {
      // expected
    }
  }

  @Override
  public void start() {

  }

  @Override
  public void shutdown() {
    for (DataPointConsumerGeneric consumer : this.consumers.values()) {
      unregisterDatapointConsumer(consumer);
    }
  }

  @Override
  public List<DataPointInfo> getAvailableDataPoints() {
    DataPointAvailableRequester
        requester =
        new DataPointAvailableRequester(lablinkConnection, availableDatapointRequestTimeout);
    return requester.requestDatapoints();
  }

  public boolean isConnected() {
    return lablinkConnection.isConnected();
  }
}
