//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync;

public interface ISyncHostNotifier {

  void stateChanged(ELlSyncHostState state);
}
