//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.messaging.impl;

import at.ac.ait.lablink.core.connection.ClientIdentifier;
import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutorManager;
import at.ac.ait.lablink.core.connection.dispatching.ICallbackExecutorFactory;
import at.ac.ait.lablink.core.connection.dispatching.IDispatcherInterface;
import at.ac.ait.lablink.core.connection.dispatching.IRootDispatcher;
import at.ac.ait.lablink.core.connection.dispatching.impl.DispatcherCallbackImpl;
import at.ac.ait.lablink.core.connection.dispatching.impl.DispatchingTreeNode;
import at.ac.ait.lablink.core.connection.encoding.impl.DecoderFactory;
import at.ac.ait.lablink.core.connection.messaging.IMessageCallback;
import at.ac.ait.lablink.core.connection.messaging.IMessageReceiveHandler;
import at.ac.ait.lablink.core.connection.mqtt.impl.MqttUtils;
import at.ac.ait.lablink.core.connection.topic.MsgSubscription;
import at.ac.ait.lablink.core.connection.topic.Topic;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementation of handler interface for message handling.
 */
public class MessageReceiveHandlerImpl implements IMessageReceiveHandler {

  private static final Logger logger = LoggerFactory.getLogger(MessageReceiveHandlerImpl.class);

  /* Root dispatcher that is used for adding the dispatcher */
  private IRootDispatcher rootDispatcher;
  private DecoderFactory decoderFactory;

  private final String transmissionIdentifier;
  private final ClientIdentifier clientId;

  /* Manager for executing callback threads */
  private CallbackExecutorManager callbackExecutorManager;

  /**
   * Constructor.
   *
   * @param transmissionIdentifier identifier of the message transmission type (typically "msg")
   * @param clientId               Identifier of the Lablink client
   */
  public MessageReceiveHandlerImpl(String transmissionIdentifier, ClientIdentifier clientId) {
    this.transmissionIdentifier = transmissionIdentifier;
    this.clientId = clientId;
  }

  /**
   * Set the factory manager for creating a decoder for decoding received messages.
   *
   * @param decoderFactory that should be used for decoding incoming packaged.
   */
  public void setDecoderFactory(DecoderFactory decoderFactory) {
    this.decoderFactory = decoderFactory;
  }

  /**
   * Set the root dispatcher that should be used for registering message handlers
   *
   * @param rootDispatcher that should be registered.
   */
  public void setRootDispatcher(IRootDispatcher rootDispatcher) {
    this.rootDispatcher = rootDispatcher;
  }

  @Override
  public void registerMessageHandler(MsgSubscription msgFilter, IMessageCallback callback) {

    Topic msgSubscription = new Topic();
    msgSubscription.setSubject(msgFilter.getSubscriptionSubject());
    msgSubscription.setClientIdentifiers(msgFilter.getSubscriptionGroupId(),
        msgFilter.getSubscriptionClientId());
    msgSubscription.setPrefix(clientId.getPrefix());
    msgSubscription.setApplicationId(clientId.getAppId());
    msgSubscription.setTransmissionType(this.transmissionIdentifier);

    List<String> subscription = msgSubscription.getTopic();
    try {
      MqttUtils.validateMqttSubscription(subscription);
    } catch (LlCoreRuntimeException ex) {
      throw new LlCoreRuntimeException(
          "Error during validation of subscription (" + subscription.toString() + ")", ex);
    }

    ICallbackExecutorFactory callbackExecutorFactory = new MessageCallbackExecutorFactory(callback);
    DispatcherCallbackImpl
        cb =
        new DispatcherCallbackImpl(decoderFactory.getDefaultDecoderObject(),
            callbackExecutorFactory);
    cb.setCallbackExecutorManager(callbackExecutorManager);
    IDispatcherInterface dispatcherNode;

    try {
      if (rootDispatcher.hasDispatcher(subscription)) {
        dispatcherNode = rootDispatcher.getDispatcher(subscription);
        dispatcherNode.addCallback(cb);
      } else {
        dispatcherNode = new DispatchingTreeNode();
        dispatcherNode.addCallback(cb);
        rootDispatcher.addDispatcher(subscription, dispatcherNode);
      }
    } catch (LlCoreRuntimeException ex) {
      throw new LlCoreRuntimeException(
          "Error during registration of a message handler (" + subscription.toString() + ")", ex);
    }

    logger.debug("New message handler is registered under {}", subscription.toString());
  }

  @Override
  public void unregisterMessageHandler(MsgSubscription msgFilter, IMessageCallback callback) {

    Topic msgSubscription = new Topic();
    msgSubscription.setSubject(msgFilter.getSubscriptionSubject());
    msgSubscription.setClientIdentifiers(msgFilter.getSubscriptionGroupId(),
        msgFilter.getSubscriptionClientId());
    msgSubscription.setPrefix(clientId.getPrefix());
    msgSubscription.setApplicationId(clientId.getAppId());
    msgSubscription.setTransmissionType(this.transmissionIdentifier);

    List<String> subscription = msgSubscription.getTopic();

    try {
      MqttUtils.validateMqttSubscription(subscription);
    } catch (LlCoreRuntimeException ex) {
      throw new LlCoreRuntimeException(
          "Error during validation of subscription (" + subscription.toString() + ")", ex);
    }

    if (!rootDispatcher.hasDispatcher(subscription)) {
      logger.debug("No message handler was registered under {}. Deregistration will be aborted.",
          subscription.toString());
      return;
    }
    try {
      IDispatcherInterface dispatcherNode = rootDispatcher.getDispatcher(subscription);

      ICallbackExecutorFactory
          callbackExecutorFactory =
          new MessageCallbackExecutorFactory(callback);
      DispatcherCallbackImpl
          cb =
          new DispatcherCallbackImpl(decoderFactory.getDefaultDecoderObject(),
              callbackExecutorFactory);
      cb.setCallbackExecutorManager(callbackExecutorManager);

      dispatcherNode.removeCallback(cb);
      if (dispatcherNode.canBeRemoved()) {
        rootDispatcher.removeDispatcher(subscription);
      }
    } catch (LlCoreRuntimeException ex) {
      throw new LlCoreRuntimeException(
          "Error during deregistration of a message handler (" + subscription.toString() + ")", ex);
    }

    logger.debug("Message handler was deregistered ({})",subscription.toString());
  }

  public void setCallbackExecutorManager(CallbackExecutorManager callbackExecutorManager) {
    this.callbackExecutorManager = callbackExecutorManager;
  }
}
