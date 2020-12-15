//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

public interface IServiceStateChangeNotifier<S, T> {
  public void stateChanged(S service, T oldVal, T newVal);
}
