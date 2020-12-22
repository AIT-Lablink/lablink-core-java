//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.publishing;

import at.ac.ait.lablink.core.connection.encoding.EncoderBase;
import at.ac.ait.lablink.core.connection.encoding.encodables.Packet;
import at.ac.ait.lablink.core.connection.encoding.impl.EncoderFactory;
import at.ac.ait.lablink.core.connection.ex.LowLevelCommRuntimeException;
import at.ac.ait.lablink.core.connection.mqtt.IMqttPublisher;
import at.ac.ait.lablink.core.connection.mqtt.impl.MqttUtils;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import java.util.List;

/**
 * Simple manager and abstraction for publishing messages over a MQTT client.
 *
 * <p>The manager first encodes the packet that should be published into a byte array and sends
 * this array afterwards to the low-level communication.
 *
 * <p>For correct operations the PublishingManager needs a default encoder and a low-level
 * IMqttPublisher that should be set after the construction at the beginning
 * ({@link #setEncoderFactory(EncoderFactory)} and {@link #setMqttPublisher(IMqttPublisher)}).
 */
public class PublishingManager {

  /* default encoder used by the publishing of messages */
  private EncoderFactory encoderFactory;

  /* Mqtt client used for publishing messages */
  private IMqttPublisher mqttPublisher;


  /**
   * Set the factory class for generating the encoder that is used.
   *
   * @param encoderFactory encoder used as a default
   */
  public void setEncoderFactory(EncoderFactory encoderFactory) {
    this.encoderFactory = encoderFactory;
  }

  /**
   * Set the Mqtt client for publishing messages.
   *
   * @param mqttPublisher used for communication
   */
  public void setMqttPublisher(IMqttPublisher mqttPublisher) {
    this.mqttPublisher = mqttPublisher;
  }

  /**
   * Publish a message over MQTT using the default encoder for the packet payloads.
   *
   * @param topic  List of topic elements where the packet should be published
   * @param packet Packet payloads to be published
   * @throws LowLevelCommRuntimeException if an error with the low level communication happens.
   */
  public void publishPacket(List<String> topic, Packet packet) {
    if (encoderFactory == null) {
      throw new LlCoreRuntimeException(
          "The publishing manager hasn't an encoder factory manager registered.");
    }

    this.publishPacket(topic, packet, encoderFactory.getDefaultEncoderObject());
  }


  /**
   * Publish a message over MQTT using the default encoder for the packet payloads.
   *
   * @param topic       List of topic elements where the packet should be published
   * @param packet      Packet payloads to be published
   * @param encoderType Type of the encoder that is used to encode the sending packet
   * @throws LowLevelCommRuntimeException if an error with the low level communication happens.
   */
  public void publishPacket(List<String> topic, Packet packet,
                            EncoderFactory.EEncoderType encoderType) {
    if (encoderFactory == null) {
      throw new LlCoreRuntimeException(
          "The publishing manager hasn't an encoder factory manager registered.");
    }

    this.publishPacket(topic, packet, encoderFactory.getEncoderObject(encoderType));
  }

  /**
   * Publish a message over MQTT using a specific encoder for the packet payloads.
   *
   * @param topic   List of topic elements where the packet should be published
   * @param packet  Packet payloads to be published
   * @param encoder IEncoder that should be used for encoding the package.
   * @throws LowLevelCommRuntimeException if an error with the low level communication happens
   *                                      and the package can't be sent.
   */
  private void publishPacket(List<String> topic, Packet packet, EncoderBase encoder) {

    if (encoder == null) {
      throw new LlCoreRuntimeException("No encoder is specified for conversation");
    }

    if (packet == null) {
      throw new LlCoreRuntimeException(
          "No packet is specified for publishing. Packet is null.");
    }

    MqttUtils.validateMqttTopic(topic);
    packet.validate();

    String mqttTopic = MqttUtils.convertStringListTopicToMqtt(topic);
    byte[] payload = encoder.processEncoding(packet);
    encoderFactory.returnEncoderToPool(encoder);

    mqttPublisher.publish(mqttTopic, payload);
  }
}
