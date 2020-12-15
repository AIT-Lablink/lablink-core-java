//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint;

import at.ac.ait.lablink.core.service.datapoint.payloads.ISimpleValue;

/**
 * Notification interface for a datapoint.
 *
 * <p>A datapoint will use this interface to inform a component about a received value update.
 */
public interface IDataPointNotifier<T> {

  /**
   * A set value for the datapoint is received from an external datapoint consumer.
   *
   * <p>During this method call the <code>dataPoint.setValue()</code> method should be called to
   * update the current state of the value.
   *
   * @param dataPoint Datapoint that received this setvalue request
   * @param setValue  new set value that is received
   */
  void valueSetNotifier(IDataPoint<T> dataPoint, ISimpleValue<T> setValue);

  /**
   * An Update request is received. The method should update its value using <code>dataPoint
   * .setValue()</code>.
   *
   * @param dataPoint Datapoint that received this update Value request
   */
  void requestValueUpdate(IDataPoint<T> dataPoint);
}
