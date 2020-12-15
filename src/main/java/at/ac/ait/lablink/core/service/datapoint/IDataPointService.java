//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint;

import at.ac.ait.lablink.core.connection.encoding.encodeables.IPayload;
import at.ac.ait.lablink.core.service.datapoint.payloads.ISimpleValue;
import at.ac.ait.lablink.core.service.sync.consumer.ISyncConsumer;

import java.util.List;


/**
 * Datapoint service for hosting datapoints with simple values.
 */
public interface IDataPointService {

  /**
   * Register a datapoint to the service and host it.
   *
   * @param dataPoint IDataPoint to be registered
   */
  void registerDatapoint(DataPointGeneric dataPoint);

  /**
   * Unregister an existing datapoint from the service.
   *
   * @param dataPoint IDataPoint to be unregistered
   */
  void unregisterDatapoint(DataPointGeneric dataPoint);

  void publishValue(List<String> dataPointIdentifier, ISimpleValue payload);
  
  void publishMessage(List<String> dataPointIdentifier, String command, List<IPayload> payloads);

  void start();

  void shutdown();

  ISyncConsumer getSyncConsumer();
}
