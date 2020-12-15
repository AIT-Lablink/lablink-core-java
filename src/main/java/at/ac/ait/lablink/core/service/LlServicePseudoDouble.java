//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

public class LlServicePseudoDouble extends LlServicePseudo<Double> {

  public LlServicePseudoDouble() {
    super();
    this.set(0.0);
  }

  public LlServicePseudoDouble(String name, boolean readonly) {
    super(name, readonly);
    this.set(0.0);
  }

  public LlServicePseudoDouble(String name) {
    super(name);
    this.set(0.0);
  }

  public LlServicePseudoDouble(boolean readonly) {
    super(readonly);
    this.set(0.0);
  }

}
