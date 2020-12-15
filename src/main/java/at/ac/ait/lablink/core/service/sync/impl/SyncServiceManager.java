//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync.impl;

import at.ac.ait.lablink.core.connection.ILlConnection;
import at.ac.ait.lablink.core.service.sync.ISyncHostService;
import at.ac.ait.lablink.core.service.sync.consumer.ISyncClientService;
import at.ac.ait.lablink.core.service.sync.consumer.impl.SyncClientServiceImpl;

import org.apache.commons.configuration.Configuration;


public class SyncServiceManager {

  public static ISyncHostService getSyncHostService(ILlConnection connection,
                                                   Configuration config) {
    return new SyncHostServiceImpl(connection, config);
  }

  public static ISyncClientService getSyncClientService(ILlConnection connection,
                                                       Configuration config) {
    return new SyncClientServiceImpl(connection, config);
  }
}
