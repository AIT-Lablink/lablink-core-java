//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync.consumer;

import at.ac.ait.lablink.core.service.sync.ISyncParameter;


public interface ISyncClientService {
  void registerSyncConsumer(ISyncConsumer syncConsumer);

  void unregisterSyncConsumer(ISyncConsumer syncConsumer);

  void start();

  void shutdown();

  ISyncParameter getSyncParameter();

  long getCurrentSimTime();
}
