//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

public class LlServicePseudoLong extends LlServicePseudo<Long> {

 /**
   * Default constructor. (1)
   */
  public LlServicePseudoLong() {
    super(false, true);
    init();
  }
	  
  /**
   * Constructor with service name only. (2)
   *
   * @param name the name
   */
  public LlServicePseudoLong(String name) {
    super(name, false, true);
    init();
  }

  /**
   * Constructor with readonly flag only. (3)
   *
   * @param readonly the readonly
   */
  public LlServicePseudoLong(boolean readonly) {
    super(readonly, true);
    init();
  }

  /**
   * Instantiates a new ll service PseudoLong. (4)
   *
   * @param name the name
   * @param readonly the readonly
   */
  public LlServicePseudoLong(String name, boolean readonly) {
    super(name, readonly, true);
    init();
  }
  
  /**
   * Instantiates a new ll service PseudoLong. (5)
   *
   * @param readonly the readonly
   * @param expose the expose
   */
  public LlServicePseudoLong(boolean readonly, boolean expose) {
    super(readonly, expose);
    init();
  }

  /**
   * Instantiates a new ll service PseudoLong. (6)
   *
   * @param name the name
   * @param readonly the readonly
   * @param expose the expose
   */
  public LlServicePseudoLong(String name, boolean readonly, boolean expose) {
    super(name, readonly, expose);
    init();
  }

  @Override
  protected void setGage() {
      this.serviceGage.set(this.get());
  }

  private void init() {
	  set(0L);	  
	  this.serviceGage.set(this.get());	    
  }
}
