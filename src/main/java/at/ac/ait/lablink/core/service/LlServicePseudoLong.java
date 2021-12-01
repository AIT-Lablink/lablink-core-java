//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

public class LlServicePseudoLong extends LlServicePseudo<Long> {

  public LlServicePseudoLong() {
    super();
    this.set(0L);
  }

  public LlServicePseudoLong(String name, boolean readonly) {
    super(name, readonly);
  }

  public LlServicePseudoLong(String name) {
    super(name);
  }

  public LlServicePseudoLong(boolean readonly) {
    super(readonly);
  }

  @Override
  protected void setGage() {
      this.serviceGage.set(this.get());
  }

}
