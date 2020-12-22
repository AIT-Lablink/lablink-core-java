//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.rpc.request.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import at.ac.ait.lablink.core.connection.ClientIdentifier;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.encoding.encodables.Packet;
import at.ac.ait.lablink.core.connection.publishing.PublishingManager;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for class RpcReplyPublisher.
 */
public class RpcReplyPublisherTest {

  RpcReplyPublisher classUnderTest;
  private PublishingManager publishingManager;
  private RpcHeader requestHeader;
  private ArgumentCaptor<Packet> packetCaptor;
  private List<IPayload> payloads;


  @Before
  public void setUp() throws Exception {

    ClientIdentifier
        clientId =
        new ClientIdentifier(Collections.singletonList("top"), "TestApp", "Dstgroup1",
            "Dstclient1");
    classUnderTest = new RpcReplyPublisher("rep", clientId);

    publishingManager = mock(PublishingManager.class);

    classUnderTest.setPublishingManager(publishingManager);

    requestHeader =
        new RpcHeader("TestApp", "srcgroup1", "srcclient1", Arrays.asList("Sub1", "Sub2"), 12,
            "Dstgroup1", "Dstclient1", "PacketId");

    packetCaptor = ArgumentCaptor.forClass(Packet.class);
    payloads = new ArrayList<IPayload>();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void publishResponse_NoPayloadObjects_test() throws Exception {

    classUnderTest.publishResponse(requestHeader, new ArrayList<IPayload>());

    verify(publishingManager, times(1)).publishPacket(anyList(), packetCaptor.capture());
    List<IPayload> publishedPayloads = packetCaptor.getValue().getPayloads();
    assertEquals("Published payloads should minimum contain one payloads", 1,
        publishedPayloads.size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void publishResponse_AvailablePayloadObjects_test() throws Exception {

    payloads.add(mock(IPayload.class));

    classUnderTest.publishResponse(requestHeader, payloads);

    verify(publishingManager, times(1)).publishPacket(anyList(), packetCaptor.capture());
    List<IPayload> publishedPayloads = packetCaptor.getValue().getPayloads();
    assertEquals("Published payloads should match the expected ones.", payloads.size(),
        publishedPayloads.size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void publishResponse_CheckHeaderOptions_test() throws Exception {

    payloads.add(mock(IPayload.class));

    classUnderTest.publishResponse(requestHeader, payloads);

    verify(publishingManager, times(1)).publishPacket(anyList(), packetCaptor.capture());
    RpcHeader publishedHeader = (RpcHeader) packetCaptor.getValue().getHeader();

    assertEquals(requestHeader.getApplicationId(), publishedHeader.getApplicationId());
    assertEquals(requestHeader.getDestinationGroupId(), publishedHeader.getSourceGroupId());
    assertEquals(requestHeader.getDestinationClientId(), publishedHeader.getSourceClientId());
    assertEquals(requestHeader.getPacketId(), publishedHeader.getPacketId());
    assertEquals(requestHeader.getSourceGroupId(), publishedHeader.getDestinationGroupId());
    assertEquals(requestHeader.getSourceClientId(), publishedHeader.getDestinationClientId());
    assertEquals(requestHeader.getSubject(), publishedHeader.getSubject());
  }
}