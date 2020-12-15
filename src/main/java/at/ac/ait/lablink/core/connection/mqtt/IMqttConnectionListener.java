//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.mqtt;

/**
 * Listener interface for events from the low-level communication client.
 *
 * <p>This interface describes methods, which are called from the low level MQTT client to inform
 * a connected class about occurred events. A class (with the implemented interface) can be
 * registered to the low-level MQTT client.
 */
public interface IMqttConnectionListener {

  /**
   * Callback method for an established MQTT connection to a broker
   *
   * <p>This method is called by the MQTT client after a successful connection establishment. This
   * method should be used to register available subscriptions to the connected broker. The
   * communication with a broker is only possible with a connection. The low-level MQTT client uses
   * the method to inform connected classes about a connection establishment. The connected classes
   * can now register already stored subscriptions to the broker.
   */
  void onEstablishedMqttConnection();

  /**
   * Callback method for a lost connection to the MQTT broker.
   *
   * <p>This method is called by the MQTT client, if it has lost its connection to the MQTT
   * broker. This can be happened if the broker crashes or was shutdown.
   */
  void onLostMqttConnection();

  /**
   * Callback method that is called during the disconnecting process.
   *
   * <p>The MQTT client calls this method before disconnecting from a MQTT broker. Therefore this
   * method can be used for cleanup procedures (e.g., unsubscribing topics). The method will be
   * called only by an intended disconnection call. If an exception occurs and the connection is
   * lost than the @link onLostMqttConnection() is called
   */
  void onDisconnectingMqttConnection();
}
