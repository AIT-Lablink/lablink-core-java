//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection;

import at.ac.ait.lablink.core.connection.ex.LowLevelCommRuntimeException;

/**
 * Interface for handling connection commands.
 *
 * <p>The interface provides the functionality to connect the communication core to another one. In
 * case of MQTT the connection core with its low-level communication interface) will be connected
 * to an external broker.
 */
public interface IConnectionHandler {

  /**
   * Connect to the low level connection core.
   *
   * <p>This method must be called before sending the first message. If the core isn't connected to
   * an external device (broker) it won't be possible to receive or send any message.
   *
   * <p>Usually the messaging core won't connect automatically to an external broker. So it is
   * necessary to call this method before using the connection.
   *
   * @throws LowLevelCommRuntimeException if an error occurs during connecting to a MQTT broker
   */
  void connect();

  /**
   * Disconnect from the connection core.
   *
   * <p>After calling this method no messages will be received from an external device.
   *
   * @throws LowLevelCommRuntimeException if an error occurs during disconnecting to a MQTT broker
   */
  void disconnect();

  /**
   * Checks if the Mqtt client core is connected to a broker.
   *
   * @return true if the low-level client is connected to a broker, otherwise false
   */
  boolean isConnected();
}
