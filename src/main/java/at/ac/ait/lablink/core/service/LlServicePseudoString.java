//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

public class LlServicePseudoString extends LlServicePseudo<String> {

  public LlServicePseudoString() {
    super();
    this.set("");
  }

  public LlServicePseudoString(String name, boolean readonly) {
    super(name, readonly);
    this.set("");
  }

  public LlServicePseudoString(String name) {
    super(name);
    this.set("");
  }

  public LlServicePseudoString(boolean readonly) {
    super(readonly);
    this.set("");
  }

}
