//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.publishing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import at.ac.ait.lablink.core.connection.encoding.EncoderBase;
import at.ac.ait.lablink.core.connection.encoding.encodables.Packet;
import at.ac.ait.lablink.core.connection.encoding.impl.EncoderFactory;
import at.ac.ait.lablink.core.connection.ex.LowLevelCommRuntimeException;
import at.ac.ait.lablink.core.connection.mqtt.IMqttPublisher;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for class PublishingManager.
 */
public class PublishingManagerTest {

  private PublishingManager classUnderTest;
  private IMqttPublisher mqttPublisher;
  private EncoderBase encoder;

  @Before
  public void setUp() throws Exception {
    mqttPublisher = mock(IMqttPublisher.class);

    encoder = mock(EncoderBase.class);
    when(encoder.processEncoding(any(Packet.class))).thenReturn(new byte[0]);

    EncoderFactory encoderFactory = mock(EncoderFactory.class);
    when(encoderFactory.getDefaultEncoderObject()).thenReturn(encoder);
    when(encoderFactory.getEncoderObject(any(EncoderFactory.EEncoderType.class)))
        .thenReturn(encoder);

    classUnderTest = new PublishingManager();
    classUnderTest.setMqttPublisher(mqttPublisher);
    classUnderTest.setEncoderFactory(encoderFactory);
  }

  @Test
  public void publishPacket_useDefaultEncoder_MessageShouldBePublished_test() throws Exception {
    Packet packet = mock(Packet.class);
    List<String> topic = Arrays.asList("at.ac.ait", "app", "msg");

    classUnderTest.publishPacket(topic, packet);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void publishPacket_sendEmptyTopic_MessageShouldNotBePublished_test() throws Exception {
    Packet packet = mock(Packet.class);
    List<String> topic = new ArrayList<String>();

    classUnderTest.publishPacket(topic, packet);
  }

  @SuppressWarnings("ConstantConditions")
  @Test(expected = LlCoreRuntimeException.class)
  public void publishPacket_sendNullTopic_MessageShouldNotBePublished_test() throws Exception {
    Packet packet = mock(Packet.class);
    List<String> topic = null;

    classUnderTest.publishPacket(topic, packet);
  }

  @SuppressWarnings("ConstantConditions")
  @Test(expected = LlCoreRuntimeException.class)
  public void publishPacket_sendNullPacket_MessageShouldNotBePublished_test() throws Exception {
    Packet packet = null;
    List<String> topic = Arrays.asList("at.ac.ait", "app", "msg");

    classUnderTest.publishPacket(topic, packet);
  }

  @SuppressWarnings("ConstantConditions")
  @Test(expected = LlCoreRuntimeException.class)
  public void publishPacket_setEncoder_MessageShouldNotBePublished_test() throws Exception {
    Packet packet = null;
    List<String> topic = Arrays.asList("at.ac.ait", "app", "msg");

    classUnderTest.publishPacket(topic, packet, EncoderFactory.EEncoderType.JSON);
  }

  @Test
  public void publishPacket_setEncoder_MessageShouldBePublished_test() throws Exception {
    Packet packet = mock(Packet.class);
    List<String> topic = Arrays.asList("at.ac.ait", "app", "msg");

    classUnderTest.publishPacket(topic, packet, EncoderFactory.EEncoderType.JSON);
  }

  @Test(expected = LowLevelCommRuntimeException.class)
  public void publishPacket_lowLevelThrowsException_test() throws Exception {
    doThrow(new LowLevelCommRuntimeException()).when(mqttPublisher)
        .publish(anyString(), any(byte[].class));

    Packet packet = mock(Packet.class);
    List<String> topic = Arrays.asList("at.ac.ait", "app", "msg");

    classUnderTest.publishPacket(topic, packet);
  }
}