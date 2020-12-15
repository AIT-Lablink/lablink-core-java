//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.messaging;

import at.ac.ait.lablink.core.connection.encoding.encodeables.IPayload;
import at.ac.ait.lablink.core.connection.topic.MsgSubject;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import java.util.List;

/**
 * Interface for handling messages.
 *
 * <p>A message in the Lablink connection module indicates an unidirectional and asynchronous
 * communication channel. A client can send a message to a communication channel. The receiving
 * clients can listen to such an channel. If a message is received at a client it will call a
 * callback method with the received packet.
 */
public interface IMessagePublishHandler {

  /**
   * Send an asynchronous message to the connection system. This method is used to send updates,
   * notifications, events to other devices, which can be listen to this message. The main
   * application of this method is to inform the connected world with information about me. The
   * method doesn't care about the receiving of the message at any other client.
   *
   * @param subject  Elements of the message subject, which is used for sending (e.g.,
   *                 DataPointUpdate/Voltage/Node2)
   * @param payloads IPayload of the message, which is wrapped by the sending envelope
   * @throws LlCoreRuntimeException if the message can't be sent. Reasons for that can be the
   *                                     missing or incorrect subject or payloads or an error
   *                                     during the
   *                                     publishing itself.
   */
  void publishMessage(MsgSubject subject, List<IPayload> payloads);
}
