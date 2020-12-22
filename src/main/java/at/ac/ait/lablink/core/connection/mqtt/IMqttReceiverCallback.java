//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.mqtt;

/**
 * LowLevel MQTT interface for received messages.
 *
 * <p>An implementation of this interface is used by the MQTT client to forward a received message
 * to a handler.
 */
public interface IMqttReceiverCallback {

  /**
   * Callback method for a received MQTT raw message.
   *
   * <p>The method should handle the received MQTT raw message.
   *
   * @param topic   full MQTT topic of the received message
   * @param mqttPayload encodables of the received message
   */
  void handleRawMqttMessage(String topic, byte[] mqttPayload);

}
