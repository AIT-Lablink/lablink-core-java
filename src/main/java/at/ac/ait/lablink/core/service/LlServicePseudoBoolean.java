//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

public class LlServicePseudoBoolean extends LlServicePseudo<Boolean> {

  public LlServicePseudoBoolean() {
    super();
    this.set(false);
  }

  public LlServicePseudoBoolean(boolean readonly) {
    super(readonly);
    this.set(false);
  }

  public LlServicePseudoBoolean(String name, boolean readonly) {
    super(name, readonly);
    this.set(false);
  }

  public LlServicePseudoBoolean(String name) {
    super(name);
    this.set(false);
  }

}
