//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.request;

import at.ac.ait.lablink.core.connection.topic.RpcSubject;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

/**
 * Interface for handling RPC requests.
 */
public interface IRpcRequestHandler {

  /**
   * Register a handler for handling received RPC requests. The handler will be registered with a
   * method name given by the parameter requestName.
   *
   * <p>For an RPC request only one handler can be registered to the system. Otherwise multiple
   * handler will reply to a single request. That is unwanted and generates an unpredictable system.
   *
   * @param subject  Method name, which will be used to identify the request. It will be
   *                 merged with the client and group ID of the connection core.
   * @param callback Handler that will be registered. The requestHandler handles the
   *                 incoming request and generates the encodeables for the response message,
   *                 which will be sent back to the requester
   * @throws LlCoreRuntimeException if an error during registering a new handler
   *                                     occurs.
   */
  void registerRequestHandler(RpcSubject subject, IRpcRequestCallback callback);

  /**
   * Deregister a registered message handler. An available message handler will be removed from
   * the connection system. So it won't be called anymore after the deregistering process. If the
   * method couldn't find any existing handler it will continue without an error message.
   *
   * @param subject  Name of the handler, which is defined by the registration
   *                 {@link #registerRequestHandler(RpcSubject, IRpcRequestCallback)}
   * @param callback Handler that will be unregistered. The requestHandler handles the
   *                 incoming request and generates the encodeables for the response message,
   *                 which will be sent back to the requester
   * @throws LlCoreRuntimeException if an error during unregistering a handler occurs.
   */
  void unregisterRequestHandler(RpcSubject subject, IRpcRequestCallback callback);
}


