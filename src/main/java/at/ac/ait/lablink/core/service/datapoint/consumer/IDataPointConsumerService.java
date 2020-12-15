//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.consumer;

import at.ac.ait.lablink.core.service.datapoint.ex.DatapointServiceRuntimeException;

import java.util.List;

/**
 * A service consumer of the datapoint service.
 */
public interface IDataPointConsumerService {

  /**
   * Register a datapoint consumer to the service consumer.
   *
   * @param dataPoint DatapointConsumer that should be registered.
   */
  void registerDatapointConsumer(DataPointConsumerGeneric dataPoint);

  /**
   * Unregister a datapoint consumer from the service consumer.
   *
   * @param dataPoint DatapointConsumer that should be removed from the service.
   */
  void unregisterDatapointConsumer(DataPointConsumerGeneric dataPoint);

  /**
   * Wait for all datapoint consumers to connect to their datapoints.
   *
   * <p>The method will check the connection state of the registered Datapoint Consumers
   * periodically and will wait until all consumers are connected to their datapoints.
   *
   * @throws DatapointServiceRuntimeException If not all datapoints are connected until the timeout
   *                                          exceeds.
   */
  void waitForAllDatapointConnections();

  void start();

  void shutdown();

  boolean isConnected();

  List<DataPointInfo> getAvailableDataPoints();
}
