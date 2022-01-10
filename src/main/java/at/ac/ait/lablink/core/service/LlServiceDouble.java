//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

// TODO: Auto-generated Javadoc
/**
 * The Class LlServiceDouble.
 */
public abstract class LlServiceDouble extends LlService<Double> {

  /**
   * Default constructor. (1)
   */
  public LlServiceDouble() {
    super(false, true);
    init();
  }
	  
  /**
   * Constructor with service name only. (2)
   *
   * @param name the name
   */
  public LlServiceDouble(String name) {
    super(name, false, true);
    init();
  }

  /**
   * Constructor with readonly flag only. (3)
   *
   * @param readonly the readonly
   */
  public LlServiceDouble(boolean readonly) {
    super(readonly, true);
    init();
  }

  /**
   * Instantiates a new ll service double. (4)
   *
   * @param name the name
   * @param readonly the readonly
   */
  public LlServiceDouble(String name, boolean readonly) {
    super(name, readonly, true);
    init();
  }
  
  /**
   * Instantiates a new ll service double. (5)
   *
   * @param readonly the readonly
   * @param expose the expose
   */
  public LlServiceDouble(boolean readonly, boolean expose) {
    super(readonly, expose);
    init();
  }

  /**
   * Instantiates a new ll service double. (6)
   *
   * @param name the name
   * @param readonly the readonly
   * @param expose the expose
   */
  public LlServiceDouble(String name, boolean readonly, boolean expose) {
    super(name, readonly, expose);
    init();
  }
  
  /**
   * Sets the gage.
   */
  @Override
  protected void setGage() {
      this.serviceGage.set(this.getCurState());
  }  
  
  /**
   * Inits the.
   */
  private void init() {
	  setCurState(0.0);	  
	  this.serviceGage.set(this.getCurState());	    
  }
}
