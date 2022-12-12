//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

import at.ac.ait.lablink.core.service.types.Complex;

public abstract class LlServiceComplex extends LlService<Complex> {

  public LlServiceComplex(String name) {
    super(name);
    setCurState(new Complex(0.0, 0.0));
  }

  public LlServiceComplex() {
    super();
    setCurState(new Complex(0.0, 0.0));
  }

  public LlServiceComplex(String name, boolean readonly) {
    super(name, readonly);
    setCurState(new Complex(0.0, 0.0));
  }

  public LlServiceComplex(boolean readonly) {
    super(readonly);
    setCurState(new Complex(0.0, 0.0));
  }
}
