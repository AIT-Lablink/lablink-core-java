//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.impl;

import at.ac.ait.lablink.core.connection.ClientIdentifier;
import at.ac.ait.lablink.core.connection.IConnectionHandler;
import at.ac.ait.lablink.core.connection.ILlConnection;
import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutorManager;
import at.ac.ait.lablink.core.connection.dispatching.IRootDispatcher;
import at.ac.ait.lablink.core.connection.dispatching.impl.RootDispatchingTreeNode;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.encoding.encodables.Packet;
import at.ac.ait.lablink.core.connection.encoding.impl.DecoderFactory;
import at.ac.ait.lablink.core.connection.encoding.impl.EncodableFactoryManagerImpl;
import at.ac.ait.lablink.core.connection.encoding.impl.EncoderFactory;
import at.ac.ait.lablink.core.connection.messaging.IMessageCallback;
import at.ac.ait.lablink.core.connection.messaging.IMessagePublishHandler;
import at.ac.ait.lablink.core.connection.messaging.IMessageReceiveHandler;
import at.ac.ait.lablink.core.connection.messaging.MsgHeader;
import at.ac.ait.lablink.core.connection.messaging.impl.MessagePublishHandlerImpl;
import at.ac.ait.lablink.core.connection.messaging.impl.MessageReceiveHandlerImpl;
import at.ac.ait.lablink.core.connection.mqtt.impl.MqttClientSync;
import at.ac.ait.lablink.core.connection.mqtt.impl.MqttUtils;
import at.ac.ait.lablink.core.connection.publishing.PublishingManager;
import at.ac.ait.lablink.core.connection.rpc.IRpcRequester;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;
import at.ac.ait.lablink.core.connection.rpc.impl.RpcRequesterFactory;
import at.ac.ait.lablink.core.connection.rpc.reply.IRpcReplyCallback;
import at.ac.ait.lablink.core.connection.rpc.reply.IRpcReplyHandler;
import at.ac.ait.lablink.core.connection.rpc.reply.impl.RpcReplyHandlerImpl;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestCallback;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestHandler;
import at.ac.ait.lablink.core.connection.rpc.request.impl.RpcReplyPublisher;
import at.ac.ait.lablink.core.connection.rpc.request.impl.RpcRequestHandlerImpl;
import at.ac.ait.lablink.core.connection.topic.MsgSubject;
import at.ac.ait.lablink.core.connection.topic.MsgSubscription;
import at.ac.ait.lablink.core.connection.topic.RpcSubject;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;
import at.ac.ait.lablink.core.payloads.ErrorMessage;
import at.ac.ait.lablink.core.payloads.LogMessage;
import at.ac.ait.lablink.core.payloads.StatusMessage;
import at.ac.ait.lablink.core.payloads.StringMessage;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.Collections;
import java.util.List;

/**
 * LlConnectionController implementation using an MQTT connection.
 */
@SuppressWarnings("FieldCanBeLocal")
public class LlConnectionController implements ILlConnection {

  private final ClientIdentifier clientId;

  private MqttClientSync mqttClient;

  private IConnectionHandler connectionHandler;
  private IMessageReceiveHandler messageReceiveHandler;
  private IMessagePublishHandler messagePublishHandler;
  private IRpcRequestHandler rpcRequestHandler;
  private IRpcReplyHandler rpcReplyHandler;
  private EncodableFactoryManagerImpl encodableFactoryManager;

  private EncoderFactory encoderFactory;
  private DecoderFactory decoderFactory;

  private IRootDispatcher rootDispatchingTreeNode;
  private PublishingManager publishingManager;
  private CallbackExecutorManager callbackExecutorManager;


  /**
   * Constructor with optional configuration object.
   *
   * @param prefix   Prefix of the application using Lablink connection
   * @param appId    App identifier of the core to be connected
   * @param groupId  Group identifier for the client related to
   * @param clientId client identifier of the connected client
   * @param config   Configuration object that is used to parametrize the Lablink connection
   *                 client. Different parameters can be set. If no parameter is set, the client
   *                 will use the default settings.
   */
  public LlConnectionController(List<String> prefix, String appId, String groupId,
                                     String clientId, Configuration config) {

    try {
      for (String element : prefix) {
        MqttUtils.validateTopicElement(element);
      }
      MqttUtils.validateTopicElement(appId);
      MqttUtils.validateTopicElement(groupId);
      MqttUtils.validateTopicElement(clientId);
    } catch (LlCoreRuntimeException ex) {
      throw new LlCoreRuntimeException("False identification parameter submitted. ", ex);
    }

    this.clientId = new ClientIdentifier(prefix, appId, groupId, clientId);
    initMemberClassesAndConnectModules(config);
  }

  /**
   * Construction with automatic configuration file loading.
   *
   * @param prefix         Prefix of the application using Lablink connection
   * @param appId          App identifier of the core to be connected
   * @param groupId        Group identifier for the client related to
   * @param clientId       client identifier of the connected client
   * @param configFileName Filename for the configuration file that should be used to configure
   *                       the connection module.
   * @throws LlCoreRuntimeException if there are errors loading the configuration file.
   */
  public LlConnectionController(List<String> prefix, String appId, String groupId,
                                     String clientId, String configFileName) {
    this(prefix, appId, groupId, clientId,
        LlConnectionController.loadConfigFile(configFileName));
  }

  /**
   * Load a configuration file using the filename.
   *
   * @param configFileName name of the configuration file
   * @return the loaded configuration
   */
  private static Configuration loadConfigFile(String configFileName) {

    Configuration config;
    try {
      config = new PropertiesConfiguration(configFileName);
    } catch (ConfigurationException ex) {
      throw new LlCoreRuntimeException("Can't load configuration file " + configFileName, ex);
    }
    return config;
  }

  private void initMemberClassesAndConnectModules(Configuration config) {
    mqttClient =
        new MqttClientSync(
            clientId.getAppId() + "_" + clientId.getGroupId() + "_" + clientId.getClientId(),
            config);
    connectionHandler = mqttClient;

    encoderFactory = new EncoderFactory(EncoderFactory.EEncoderType.JSON, config);
    decoderFactory = new DecoderFactory(DecoderFactory.EDecoderType.JSON, config);
    //TODO decoder encoder factory register instead of implicit create objects

    encodableFactoryManager = new EncodableFactoryManagerImpl();
    encodableFactoryManager.registerEncodableFactory(Packet.class);
    encodableFactoryManager.registerEncodableFactory(RpcHeader.class);
    encodableFactoryManager.registerEncodableFactory(MsgHeader.class);

    encodableFactoryManager.registerEncodableFactory(ErrorMessage.class);
    encodableFactoryManager.registerEncodableFactory(StatusMessage.class);
    encodableFactoryManager.registerEncodableFactory(LogMessage.class);
    encodableFactoryManager.registerEncodableFactory(StringMessage.class);

    decoderFactory.setEncodableFactoryManager(encodableFactoryManager);

    rootDispatchingTreeNode = new RootDispatchingTreeNode(clientId.getPrefix().get(0));
    rootDispatchingTreeNode.setMqttSubscriber(mqttClient);
    mqttClient.addMqttConnectionListener(rootDispatchingTreeNode);
    mqttClient.setReceiveCallback(rootDispatchingTreeNode);

    publishingManager = new PublishingManager();
    publishingManager.setEncoderFactory(encoderFactory);
    publishingManager.setMqttPublisher(mqttClient);

    callbackExecutorManager = new CallbackExecutorManager(config);

    MessageReceiveHandlerImpl msgHandler = new MessageReceiveHandlerImpl("msg", clientId);
    msgHandler.setDecoderFactory(decoderFactory);
    msgHandler.setRootDispatcher(rootDispatchingTreeNode);
    msgHandler.setCallbackExecutorManager(callbackExecutorManager);
    messageReceiveHandler = msgHandler;

    MessagePublishHandlerImpl msgPubHandler = new MessagePublishHandlerImpl("msg", clientId);
    msgPubHandler.setPublishingManager(publishingManager);
    messagePublishHandler = msgPubHandler;

    RpcReplyPublisher rpcReplyPublisher = new RpcReplyPublisher("rep", clientId);
    rpcReplyPublisher.setPublishingManager(publishingManager);

    RpcRequesterFactory rpcRequesterFactory = new RpcRequesterFactory("req", clientId, config);
    rpcRequesterFactory.setPublishingManager(publishingManager);

    RpcRequestHandlerImpl rpcRequestHandlerImpl = new RpcRequestHandlerImpl("req", clientId);
    rpcRequestHandlerImpl.setDecoderFactory(decoderFactory);
    rpcRequestHandlerImpl.setRpcReplyPublisher(rpcReplyPublisher);
    rpcRequestHandlerImpl.setRootDispatcher(rootDispatchingTreeNode);
    rpcRequestHandlerImpl.setCallbackExecutorManager(callbackExecutorManager);
    rpcRequestHandlerImpl.init();
    rpcRequestHandler = rpcRequestHandlerImpl;

    RpcReplyHandlerImpl rpcReplyHandlerImpl = new RpcReplyHandlerImpl("rep", clientId);
    rpcReplyHandlerImpl.setDecoderFactory(decoderFactory);
    rpcReplyHandlerImpl.setRpcRequesterFactory(rpcRequesterFactory);
    rpcReplyHandlerImpl.setRootDispatcher(rootDispatchingTreeNode);
    rpcReplyHandlerImpl.setCallbackExecutorManager(callbackExecutorManager);
    rpcReplyHandlerImpl.init();
    rpcReplyHandler = rpcReplyHandlerImpl;

  }

  /**
   * Shutdown the Lablink connection module.
   */
  @Override
  public void shutdown() {
    callbackExecutorManager.shutdown();
    mqttClient.shutdown();
  }

  @Override
  public ClientIdentifier getClientIdentifier() {
    return this.clientId;
  }

  @Override
  public void connect() {
    connectionHandler.connect();
  }

  @Override
  public void disconnect() {
    connectionHandler.disconnect();
  }

  @Override
  public boolean isConnected() {
    return connectionHandler.isConnected();
  }

  @Override
  public void publishMessage(MsgSubject msgSubject, IPayload payload) {
    if (payload != null) {
      this.publishMessage(msgSubject, Collections.singletonList(payload));
    } else {
      this.publishMessage(msgSubject, Collections.<IPayload>emptyList());
    }
  }

  @Override
  public void publishMessage(MsgSubject subject, List<IPayload> payloads) {
    messagePublishHandler.publishMessage(subject, payloads);
  }

  @Override
  public void registerMessageHandler(MsgSubscription msgFilter, IMessageCallback callback) {
    messageReceiveHandler.registerMessageHandler(msgFilter, callback);
  }

  @Override
  public void unregisterMessageHandler(MsgSubscription msgFilter, IMessageCallback callback) {
    messageReceiveHandler.unregisterMessageHandler(msgFilter, callback);
  }

  @Override
  public void registerRequestHandler(RpcSubject subject, IRpcRequestCallback callback) {
    rpcRequestHandler.registerRequestHandler(subject, callback);
  }

  @Override
  public void unregisterRequestHandler(RpcSubject subject, IRpcRequestCallback callback) {
    rpcRequestHandler.unregisterRequestHandler(subject, callback);
  }

  @Override
  public IRpcRequester registerReplyHandler(RpcSubject subject, IRpcReplyCallback callback) {
    return rpcReplyHandler.registerReplyHandler(subject, callback);
  }

  @Override
  public void registerEncodableFactory(String type, IEncodableFactory encodableFactory) {
    encodableFactoryManager.registerEncodableFactory(type, encodableFactory);
  }

  @Override
  public void registerEncodableFactory(String type, Class<? extends IEncodable> encodableClass) {
    encodableFactoryManager.registerEncodableFactory(type, encodableClass);
  }

  @Override
  public void registerEncodableFactory(Class<? extends IEncodable> encodableClass) {
    encodableFactoryManager.registerEncodableFactory(encodableClass);
  }

  @Override
  public void unregisterEncodableFactory(String type) {
    encodableFactoryManager.unregisterEncodableFactory(type);
  }

  @Override
  public void unregisterEncodableFactory(Class<? extends IEncodable> encodableClass) {
    encodableFactoryManager.unregisterEncodableFactory(encodableClass);
  }
  
  @Override
  public IEncodable createEncodable(String type) {
    return encodableFactoryManager.createEncodable(type);
  }
}
