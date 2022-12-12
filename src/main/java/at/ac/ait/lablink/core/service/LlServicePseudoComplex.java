//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

import at.ac.ait.lablink.core.service.types.Complex;

public class LlServicePseudoComplex extends LlServicePseudo<Complex> {

  public LlServicePseudoComplex() {
    super();
    this.set(new Complex(0.0, 0.0));
  }

  public LlServicePseudoComplex(String name, boolean readonly) {
    super(name, readonly);
    this.set(new Complex(0.0, 0.0));
  }

  public LlServicePseudoComplex(String name) {
    super(name);
    this.set(new Complex(0.0, 0.0));
  }

  public LlServicePseudoComplex(boolean readonly) {
    super(readonly);
    this.set(new Complex(0.0, 0.0));
  }

}
