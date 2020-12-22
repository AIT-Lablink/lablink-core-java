//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.messaging.impl;

import at.ac.ait.lablink.core.connection.ClientIdentifier;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.encoding.encodables.Packet;
import at.ac.ait.lablink.core.connection.messaging.IMessagePublishHandler;
import at.ac.ait.lablink.core.connection.messaging.MsgHeader;
import at.ac.ait.lablink.core.connection.publishing.PublishingManager;
import at.ac.ait.lablink.core.connection.topic.MsgSubject;
import at.ac.ait.lablink.core.connection.topic.Topic;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import java.util.List;

/**
 * Manager and helping class for publishing messages.
 *
 * <p>The publishing manager is used to prepare and send a message over the low-level communication.
 */
public class MessagePublishHandlerImpl implements IMessagePublishHandler {

  private PublishingManager publishingManager;

  private final String transmissionIdentifier;
  private final ClientIdentifier clientId;

  /**
   * Constructor.
   *
   * @param transmissionIdentifier identifier of the message transmission type (typically "msg")
   * @param clientId               Identifier of the Lablink client
   */
  public MessagePublishHandlerImpl(String transmissionIdentifier, ClientIdentifier clientId) {
    this.transmissionIdentifier = transmissionIdentifier;
    this.clientId = clientId;
  }

  /**
   * Set the lower level publishing manager that is used to send a message.
   *
   * @param publishingManager to be set
   */
  public void setPublishingManager(PublishingManager publishingManager) {
    this.publishingManager = publishingManager;
  }

  /**
   * {@inheritDoc}
   *
   * @param subject  Elements of the message subject, which is used for sending (e.g.,
   *                 DataPointUpdate/Voltage/Node2)
   * @param payloads IPayload of the message, which is wrapped by the sending envelope
   * @throws LlCoreRuntimeException if the message can't be sent.
   */
  @Override
  public void publishMessage(MsgSubject subject, List<IPayload> payloads) {

    Topic topic = new Topic();
    topic.setPrefix(clientId.getPrefix());
    topic.setApplicationId(clientId.getAppId());
    topic.setClientIdentifiers(clientId.getGroupId(), clientId.getClientId());
    topic.setSubject(subject.getSubject());
    topic.setTransmissionType(this.transmissionIdentifier);

    MsgHeader
        header =
        new MsgHeader(topic.getApplicationId(), topic.getGroupId(), topic.getClientId(),
            topic.getSubject(), System.currentTimeMillis());
    //TODO timestamp class

    Packet packet = new Packet(header, payloads);

    List<String> mqttTopic = topic.getTopic();

    publishingManager.publishPacket(mqttTopic, packet);
  }
}
