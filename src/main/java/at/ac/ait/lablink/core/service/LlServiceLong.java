//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

public abstract class LlServiceLong extends LlService<Long> {

 /**
   * Default constructor. (1)
   */
  public LlServiceLong() {
    super(false, true);
    init();
  }
	  
  /**
   * Constructor with service name only. (2)
   *
   * @param name the name
   */
  public LlServiceLong(String name) {
    super(name, false, true);
    init();
  }

  /**
   * Constructor with readonly flag only. (3)
   *
   * @param readonly the readonly
   */
  public LlServiceLong(boolean readonly) {
    super(readonly, true);
    init();
  }

  /**
   * Instantiates a new ll service long. (4)
   *
   * @param name the name
   * @param readonly the readonly
   */
  public LlServiceLong(String name, boolean readonly) {
    super(name, readonly, true);
    init();
  }
  
  /**
   * Instantiates a new ll service long. (5)
   *
   * @param readonly the readonly
   * @param expose the expose
   */
  public LlServiceLong(boolean readonly, boolean expose) {
    super(readonly, expose);
    init();
  }

  /**
   * Instantiates a new ll service long. (6)
   *
   * @param name the name
   * @param readonly the readonly
   * @param expose the expose
   */
  public LlServiceLong(String name, boolean readonly, boolean expose) {
    super(name, readonly, expose);
    init();
  }

  @Override
  protected void setGage() {
      this.serviceGage.set(this.getCurState());
  }
  
  private void init() {
	  setCurState(0L);	  
	  this.serviceGage.set(this.getCurState());	    
  }
}
