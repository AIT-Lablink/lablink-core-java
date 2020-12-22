//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc;

import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.topic.RpcDestination;
import at.ac.ait.lablink.core.connection.topic.RpcSubject;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import java.util.List;

/**
 * Interface for an RPC requester that is used to send new Rpc requests.
 */
public interface IRpcRequester {

  /**
   * Send a request.
   *
   * @param destination Definition who should receive the request.
   * @param payload     Single payloads that should be transmitted with the request
   * @return The unique identifier of the sent request.
   * @throws LlCoreRuntimeException if an error occurs during sending the request
   */
  String sendRequest(RpcDestination destination, IPayload payload);

  /**
   * Send a request.
   *
   * @param destination Definition who should receive the request.
   * @param payload     Single payloads that should be transmitted with the request
   * @param noOfReturns Expected number of returns that should be received after the request. If
   *                    value is set to -1 the system will keep the channel open until the
   *                    timeout closes it. This allows an unknown number of replies to be received.
   * @return The unique identifier of the sent request.
   * @throws LlCoreRuntimeException if an error occurs during sending the request
   */
  String sendRequest(RpcDestination destination, IPayload payload, int noOfReturns);

  /**
   * Send a request.
   *
   * @param destination Definition who should receive the request.
   * @param payload     Single payloads that should be transmitted with the request
   * @param noOfReturns Expected number of returns that should be received after the request. If
   *                    value is set to -1 the system will keep the channel open until the
   *                    timeout closes it. This allows an unknown number of replies to be received.
   * @param timeoutInMs Timeout of the response handler to receive replies (in Milliseconds).
   * @return The unique identifier of the sent request.
   * @throws LlCoreRuntimeException if an error occurs during sending the request
   */
  String sendRequest(RpcDestination destination, IPayload payload, int noOfReturns,
                     long timeoutInMs);

  /**
   * Send a request.
   *
   * @param destination Definition who should receive the request.
   * @param payloads    List of payloads that should be transmitted with the request
   * @return The unique identifier of the sent request.
   * @throws LlCoreRuntimeException if an error occurs during sending the request
   */
  String sendRequest(RpcDestination destination, List<IPayload> payloads);

  /**
   * Send a request.
   *
   * @param destination Definition who should receive the request.
   * @param payloads    List of payloads that should be transmitted with the request
   * @param noOfReturns Expected number of returns that should be received after the request. If
   *                    value is set to -1 the system will keep the channel open until the
   *                    timeout closes it. This allows an unknown number of replies to be received.
   * @return The unique identifier of the sent request.
   * @throws LlCoreRuntimeException if an error occurs during sending the request
   */
  String sendRequest(RpcDestination destination, List<IPayload> payloads, int noOfReturns);

  /**
   * Send a request.
   *
   * @param destination Definition who should receive the request.
   * @param payloads    List of payloads that should be transmitted with the request
   * @param noOfReturns Expected number of returns that should be received after the request. If
   *                    value is set to -1 the system will keep the channel open until the
   *                    timeout closes it. This allows an unknown number of replies to be received.
   * @param timeoutInMs Timeout of the response handler to receive replies (in Milliseconds).
   * @return The unique identifier of the sent request.
   * @throws LlCoreRuntimeException if an error occurs during sending the request
   */
  String sendRequest(RpcDestination destination, List<IPayload> payloads, int noOfReturns,
                     long timeoutInMs);

  /**
   * Get the Subject of the requester.
   *
   * @return the actual registered subject.
   */
  RpcSubject getSubject();
}
