//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection;

import at.ac.ait.lablink.core.connection.IConnectionHandler;
import at.ac.ait.lablink.core.connection.encoding.IEncodeableFactoryManager;
import at.ac.ait.lablink.core.connection.encoding.encodeables.IPayload;
import at.ac.ait.lablink.core.connection.messaging.IMessagePublishHandler;
import at.ac.ait.lablink.core.connection.messaging.IMessageReceiveHandler;
import at.ac.ait.lablink.core.connection.rpc.reply.IRpcReplyHandler;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestHandler;
import at.ac.ait.lablink.core.connection.topic.MsgSubject;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

/**
 * Interface for the Lablink low level connection system. The connection system provides different
 * communication paradigms.
 *
 * <p>This interface can be used to send updates, notifications, logging information or events to
 * other systems. Therefore it will be used to "inform the world" about my own state. Messages
 * uses a publish and subscribe pattern. The sender doesn't care about the message is received by
 * anyone. In the Lablink context this connection pattern will be call "connection" and the
 * transmitted information will be called "message".
 *
 * <p>TODO add rpc description
 */

public interface ILlConnection
    extends IConnectionHandler, IMessageReceiveHandler, IMessagePublishHandler, IRpcRequestHandler,
    IRpcReplyHandler, IEncodeableFactoryManager {

  /**
   * Send an asynchronous message to the connection system. This method is used to send updates,
   * notifications, events to other devices, which can be listen to this message. The main
   * application of this method is to inform the connected world with information about me. The
   * method doesn't care about the receiving of the message at any other client.
   *
   * @param msgSubject Elements of the message subject, which is used for sending (e.g.,
   *                   DataPointUpdate/Voltage/Node2)
   * @param payload    IPayload of the message, which is wrapped by the sending envelope
   * @throws LlCoreRuntimeException if the message can't be sent. Reasons for that can be the
   *                                     missing or incorrect subject or payloads or an error
   *                                     during the
   *                                     publishing itself.
   */
  void publishMessage(MsgSubject msgSubject, IPayload payload);

  /**
   * Shutdown the ILlConnection core.
   *
   * <p>This method will close all available connection and shuts down the client.
   */
  void shutdown();

  /**
   * Get the identifier of the ILlConnection.
   *
   * <p>The client identifier contains all information about the registered connection class.
   *
   * @return The client identifier of the Lablink connection.
   */
  ClientIdentifier getClientIdentifier();
}
