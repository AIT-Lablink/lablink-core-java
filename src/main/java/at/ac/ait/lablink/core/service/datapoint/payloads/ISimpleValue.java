//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.payloads;

import at.ac.ait.lablink.core.service.datapoint.ex.DatapointServiceRuntimeException;

/**
 * Interface for simple value payloads that are used for the datapoint service.
 */
public interface ISimpleValue<T> {

  /**
   * Set the value.
   *
   * @param value value to be set
   */
  void setValue(T value);

  /**
   * Get the value.
   *
   * @return value
   */
  T getValue();

  /**
   * Set the timestamp of the value.
   *
   * @param time The timestamp in milliseconds since 1.1.1970
   */
  void setTime(long time);

  /**
   * Get the timestamp of the value.
   *
   * @return the timestamp in milliseconds since 1.1.1970
   */
  long getTime();


  void setEmulationTime(long emulationTime);

  /**
   * Get the last timestamp as a calculated emulated time if the datapoint is used in a
   * simulation environment. This method only works if the datapoint is used with the sync client
   *
   * @return the calculated timestamp in Emulation mode in milliseconds since 1.1.1970
   * @throws DatapointServiceRuntimeException if the emulation time isn't implemented and the
   *                                          datapoint isn't running in a synced environment.
   */
  long getEmulationTime();
}
