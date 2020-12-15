//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ci;

import at.ac.ait.lablink.core.client.ELlClientStates;
import at.ac.ait.lablink.core.client.ILlClientLogic;
import at.ac.ait.lablink.core.client.ex.ClientNotReadyException;
import at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException;
import at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException;
import at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException;
import at.ac.ait.lablink.core.client.ex.PseudoHostException;
import at.ac.ait.lablink.core.rd.ResourceDiscoveryClientMeta;
import at.ac.ait.lablink.core.service.IImplementedService;

import org.apache.commons.configuration.ConfigurationException;

import java.util.Map;


/**
 * Interface for the Lablink client communication interface.
 */
public interface ILlClientCommInterface {

  /**
   * Inits the.
   *
   * @throws ClientNotReadyException the client not ready exception
   * @throws ConfigurationException the configuration exception
   * @throws NoServicesInClientLogicException the no services in client logic exception
   * @throws DataTypeNotSupportedException the data type not supported exception
   * @throws PseudoHostException the pseudo host exception
   */
  public void init() throws ClientNotReadyException, ConfigurationException,
      NoServicesInClientLogicException, DataTypeNotSupportedException, PseudoHostException;

  /**
   * Start.
   *
   * @throws ClientNotReadyException the client not ready exception
   * @throws PseudoHostException the pseudo host exception
   */
  public void start() throws ClientNotReadyException, PseudoHostException;

  /**
   * Stop.
   *
   * @throws ClientNotReadyException the client not ready exception
   * @throws PseudoHostException the pseudo host exception
   */
  public void stop() throws ClientNotReadyException, PseudoHostException;

  /**
   * Shutdown.
   *
   * @throws ClientNotReadyException the client not ready exception
   */
  public void shutdown() throws ClientNotReadyException;

  /**
   * Creates the.
   *
   * @throws ClientNotReadyException the client not ready exception
   * @throws NoSuchCommInterfaceException the no such comm interface exception
   */
  public void create() throws ClientNotReadyException, NoSuchCommInterfaceException;

  /**
   * Gets the state.
   *
   * @return the state
   */
  public ELlClientStates getState();

  /**
   * Gets the yellow page json.
   *
   * @return the yellow page json
   */
  public String getYellowPageJson();

  /**
   * Gets the resource discovery meta.
   *
   * @return the resource discovery meta
   */
  public ResourceDiscoveryClientMeta getResourceDiscoveryMeta();

  /**
   * Gets the implemented services.
   *
   * @return the implemented services
   */
  public Map<String, IImplementedService> getImplementedServices();

  /**
   * Sets the client logic.
   *
   * @param clogic the new client logic
   */
  public void setClientLogic(ILlClientLogic clogic);

}
