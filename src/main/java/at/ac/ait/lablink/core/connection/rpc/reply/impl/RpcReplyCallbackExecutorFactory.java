//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.reply.impl;

import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutor;
import at.ac.ait.lablink.core.connection.dispatching.ICallbackExecutorFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.rpc.reply.IRpcReplyCallback;
import at.ac.ait.lablink.core.payloads.ErrorMessage;

import java.util.List;

/**
 * Implementation of the CallbackExecutor factory for RPC reply callbacks.
 *
 * <p>A factory class for generation Callback executors for RPC reply handling.
 */
public class RpcReplyCallbackExecutorFactory implements ICallbackExecutorFactory {

  /* User callback interface for handling messages. */
  private IRpcReplyCallback rpcReplyCallback;

  /**
   * Constructor.
   *
   * @param rpcReplyCallback User defined and provided callback method for handling messages.
   */
  public RpcReplyCallbackExecutorFactory(IRpcReplyCallback rpcReplyCallback) {
    if (rpcReplyCallback == null) {
      throw new NullPointerException("No RepcReply is set.");
    }
    this.rpcReplyCallback = rpcReplyCallback;
  }

  @Override
  public CallbackExecutor createCallbackExecutor(IEncodeable decoded, List<ErrorMessage> errors) {
    return new RpcReplyCallbackExecutor(decoded, errors, rpcReplyCallback);
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    RpcReplyCallbackExecutorFactory that = (RpcReplyCallbackExecutorFactory) obj;
    return rpcReplyCallback.equals(that.rpcReplyCallback);
  }

  @Override
  public int hashCode() {
    return rpcReplyCallback.hashCode();
  }
}
