//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.messaging.impl;

import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutor;
import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.encoding.encodeables.IPayload;
import at.ac.ait.lablink.core.connection.messaging.IMessageCallback;
import at.ac.ait.lablink.core.connection.messaging.MsgHeader;

import at.ac.ait.lablink.core.payloads.ErrorMessage;

import java.util.List;

/**
 * Special implementation of Callback Executor for handling messages
 *
 * <p>For equality checks the adapter uses the registered callbacks for identification. So
 * it won't be possible to register two MessageCallbackExecutor with the same IMessageCallback for
 * the same message subscription.
 */
public class MessageCallbackExecutor extends CallbackExecutor {

  /* User callback for handling messages */
  private IMessageCallback msgCallback;

  /**
   * Constructor.
   *
   * @param decodedPacket Decoded object of incoming packet.
   * @param errors Occurred errors during dispatching and decoding.
   * @param msgCallback User's callback method that is used for message handling.
   */
  public MessageCallbackExecutor(IEncodeable decodedPacket, List<ErrorMessage> errors,
                                 IMessageCallback msgCallback) {
    super(decodedPacket, errors, msgCallback);

    if (msgCallback == null) {
      throw new NullPointerException("No MsgCallback is set.");
    }
    this.msgCallback = msgCallback;

  }

  @Override
  protected void executeHandleCallback(List<IPayload> payloads) throws Exception {
    msgCallback.handleMessage((MsgHeader) header, payloads);
  }
}
