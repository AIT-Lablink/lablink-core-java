//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.spi;

import at.ac.ait.lablink.core.client.ci.ILlClientCommInterface;

/**
 * The host implementation of the Lablink service provider interface.
 */
public final class HostImplementationSpi {

  /** The host SPI. */
  private ALlHostImplementation hostSpi;

  /** The host implementer. */
  private Class<ILlClientCommInterface> hostImplementer;

  /**
   * Gets the host SPI.
   *
   * @return the host SPI
   */
  public ALlHostImplementation getHostSpi() {
    return hostSpi;
  }

  /**
   * Sets the host SPI.
   *
   * @param hostSpi the host SPI to set
   */
  public void setHostSpi(ALlHostImplementation hostSpi) {
    this.hostSpi = hostSpi;
  }

  /**
   * Gets the host implementer.
   *
   * @return the hostImplementer
   */
  public Class<ILlClientCommInterface> getHostImplementer() {
    return hostImplementer;
  }

  /**
   * Instantiates a new host implementation spi.
   *
   * @param hostSpi the host SPI
   * @param class1 the host implementer
   */
  @SuppressWarnings( "unchecked" )
  public HostImplementationSpi(ALlHostImplementation hostSpi,
      Class<? extends ILlClientCommInterface> class1) {
    super();
    this.hostSpi = hostSpi;
    this.hostImplementer = (Class<ILlClientCommInterface>) class1;
  }

  /**
   * Instantiates a new host implementation SPI.
   */
  public HostImplementationSpi() {
    return;
  }

  /**
   * Sets the host implementer.
   *
   * @param hostImplementer the hostImplementer to set
   */
  public void setHostImplementer(Class<ILlClientCommInterface> hostImplementer) {
    this.hostImplementer = hostImplementer;
  }


}
