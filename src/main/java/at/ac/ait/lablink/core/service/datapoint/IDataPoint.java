//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint;

import at.ac.ait.lablink.core.service.datapoint.payloads.DataPointProperties;

/**
 * Datapoint that will host simple values and transmit it using Lablink.
 */
public interface IDataPoint<T> {

  /**
   * Set a notifier listener to the datapoint.
   *
   * <p>Incoming asynchronous requests will call this notifier to inform the user of this datapoint
   * about a change or request.
   *
   * @param notifier Notifier implementation that should be used.
   */
  void setNotifier(IDataPointNotifier<T> notifier);

  /**
   * Get the last value of the datapoint.
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
   * Set the value and the timestamp of the datapoint.
   *
   * <p>The timestamp is independent from the wall clock time.
   *
   * @param value     Value to be set
   * @param timestamp Timestamp to be set
   */
  void setValue(T value, long timestamp);

  /**
   * Set the value of the datapoint. The current computer time will be used as a timestamp.
   *
   * @param value Value to be set.
   */
  void setValue(T value);

  /**
   * Get the properties of the datapoint.
   *
   * @return the properties.
   */
  DataPointProperties getProps();
}
