//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.request.impl;

import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutor;
import at.ac.ait.lablink.core.connection.dispatching.ICallbackExecutorFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestCallback;
import at.ac.ait.lablink.core.payloads.ErrorMessage;

import java.util.List;

/**
 * Implementation of the CallbackExecutor factory for RPC reply callbacks.
 *
 * <p>A factory class for generation Callback executors for RPC reply handling.
 */
public class RpcRequestCallbackExecutorFactory implements ICallbackExecutorFactory {

  /* User callback interface for handling messages. */
  private final IRpcRequestCallback rpcRequestCallback;

  private final RpcReplyPublisher rpcReplyPublisher;

  /**
   * Constructor.
   *
   * @param rpcRequestCallback User defined and provided callback method for handling messages.
   * @param rpcReplyPublisher  Publisher object for publish reply messages
   */
  public RpcRequestCallbackExecutorFactory(IRpcRequestCallback rpcRequestCallback,
                                           RpcReplyPublisher rpcReplyPublisher) {
    if (rpcRequestCallback == null) {
      throw new NullPointerException("No IRpcRequestCallback is set.");
    }
    this.rpcRequestCallback = rpcRequestCallback;

    if (rpcReplyPublisher == null) {
      throw new NullPointerException("No RpcReplyPublisher is set.");
    }
    this.rpcReplyPublisher = rpcReplyPublisher;
  }

  @Override
  public CallbackExecutor createCallbackExecutor(IEncodable decoded, List<ErrorMessage> errors) {
    return new RpcRequestCallbackExecutor(decoded, errors, rpcRequestCallback, rpcReplyPublisher);
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    RpcRequestCallbackExecutorFactory that = (RpcRequestCallbackExecutorFactory) obj;
    return rpcRequestCallback.equals(that.rpcRequestCallback);
  }

  @Override
  public int hashCode() {
    return rpcRequestCallback.hashCode();
  }
}
