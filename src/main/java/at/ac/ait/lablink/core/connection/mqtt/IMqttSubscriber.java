//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.mqtt;

import at.ac.ait.lablink.core.connection.ex.LowLevelCommRuntimeException;

import java.util.List;

/**
 * Subscriber interface to the MQTT low-level client.
 *
 * <p>The interface implements methods to subscribe or unsubscribe a topic to/from the MQTT broker.
 */
public interface IMqttSubscriber {

  /**
   * Subscribe a MQTT topic to the MQTT broker.
   *
   * @param mqttTopic MQTT topic subscription filter (containing wildcards)
   * @throws LowLevelCommRuntimeException if an error occurs during subscribing a MQTT message
   */
  void subscribe(String mqttTopic);

  /**
   * Subscribe a list of MQTT topics to the MQTT broker.
   *
   * @param mqttTopics List containing MQTT topic subscription filters (containing wildcards)
   * @throws LowLevelCommRuntimeException if an error occurs during subscribing a MQTT message
   */
  void subscribe(List<String> mqttTopics);

  /**
   * Subscribe a MQTT topic from the MQTT broker.
   *
   * <p>The topic filter must be the same string as the subscribed one.
   *
   * @param mqttTopic MQTT topic subscription filter (containing wildcards).
   * @throws LowLevelCommRuntimeException if an error occurs during unsubscribing a MQTT message
   */
  void unsubscribe(String mqttTopic);

  /**
   * Subscribe a list of MQTT topics from the MQTT broker.
   *
   * <p>The topic filter must be the same string as the subscribed one.
   *
   * @param mqttTopics List containing MQTT topic subscription filters (containing wildcards)
   * @throws LowLevelCommRuntimeException if an error occurs during unsubscribing a MQTT message
   */
  void unsubscribe(List<String> mqttTopics);
}
