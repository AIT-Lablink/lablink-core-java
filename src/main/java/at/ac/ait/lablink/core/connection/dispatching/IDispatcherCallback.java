//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.dispatching;

/**
 * Interface for handling registered callbacks from dispatcher. If an incoming messages should be
 * handled, this interface must be implemented and registered to the connection system.
 */
public interface IDispatcherCallback {

  /**
   * Callback function for received message.
   *
   * <p>An incoming message will be parsed and if it matched the handler's search pattern it will
   * call the handleMessage callback function
   *
   * @param payload IPayload of the received message
   */
  void handleMessage(byte[] payload);
}
