//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.mqtt;

import at.ac.ait.lablink.core.connection.ex.LowLevelCommRuntimeException;

/**
 * Publisher interface to the MQTT low-level client.
 *
 * <p>The interface implements the publishing method to publish a message to the MQTT broker.
 */
public interface IMqttPublisher {

  /**
   * Publish a raw MQTT message.
   *
   * @param mqttTopic   Mqtt topic where the message should be published
   * @param mqttPayload IPayload of the message to be sent
   * @throws LowLevelCommRuntimeException if an error occurs during publishing a MQTT message
   */
  void publish(String mqttTopic, byte[] mqttPayload);
}
