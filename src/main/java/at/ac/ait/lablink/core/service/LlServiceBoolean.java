//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

public abstract class LlServiceBoolean extends LlService<Boolean> {

  public LlServiceBoolean() {
    super();
    setCurState(false);
  }

  public LlServiceBoolean(String name) {
    super(name);
    setCurState(false);
  }

  public LlServiceBoolean(String name, boolean readonly) {
    super(name, readonly);
    setCurState(false);
  }

  public LlServiceBoolean(boolean readonly) {
    super(readonly);
    setCurState(false);
  }

}
