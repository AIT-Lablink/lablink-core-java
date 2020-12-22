//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.reply;

import at.ac.ait.lablink.core.connection.dispatching.ICallbackBase;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;

import java.util.List;

/**
 * Interface for handling messages. If incoming messages should be handled, this interface must
 * be implemented and registered as RequestHandler to the connection system
 */
public interface IRpcReplyCallback extends ICallbackBase {

  /**
   * Callback function for received request messages.
   * An incoming message will be parsed and if it matched the handler's search pattern it will
   * call the handleRequest callback function
   *
   * <p>The method can be called from different threads at the same time. Therefore the
   * implementation of this method should be able to handle concurrent access.
   *
   * @param header   received header of the response
   * @param payloads that were received
   */
  void handleReply(RpcHeader header, List<IPayload> payloads);
}
