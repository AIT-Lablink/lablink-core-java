//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.messaging.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import at.ac.ait.lablink.core.connection.ClientIdentifier;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.encoding.encodables.Packet;
import at.ac.ait.lablink.core.connection.publishing.PublishingManager;
import at.ac.ait.lablink.core.connection.topic.MsgSubject;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

/**
 * Unit tests for class MessagePublishHandlerImpl.
 */
@SuppressWarnings("unchecked")
public class MessagePublishHandlerImplTest {

  MessagePublishHandlerImpl msgPublisher;
  private PublishingManager publishingManager;

  @Before
  public void setUp() throws Exception {

    ClientIdentifier
        clientId =
        new ClientIdentifier(Collections.singletonList("at.ac.ait"), "TestApp", "group1",
            "client1");
    publishingManager = mock(PublishingManager.class);

    msgPublisher = new MessagePublishHandlerImpl("msg", clientId);
    msgPublisher.setPublishingManager(publishingManager);
  }

  @Test
  public void publishMessage_correct_lowLevelPublisherCalled_test() throws Exception {

    MsgSubject subject = MsgSubject.getBuilder().addSubjectElement("TestSubject").build();
    IPayload payload = mock(IPayload.class);

    msgPublisher.publishMessage(subject, Collections.singletonList(payload));

    verify(publishingManager,times(1)).publishPacket(anyList(),any(Packet.class));
  }
}