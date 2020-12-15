//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.messaging;

import at.ac.ait.lablink.core.connection.topic.MsgSubscription;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

/**
 * Interface for handling messages.
 *
 * <p>A message in the Lablink connection module indicates an unidirectional and asynchronous
 * communication channel. A client can send a message to a communication channel. The receiving
 * clients can listen to such an channel. If a message is received at a client it will call a
 * callback method with the received packet.
 */
public interface IMessageReceiveHandler {

  /**
   * Register a handler for receiving messages. The handler can listen to different scenarios
   * defined by the subscribe parameter. If a message received and matches with the handler's
   * search pattern, than the handler implementation will be called.
   *
   * <p>The handler will block the receiving queue until the message is processed. So heavy
   * calculations should be implemented outside of the handler method to prevent blocking the
   * reception of new messages
   *
   * @param msgFilter subscribe pattern, which the handler listens to. It can be listen to
   *                  all received messages, to all messages of a specific client or
   *                  group, to a specific message of all clients or groups
   * @param callback  Handler, which will be registered.
   * @throws LlCoreRuntimeException if an error with wrong msgFilter occurs or if an
   *                                     error during registering a new handler occurs.
   * @throws NullPointerException        if Null elements are passed.
   */
  void registerMessageHandler(MsgSubscription msgFilter, IMessageCallback callback);

  /**
   * Deregister a registered message handler. An available message handler will be removed from
   * the connection system. So it won't be called anymore after deregistration. If the method
   * couldn't find any existing handler it will continue without an error message.
   *
   * @param msgFilter Name of the handler, which is defined by the registration
   *                  {@link #registerMessageHandler(MsgSubscription, IMessageCallback)}
   * @param callback  Callback of the handler, which is defined by the registration
   *                  {@link #registerMessageHandler(MsgSubscription, IMessageCallback)}
   * @throws LlCoreRuntimeException if an error during unregistering a handler occurs.
   */
  void unregisterMessageHandler(MsgSubscription msgFilter, IMessageCallback callback);
}
