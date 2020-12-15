//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.dispatching;

import at.ac.ait.lablink.core.connection.mqtt.IMqttConnectionListener;
import at.ac.ait.lablink.core.connection.mqtt.IMqttReceiverCallback;
import at.ac.ait.lablink.core.connection.mqtt.IMqttSubscriber;

import java.util.List;

/**
 * Interface for a root dispatcher.
 *
 * <p>The root dispatcher is used to manage other dispatcher units. It should also implement the
 * connection to the low level Mqtt communication. Therefore it must implement the
 * {@link IMqttReceiverCallback} and {@link IMqttConnectionListener} interfaces. The Mqtt client
 * will use these interfaces to inform the Root Dispatcher about connection changes or received
 * messages. The root dispatcher can also use a {@link IMqttSubscriber} to subscribe or unsubscribe
 * topics to/from the Mqtt broker.
 */
public interface IRootDispatcher extends IMqttReceiverCallback, IMqttConnectionListener {

  /**
   * Add a dispatcher to the IRootDispatcher.
   *
   * @param subscription List of topic elements that points to the requested dispatcher to be
   *                     add.
   * @param node         Dispatcher to be added
   */
  void addDispatcher(List<String> subscription, IDispatcherInterface node);

  /**
   * Remove a dispatcher from the IRootDispatcher.
   *
   * @param subscription List of topic elements that points to the requested dispatcher to be
   *                     deleted.
   */
  void removeDispatcher(List<String> subscription);

  /**
   * Check if the IRootDispatcher contains a registered dispatcher with the given full dispatcher
   * name.
   *
   * @param dispatcherName List of topic elements that points to the requested dispatcher.
   * @return true if a dispatcher is already registered
   */
  boolean hasDispatcher(List<String> dispatcherName);

  /**
   * Get an already registered dispatcher from the IRootDispatcher.
   *
   * <p>If no dispatcher with the given name is registered in the IRootDispatcher the method will
   * return null.
   *
   * @param dispatcherName List of topic elements that points to the requested dispatcher.
   * @return The method will return a registered dispatcher, if it exists.
   */
  IDispatcherInterface getDispatcher(List<String> dispatcherName);

  /**
   * Add a IMqttSubscriber to the root dispatcher. This subscriber will be used to subscribe and
   * unsubscribe topics to/from the MQTT broker.
   *
   * @param mqttSubscriber to be stored in the IRootDispatcher.
   */
  void setMqttSubscriber(IMqttSubscriber mqttSubscriber);
}
