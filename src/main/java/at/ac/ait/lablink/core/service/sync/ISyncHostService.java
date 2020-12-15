//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync;

import java.util.Collection;


public interface ISyncHostService {
  void init(String simulationConfig);

  void start();

  void shutdown();

  Collection<SyncParticipant> getRegisteredClients();
}
