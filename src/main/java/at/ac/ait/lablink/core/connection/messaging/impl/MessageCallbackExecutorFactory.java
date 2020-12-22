//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.messaging.impl;

import at.ac.ait.lablink.core.connection.dispatching.CallbackExecutor;
import at.ac.ait.lablink.core.connection.dispatching.ICallbackExecutorFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.messaging.IMessageCallback;
import at.ac.ait.lablink.core.connection.messaging.MsgHeader;
import at.ac.ait.lablink.core.payloads.ErrorMessage;

import java.util.List;

/**
 * Implementation of the CallbackExecutor factory for message executors.
 *
 * <p>A factory class for generation Callback executors for message handling.
 */
public class MessageCallbackExecutorFactory implements ICallbackExecutorFactory {

  /* User callback interface for handling messages. */
  private IMessageCallback messageCallback;

  /**
   * Constructor.
   *
   * @param messageCallback User defined and provided callback method for handling messages.
   */
  public MessageCallbackExecutorFactory(IMessageCallback messageCallback) {
    if (messageCallback == null) {
      throw new NullPointerException("No IMessageCallback is set.");
    }
    this.messageCallback = messageCallback;
  }

  @Override
  public CallbackExecutor createCallbackExecutor(IEncodable decoded, List<ErrorMessage> errors) {
    return new MessageCallbackExecutor(decoded, errors, messageCallback);
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    MessageCallbackExecutorFactory that = (MessageCallbackExecutorFactory) obj;
    return messageCallback.equals(that.messageCallback);
  }

  @Override
  public int hashCode() {
    return messageCallback.hashCode();
  }
}
