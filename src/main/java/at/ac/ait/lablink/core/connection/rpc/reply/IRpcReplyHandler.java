//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.reply;

import at.ac.ait.lablink.core.connection.rpc.IRpcRequester;
import at.ac.ait.lablink.core.connection.topic.RpcSubject;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

/**
 * Interface for handling RPC replies.
 */
public interface IRpcReplyHandler {

  /**
   * Register a reply callback method to the Lablink communication system.
   *
   * <p>The registration of the callback handler will return a {@link IRpcRequester} object. This
   * object is used for all requests to other client that should be handled with the registered
   * callback.
   *
   * @param subject  subject of the request and the corresponding reply handler.
   * @param callback Reply callback that should be registered to the Lablink communication system.
   * @return The corresponding RPC requester for the registered callback.
   * @throws LlCoreRuntimeException if an error during registering a new handler occurs.
   */
  IRpcRequester registerReplyHandler(RpcSubject subject, IRpcReplyCallback callback);
}


