//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

public abstract class LlServiceObject extends LlService<Object> {

  public LlServiceObject() {
    super();
  }

  public LlServiceObject(String name) {
    super(name);
  }

  public LlServiceObject(String name, boolean readonly) {
    super(name, readonly);
  }

  public LlServiceObject(boolean readonly) {
    super(readonly);
  }
}
