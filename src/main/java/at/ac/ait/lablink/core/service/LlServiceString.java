//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

public abstract class LlServiceString extends LlService<String> {

  public LlServiceString() {
    super();
    setCurState("");    
  }

  public LlServiceString(String name) {
    super(name);
    setCurState("");    
  }

  public LlServiceString(String name, boolean readonly) {
    super(name, readonly);
    setCurState("");    
  }

}
