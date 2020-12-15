//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.impl;

import at.ac.ait.lablink.core.connection.ILlConnection;
import at.ac.ait.lablink.core.service.datapoint.IDataPointService;
import at.ac.ait.lablink.core.service.datapoint.consumer.IDataPointConsumerService;
import at.ac.ait.lablink.core.service.datapoint.consumer.impl.DataPointConsumerServiceImpl;

import org.apache.commons.configuration.Configuration;

/**
 * Created by NoehrerM on 13.07.2017.
 */
public class DataPointServiceManager {

  public static IDataPointService getDataPointService(ILlConnection connection,
                                                     Configuration config) {
    return new DataPointServiceImpl(connection, config);
  }

  public static IDataPointConsumerService getDataPointConsumerService(ILlConnection connection,
                                                                     Configuration config) {
    return new DataPointConsumerServiceImpl(connection, config);
  }
}
