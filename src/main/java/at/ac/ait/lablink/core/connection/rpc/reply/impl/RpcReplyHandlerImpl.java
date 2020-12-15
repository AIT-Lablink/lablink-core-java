//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.reply.impl;

import at.ac.ait.lablink.core.connection.ClientIdentifier;
import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutorManager;
import at.ac.ait.lablink.core.connection.dispatching.ICallbackExecutorFactory;
import at.ac.ait.lablink.core.connection.dispatching.IRootDispatcher;
import at.ac.ait.lablink.core.connection.dispatching.impl.DispatcherCallbackImpl;
import at.ac.ait.lablink.core.connection.encoding.impl.DecoderFactory;
import at.ac.ait.lablink.core.connection.rpc.IRpcRequester;
import at.ac.ait.lablink.core.connection.rpc.impl.RpcRequesterFactory;
import at.ac.ait.lablink.core.connection.rpc.reply.IRpcReplyCallback;
import at.ac.ait.lablink.core.connection.rpc.reply.IRpcReplyHandler;
import at.ac.ait.lablink.core.connection.topic.RpcSubject;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler implementation for RPC replies. This handler will be used for registering and
 * organization of RPC replies.
 */
public class RpcReplyHandlerImpl implements IRpcReplyHandler {

  /* IDecoder that is used for the reply handler */
  private DecoderFactory decoderFactory;

  /* Root dispatcher for registering the reply handler */
  private IRootDispatcher rootDispatcher;

  /* Reply dispatcher that is used for specific calls */
  private RpcReplyDispatcher replyDispatcher;

  /* identifier for the reply handler (e.g., rep) */
  private final String dispatcherIdentifier;

  /* identifier of the client */
  private final ClientIdentifier clientId;

  /* Factory class that is used to generate an RPC requester. */
  private RpcRequesterFactory rpcRequesterFactory;

  /* Manager for executing callback threads */
  private CallbackExecutorManager callbackExecutorManager;

  /**
   * Constructor.
   *
   * @param dispatcherIdentifier Identifier of the reply dispatcher (usually "rep").
   * @param clientId             Identifier of the client.
   */
  public RpcReplyHandlerImpl(String dispatcherIdentifier, ClientIdentifier clientId) {
    this.dispatcherIdentifier = dispatcherIdentifier;
    this.clientId = clientId;
  }

  /**
   * Sets a root dispatcher that is used to register the dispatcher.
   *
   * @param rootDispatcher to be set.
   */
  public void setRootDispatcher(IRootDispatcher rootDispatcher) {
    this.rootDispatcher = rootDispatcher;
  }

  public void setCallbackExecutorManager(CallbackExecutorManager callbackExecutorManager) {
    this.callbackExecutorManager = callbackExecutorManager;
  }

  /**
   * Factory manager that creates decoder that is used for callback handling.
   *
   * @param decoderFactory that is used.
   */
  public void setDecoderFactory(DecoderFactory decoderFactory) {
    this.decoderFactory = decoderFactory;
  }

  /**
   * Set the Factory for generating IRpcRequester objects.
   *
   * @param rpcRequesterFactory to be set.
   */
  public void setRpcRequesterFactory(RpcRequesterFactory rpcRequesterFactory) {
    this.rpcRequesterFactory = rpcRequesterFactory;
  }

  /**
   * Init the dispatcher. This method should be called after all internal connections to other
   * objects are injected using the set methods.
   */
  public void init() {
    replyDispatcher = new RpcReplyDispatcher(clientId.getGroupId(), clientId.getClientId());
    List<String> replyDispatcherName = new ArrayList<String>();
    replyDispatcherName.addAll(clientId.getPrefix());
    replyDispatcherName.add(clientId.getAppId());
    replyDispatcherName.add(this.dispatcherIdentifier);
    rootDispatcher.addDispatcher(replyDispatcherName, replyDispatcher);
  }

  /**
   * {@inheritDoc}
   *
   * <p>The registration of a reply handler returns a requester object. The reply handler is
   * registered to this requester. Until this requester lives within the system the reply handler
   * is registered. If the requester will be destroyed by the garbage collector than the reply
   * handler will be unregistered.
   *
   * <p>There can be multiple reply handle be registered to one subject. So it can be happened that
   * two different IRpcRequester are bind to the same topic. A request will be sent be a specific
   * requester and that points to a specific reply handler.
   *
   * @param subject  subject of the request and the corresponding reply handler.
   * @param callback Reply callback that should be registered to the Lablink communication system.
   */
  @Override
  public IRpcRequester registerReplyHandler(RpcSubject subject, IRpcReplyCallback callback) {

    if (subject == null) {
      throw new NullPointerException("Subject during reply handler registration is null.");
    }


    ICallbackExecutorFactory
        callbackExecutorFactory =
        new RpcReplyCallbackExecutorFactory(callback);
    DispatcherCallbackImpl
        cb =
        new DispatcherCallbackImpl(decoderFactory.getDefaultDecoderObject(),
            callbackExecutorFactory);
    cb.setCallbackExecutorManager(callbackExecutorManager);

    return rpcRequesterFactory.createNewRpcRequester(subject,
        cb,
        callback, this.replyDispatcher);
  }
}
