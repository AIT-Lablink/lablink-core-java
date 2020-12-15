//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.request.impl;

import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutor;
import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.encoding.encodeables.IPayload;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestCallback;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;
import at.ac.ait.lablink.core.payloads.ErrorMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Special implementation of Callback Executor for handling RPC requests.
 */
public class RpcRequestCallbackExecutor extends CallbackExecutor {

  /* User callback for handling messages */
  private IRpcRequestCallback rpcRequestCallback;
  private final RpcReplyPublisher rpcReplyPublisher;

  private List<IPayload> responsePayloads;


  /**
   * Constructor.
   *
   * @param decodedPacket      Decoded object of incoming packet.
   * @param errors             Occurred errors during dispatching and decoding.
   * @param rpcRequestCallback User's callback method that is used for message handling.
   * @param rpcReplyPublisher  Publisher that will be used to send the reply message.
   */
  public RpcRequestCallbackExecutor(IEncodeable decodedPacket, List<ErrorMessage> errors,
                                    IRpcRequestCallback rpcRequestCallback,
                                    RpcReplyPublisher rpcReplyPublisher) {
    super(decodedPacket, errors, rpcRequestCallback);

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
  public void handleCallback() {
    super.handleCallback();
    sendResponse();
  }

  protected void sendResponse() {
    //Send response
    try {
      if (responsePayloads == null) {
        responsePayloads = new ArrayList<IPayload>();
      }
      if (this.errors != null) {
        responsePayloads.addAll(this.errors);
      }
      rpcReplyPublisher.publishResponse((RpcHeader) header, responsePayloads);
    } catch (LlCoreRuntimeException ex) {
      logger.info("Error during sending RPC response", ex);
    }
  }

  @Override
  protected void executeHandleCallback(List<IPayload> payloads) throws Exception {
    this.responsePayloads = rpcRequestCallback.handleRequest((RpcHeader) header, payloads);
  }
}
