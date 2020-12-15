//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

public abstract class LlServiceLong extends LlService<Long> {

  public LlServiceLong() {
    super();
    setCurState(0L);
  }

  public LlServiceLong(String name) {
    super(name);
    setCurState(0L);
  }

  public LlServiceLong(String name, boolean readonly) {
    super(name, readonly);
    setCurState(0L);
  }

  public LlServiceLong(boolean readonly) {
    super(readonly);
    setCurState(0L);
  }
}
