//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.consumer;

/**
 * Notification interface of a datapoint consumer.
 *
 * <p>A datapoint will use this interface to inform a component about a received value update.
 */
public interface IDataPointConsumerNotifier<T> {

  /**
   * A value update is received.
   *
   * @param dataPointConsumer DatapointConsumer that call this value update.
   */
  void valueUpdate(IDataPointConsumer<T> dataPointConsumer);

  /**
   * The state of the datapoint consumer changes.
   *
   * @param dataPointConsumer DatapointConsumer that call this value update.
   */
  void stateChanged(IDataPointConsumer<T> dataPointConsumer);
}
