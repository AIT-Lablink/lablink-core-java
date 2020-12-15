//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client;

import at.ac.ait.lablink.core.service.LlService;
import at.ac.ait.lablink.core.service.LlServicePseudo;

import java.util.Map;

/**
 * Interface for Lablink client logic.
 */
public interface ILlClientLogic {

  /**
   * Gets the properties.
   *
   * @return the properties
   */
  public Map<ELlClientProperties, String> getProperties();

  /**
   * Gets the adv properties.
   *
   * @return the adv properties
   */
  public Map<ELlClientAdvProperties, Object> getAdvProperties();

  /**
   * Gets the services.
   *
   * @return the services
   */
  public Map<String, LlService> getServices();

  /**
   * Gets the pseudo services.
   *
   * @return the pseudo services
   */
  public Map<String, LlServicePseudo> getPseudoServices();

  /**
   * Gets the property.
   *
   * @param key the key
   * @return the property
   */
  public String getProperty(ELlClientProperties key);

  /**
   * Gets the adv property.
   *
   * @param key the key
   * @return the adv property
   */
  public Object getAdvProperty(ELlClientAdvProperties key);

  public String getHostImplementationSp();

  /**
   * Gets the yellow page json.
   *
   * @return the yellow page json
   */
  public String getYellowPageJson();

  /**
   * Checks if is pseudo client.
   *
   * @return true, if is pseudo client
   */
  public boolean isPseudoClient();
}
