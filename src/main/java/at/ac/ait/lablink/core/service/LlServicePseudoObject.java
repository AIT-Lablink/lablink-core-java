//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

public class LlServicePseudoObject extends LlServicePseudo<Object> {

  public LlServicePseudoObject() {
    super();
    this.set(null);
  }

  public LlServicePseudoObject(String name, boolean readonly) {
    super(name, readonly);
    this.set(null);
  }

  public LlServicePseudoObject(String name) {
    super(name);
    this.set(null);
  }

  public LlServicePseudoObject(boolean readonly) {
    super(readonly);
    this.set(null);
  }

}
