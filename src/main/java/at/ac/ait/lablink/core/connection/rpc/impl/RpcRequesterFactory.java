//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.impl;

import at.ac.ait.lablink.core.connection.ClientIdentifier;
import at.ac.ait.lablink.core.connection.dispatching.ICallbackBase;
import at.ac.ait.lablink.core.connection.dispatching.IDispatcherCallback;
import at.ac.ait.lablink.core.connection.publishing.PublishingManager;
import at.ac.ait.lablink.core.connection.rpc.IRpcRequester;
import at.ac.ait.lablink.core.connection.rpc.reply.impl.RpcReplyDispatcher;
import at.ac.ait.lablink.core.connection.topic.RpcSubject;

import org.apache.commons.configuration.Configuration;

/**
 * Factory class for generating RPC requester.
 */
public class RpcRequesterFactory {


  private final ClientIdentifier clientId;
  private final Configuration config;
  private final String requestDispatcherIdentifier;

  private PublishingManager publishingManager;

  /**
   * Constructor.
   *
   * @param requestDispatcherIdentifier transmission identifier of the request dispatcher
   *                                    (usually "req")
   * @param clientId                    client identifier of the client.
   * @param config                      optional config parameters.
   */
  public RpcRequesterFactory(String requestDispatcherIdentifier, ClientIdentifier clientId,
                             Configuration config) {
    this.requestDispatcherIdentifier = requestDispatcherIdentifier;
    this.clientId = clientId;
    this.config = config;
  }

  /**
   * Set the publishing manager that is used to send the RPC requests.
   *
   * @param publishingManager to be set.
   */
  public void setPublishingManager(PublishingManager publishingManager) {
    this.publishingManager = publishingManager;
  }

  /**
   * Create a new RPC requester for a specific reply callback.
   *
   * @param subject          Subject of the RPC call
   * @param rpcReplyCallback Callback interface for the receiving replies.
   * @param errorCallback    Callback interface for handling errors.
   * @param replyDispatcher  Reply Root dispatcher that should be used to add reply
   *                         handlers after sending the requests.
   * @return the generated requester object
   */
  public IRpcRequester createNewRpcRequester(RpcSubject subject,
                                            IDispatcherCallback rpcReplyCallback,
                                            ICallbackBase errorCallback,
                                            RpcReplyDispatcher replyDispatcher) {
    RpcRequesterImpl
        requester =
        new RpcRequesterImpl(this.requestDispatcherIdentifier, clientId, subject, rpcReplyCallback,
            errorCallback, config);
    requester.setPublishingManager(publishingManager);
    requester.setRootReplyDispatcher(replyDispatcher);
    return requester;
  }
}
