//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.messaging;

import at.ac.ait.lablink.core.connection.dispatching.ICallbackBase;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;

import java.util.List;

/**
 * Interface for handling messages. If incoming messages should be handled, this interface must
 * be implemented and registered to the connection system:
 */
public interface IMessageCallback extends ICallbackBase {

  /**
   * Callback function for received message
   *
   * <p>An incoming message will be parsed and if it matched the handler's search pattern it will
   * call the handleMessage callback function
   *
   * <p>The method can be called from different threads at the same time. Therefore the
   * implementation of this method should be able to handle concurrent access.
   *
   * @param header   header of the received message
   * @param payloads Payloads of the received message
   * @throws Exception if an error occurs during processing the callback.
   */
  void handleMessage(MsgHeader header, List<IPayload> payloads) throws Exception;
}
