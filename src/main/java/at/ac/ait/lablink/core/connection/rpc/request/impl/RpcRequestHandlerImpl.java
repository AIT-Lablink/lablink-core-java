//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.request.impl;

import at.ac.ait.lablink.core.connection.ClientIdentifier;
import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutorManager;
import at.ac.ait.lablink.core.connection.dispatching.ICallbackExecutorFactory;
import at.ac.ait.lablink.core.connection.dispatching.IDispatcherInterface;
import at.ac.ait.lablink.core.connection.dispatching.IRootDispatcher;
import at.ac.ait.lablink.core.connection.dispatching.impl.DispatcherCallbackImpl;
import at.ac.ait.lablink.core.connection.dispatching.impl.DispatchingTreeNode;
import at.ac.ait.lablink.core.connection.encoding.impl.DecoderFactory;
import at.ac.ait.lablink.core.connection.mqtt.impl.MqttUtils;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestCallback;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestHandler;
import at.ac.ait.lablink.core.connection.topic.RpcSubject;
import at.ac.ait.lablink.core.connection.topic.Topic;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler implementation for handling RPC request.
 */
public class RpcRequestHandlerImpl implements IRpcRequestHandler {

  private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandlerImpl.class);

  private RpcReplyPublisher rpcReplyPublisher;
  private IRootDispatcher rootDispatcher;
  private DecoderFactory decoderFactory;

  private final String dispatcherIdentifier;
  private final ClientIdentifier clientId;

  /* Manager for executing callback threads */
  private CallbackExecutorManager callbackExecutorManager;

  /**
   * Constructor.
   *
   * @param dispatcherIdentifier Identifier of the Dispatcher (usually "req").
   * @param clientId             identifier of the client.
   */
  public RpcRequestHandlerImpl(String dispatcherIdentifier, ClientIdentifier clientId) {
    this.dispatcherIdentifier = dispatcherIdentifier;
    this.clientId = clientId;
  }

  /**
   * Set the RPC response publisher
   *
   * @param rpcReplyPublisher to be set.
   */
  public void setRpcReplyPublisher(RpcReplyPublisher rpcReplyPublisher) {
    this.rpcReplyPublisher = rpcReplyPublisher;
  }

  /**
   * Set the root dispatcher that is used for registering the RPC request dispatcher.
   *
   * @param rootDispatcher to be set.
   */
  public void setRootDispatcher(IRootDispatcher rootDispatcher) {
    this.rootDispatcher = rootDispatcher;
  }

  /**
   * Factory class that creates the decoder that is used for callback handling.
   *
   * @param decoderFactory that is used.
   */
  public void setDecoderFactory(DecoderFactory decoderFactory) {
    this.decoderFactory = decoderFactory;
  }

  public void setCallbackExecutorManager(CallbackExecutorManager callbackExecutorManager) {
    this.callbackExecutorManager = callbackExecutorManager;
  }

  /**
   * Init the dispatcher. This method should be called after all internal connections to other
   * objects are injected using the set methods.
   */
  public void init() {

    List<String> requestDispatcherName = new ArrayList<String>();
    requestDispatcherName.addAll(clientId.getPrefix());
    requestDispatcherName.add(clientId.getAppId());
    requestDispatcherName.add(this.dispatcherIdentifier);

    RpcRequestDispatcher
        requestDispatcher =
        new RpcRequestDispatcher(clientId.getGroupId(), clientId.getClientId());
    rootDispatcher.addDispatcher(requestDispatcherName, requestDispatcher);
  }

  @Override
  public void registerRequestHandler(RpcSubject subject, IRpcRequestCallback callback) {
    Topic topic = new Topic();
    topic.setSubject(subject.getSubject());
    topic.setPrefix(clientId.getPrefix());
    topic.setClientIdentifiers(clientId.getGroupId(), clientId.getClientId());
    topic.setApplicationId(clientId.getAppId());
    topic.setTransmissionType(this.dispatcherIdentifier);

    IDispatcherInterface dispatcherNode;

    List<String> subscription = topic.getTopic();

    MqttUtils.validateMqttSubscription(subscription);

    ICallbackExecutorFactory
        callbackExecutorFactory =
        new RpcRequestCallbackExecutorFactory(callback, rpcReplyPublisher);
    DispatcherCallbackImpl
        cb =
        new DispatcherCallbackImpl(decoderFactory.getDefaultDecoderObject(),
            callbackExecutorFactory);
    cb.setCallbackExecutorManager(callbackExecutorManager);

    if (rootDispatcher.hasDispatcher(subscription)) {
      throw new LlCoreRuntimeException(
          "RpcRequest Dispatcher already registered. It isn't allowed to add a second one.");
    } else {
      dispatcherNode = new DispatchingTreeNode();
      dispatcherNode.addCallback(cb);
      rootDispatcher.addDispatcher(subscription, dispatcherNode);
    }

    logger.debug("New RPC request handler is registered under {}",subscription.toString());
  }

  @Override
  public void unregisterRequestHandler(RpcSubject subject, IRpcRequestCallback callback) {
    Topic topic = new Topic();
    topic.setSubject(subject.getSubject());

    topic.setPrefix(clientId.getPrefix());
    topic.setClientIdentifiers(clientId.getGroupId(), clientId.getClientId());
    topic.setApplicationId(clientId.getAppId());

    topic.setTransmissionType(this.dispatcherIdentifier);
    List<String> subscription = topic.getTopic();

    MqttUtils.validateMqttSubscription(subscription);

    if (!rootDispatcher.hasDispatcher(subscription)) {
      return;
    }

    IDispatcherInterface dispatcherNode = rootDispatcher.getDispatcher(subscription);

    ICallbackExecutorFactory
        callbackExecutorFactory =
        new RpcRequestCallbackExecutorFactory(callback, rpcReplyPublisher);
    DispatcherCallbackImpl
        cb =
        new DispatcherCallbackImpl(decoderFactory.getDefaultDecoderObject(),
            callbackExecutorFactory);
    cb.setCallbackExecutorManager(callbackExecutorManager);

    dispatcherNode.removeCallback(cb);
    if (dispatcherNode.canBeRemoved()) {
      rootDispatcher.removeDispatcher(subscription);
    }

    logger.debug("RPC request handler was deregistered ({})", subscription.toString());
  }
}
