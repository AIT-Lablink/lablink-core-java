//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

public abstract class LlServiceDouble extends LlService<Double> {

  public LlServiceDouble(String name) {
    super(name);
    setCurState(0.0);
    this.exposeToPrometheus = true;
  }

  public LlServiceDouble() {
    super();
    setCurState(0.0);
  }

  public LlServiceDouble(String name, boolean readonly) {
    super(name, readonly);
    setCurState(0.0);
    this.exposeToPrometheus = true;
  }

  public LlServiceDouble(boolean readonly) {
    super(readonly);
    setCurState(0.0);
    this.exposeToPrometheus = true;
  }

  @Override
  protected void setGage() {
      this.serviceGage.set(this.getCurState());
  }  
}
