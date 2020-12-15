//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.request.impl;

import at.ac.ait.lablink.core.connection.ClientIdentifier;
import at.ac.ait.lablink.core.connection.encoding.encodeables.IPayload;
import at.ac.ait.lablink.core.connection.encoding.encodeables.Packet;
import at.ac.ait.lablink.core.connection.publishing.PublishingManager;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;
import at.ac.ait.lablink.core.payloads.StatusMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Publisher for RPC replies. This publisher will be used to publish a reply of an RPC message.
 */
public class RpcReplyPublisher {

  private final String replyToDispatcherIdentifier;
  private final ClientIdentifier clientId;

  private PublishingManager publishingManager;

  /**
   * Constructor.
   *
   * @param replyToDispatcherIdentifier Identifier of the requested dispatcher (usually: "rep").
   * @param clientId                    Identifier of the client.
   */
  public RpcReplyPublisher(String replyToDispatcherIdentifier, ClientIdentifier clientId) {
    this.replyToDispatcherIdentifier = replyToDispatcherIdentifier;
    this.clientId = clientId;
  }

  /**
   * Sets the lower level publishing manager that is used for publishing a message
   *
   * @param publishingManager publishing manager to be set.
   */
  public void setPublishingManager(PublishingManager publishingManager) {
    this.publishingManager = publishingManager;
  }

  /**
   * Publish a reply or response back to the sender of the request.
   *
   * @param requestHeader Header that is received from the requester
   * @param payloads      List of payloads that should be sent back to the requester
   * @throws LlCoreRuntimeException if an error during publishing the reply occurs.
   */
  public void publishResponse(RpcHeader requestHeader, List<IPayload> payloads) {

    if (requestHeader == null) {
      throw new LlCoreRuntimeException("No RPC Header from request is specified.");
    }

    RpcHeader
        header =
        new RpcHeader(clientId.getAppId(), clientId.getGroupId(), clientId.getClientId(),
            requestHeader.getSubject(), System.currentTimeMillis(),
            requestHeader.getSourceGroupId(), requestHeader.getSourceClientId(),
            requestHeader.getPacketId());

    List<String> replyTopic = new ArrayList<String>();
    replyTopic.addAll(clientId.getPrefix());
    replyTopic.add(clientId.getAppId());
    replyTopic.add(this.replyToDispatcherIdentifier);
    replyTopic.add(header.getDestinationGroupId());
    replyTopic.add(header.getDestinationClientId());
    replyTopic.addAll(header.getSubject());
    replyTopic.add(header.getPacketId());

    if (payloads == null || payloads.isEmpty()) {
      payloads = new ArrayList<IPayload>();
      payloads.add(new StatusMessage(StatusMessage.StatusCode.OK));
    }

    Packet packet = new Packet(header, payloads);

    publishingManager.publishPacket(replyTopic, packet);
  }
}
