//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

public interface IImplementedService<T> {
  public String getName();

  public T getValue();

  public boolean setValue(T newval);

  public Class<T> getServiceDataTypeClass();
}
