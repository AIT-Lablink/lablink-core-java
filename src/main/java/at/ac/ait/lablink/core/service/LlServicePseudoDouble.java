//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

public class LlServicePseudoDouble extends LlServicePseudo<Double> {

 /**
   * Default constructor. (1)
   */
  public LlServicePseudoDouble() {
    super(false, true);
    init();
  }
	  
  /**
   * Constructor with service name only. (2)
   *
   * @param name the name
   */
  public LlServicePseudoDouble(String name) {
    super(name, false, true);
    init();
  }

  /**
   * Constructor with readonly flag only. (3)
   *
   * @param readonly the readonly
   */
  public LlServicePseudoDouble(boolean readonly) {
    super(readonly, true);
    init();
  }

  /**
   * Instantiates a new ll service PseudoDouble. (4)
   *
   * @param name the name
   * @param readonly the readonly
   */
  public LlServicePseudoDouble(String name, boolean readonly) {
    super(name, readonly, true);
    init();
  }
  
  /**
   * Instantiates a new ll service PseudoDouble. (5)
   *
   * @param readonly the readonly
   * @param expose the expose
   */
  public LlServicePseudoDouble(boolean readonly, boolean expose) {
    super(readonly, expose);
    init();
  }

  /**
   * Instantiates a new ll service PseudoDouble. (6)
   *
   * @param name the name
   * @param readonly the readonly
   * @param expose the expose
   */
  public LlServicePseudoDouble(String name, boolean readonly, boolean expose) {
    super(name, readonly, expose);
    init();
  }

  @Override
  protected void setGage() {
      this.serviceGage.set(this.get());
  }
  
  private void init() {
	  set(0.0);	  
	  this.serviceGage.set(this.get());	    
  }
}
