//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.reply.impl;

import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutor;
import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.encoding.encodeables.IPayload;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;
import at.ac.ait.lablink.core.connection.rpc.reply.IRpcReplyCallback;
import at.ac.ait.lablink.core.payloads.ErrorMessage;

import java.util.List;

/**
 * Special implementation of Callback Executor for handling messages
 *
 * <p>For equality checks the adapter uses the registered callbacks for identification. So
 * it won't be possible to register two MessageCallbackExecutor with the same IMessageCallback for
 * the same message subscription.
 */
public class RpcReplyCallbackExecutor extends CallbackExecutor {

  /* User callback for handling messages */
  private IRpcReplyCallback rpcReplyCallback;

  /**
   * Constructor.
   *
   * @param decodedPacket Decoded object of incoming packet.
   * @param errors Occurred errors during dispatching and decoding.
   * @param rpcReplyCallback User's callback method that is used for message handling.
   */
  public RpcReplyCallbackExecutor(IEncodeable decodedPacket, List<ErrorMessage> errors,
                                  IRpcReplyCallback rpcReplyCallback) {
    super(decodedPacket, errors, rpcReplyCallback);

    if (rpcReplyCallback == null) {
      throw new NullPointerException("No IRpcReplyCallback is set.");
    }
    this.rpcReplyCallback = rpcReplyCallback;
  }


  @Override
  protected void executeHandleCallback(List<IPayload> payloads) throws Exception {
    rpcReplyCallback.handleReply((RpcHeader) header, payloads);

  }
}
