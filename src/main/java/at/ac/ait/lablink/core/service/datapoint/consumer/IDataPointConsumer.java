//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.consumer;

import at.ac.ait.lablink.core.service.datapoint.ex.DatapointServiceRuntimeException;
import at.ac.ait.lablink.core.service.datapoint.payloads.DataPointProperties;

/**
 * DatapointConsumer that will connect to a remote datapoint.
 */
public interface IDataPointConsumer<T> {

  /**
   * Set a notifier listener to the datapoint consumer.
   *
   * <p>Incoming asynchronous requests will call this notifier to inform the user of this datapoint
   * about a change or request.
   *
   * @param notifier Notifier implementation that should be used.
   */
  void setNotifier(IDataPointConsumerNotifier<T> notifier);

  /**
   * Get the last value of the datapoint consumer.
   *
   * <p>This method will call only the local representation of the remote datapoint. It won't call
   * an update request.
   *
   * @return the value of the datapoint.
   */
  T getValue();

  /**
   * Get the last timestamp of the datapoint's value.
   *
   * @return the timestamp in milliseconds since 1.1.1970
   */
  long getTimestamp();

  /**
   * Get the last timestamp as a calculated emulated time if the datapoint is used in a
   * simulation environment. This method only works if the datapoint is used with the sync client
   *
   * @return the calculated timestamp in Emulation mode in milliseconds since 1.1.1970
   * @throws DatapointServiceRuntimeException if the emulation time isn't implemented and the
   *                                          datapoint isn't running in a synced environment
   */
  long getEmulationTimestamp();

  /**
   * Set the value and the timestamp of the remote datapoint.
   *
   * <p>It will send a setValue request to the remote datapoint.
   *
   * @param value Value to be set
   */
  void setValue(T value);

  /**
   * Get the state of the datapoint consumer.
   *
   * @return The current state of the datapoint consumer.
   */
  EDataPointConsumerState getState();

  /**
   * Get the received datapoint properties of the remote datapoint
   *
   * @return the properties of the remote datapoint.
   */
  DataPointProperties getProps();

  /**
   * Request a new value update from the remote datapoint.
   *
   * <p>This method will call an asynchronous request to the remote datapoint. The remote datapoint
   * will answer asynchronously with a value update.
   */
  void requestValueUpdate();
}
