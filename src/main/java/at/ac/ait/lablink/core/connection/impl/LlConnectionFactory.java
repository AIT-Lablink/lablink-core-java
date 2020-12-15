//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.impl;

import at.ac.ait.lablink.core.connection.ILlConnection;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.apache.commons.configuration.Configuration;

import java.util.Collections;

/**
 * Factory for generating the connection core.
 *
 * <p>This allows the abstraction and an change of the connection core afterwords without changing
 * the usage of the clients using the connection core
 */
public class LlConnectionFactory {

  /**
   * Generate the default connection core
   *
   * @param prefix   Prefix of the application (e.g, at.ac.ait)
   * @param appId    identifier of the application
   * @param groupId  Group ID of the client using the connection core
   * @param clientId Client ID of the client using the connection core
   * @return the new generated core
   */
  public static ILlConnection getDefaultConnectionController(String prefix, String appId,
                                                                 String groupId, String clientId) {

    return LlConnectionFactory
        .getConnectionController("MQTT", prefix, appId, groupId, clientId, (Configuration) null);
  }

  /**
   * Generate the default connection core
   *
   * @param prefix   Prefix of the application (e.g, at.ac.ait)
   * @param appId    identifier of the application
   * @param groupId  Group ID of the client using the connection core
   * @param clientId Client ID of the client using the connection core
   * @param config   set a configuration for the connection module
   * @return the new generated core
   */
  public static ILlConnection getDefaultConnectionController(String prefix, String appId,
                                                                 String groupId, String clientId,
                                                                 Configuration config) {

    return LlConnectionFactory
        .getConnectionController("MQTT", prefix, appId, groupId, clientId, config);
  }

  /**
   * Generate the default connection core
   *
   * @param prefix         Prefix of the application (e.g, at.ac.ait)
   * @param appId          identifier of the application
   * @param groupId        Group ID of the client using the connection core
   * @param clientId       Client ID of the client using the connection core
   * @param configFileName configuration loading using a file name
   * @return the new generated core
   */
  public static ILlConnection getDefaultConnectionController(String prefix, String appId,
                                                                 String groupId, String clientId,
                                                                 String configFileName) {

    return LlConnectionFactory
        .getConnectionController("MQTT", prefix, appId, groupId, clientId, configFileName);
  }

  /**
   * Generate a connection core
   *
   * @param core     string identifier to match the core to be generated, actually not used
   * @param appId    identifier of the application
   * @param prefix   Prefix of the application (e.g, at.ac.ait)
   * @param groupId  Group ID of the client using the connection core
   * @param clientId Client ID of the client using the connection core
   * @param config   set a configuration for the connection module
   * @return the new generated core
   */
  public static ILlConnection getConnectionController(String core, String prefix, String appId,
                                                          String groupId, String clientId,
                                                          Configuration config) {

    if (core.equals("MQTT")) {
      return new LlConnectionController(Collections.singletonList(prefix), appId, groupId,
          clientId, config);
    } else {
      throw new LlCoreRuntimeException("No implementation for '" + core + "' is known.");
    }
  }

  /**
   * Generate a connection core
   *
   * @param core           string identifier to match the core to be generated, actually not used
   * @param appId          identifier of the application
   * @param prefix         Prefix of the application (e.g, at.ac.ait)
   * @param groupId        Group ID of the client using the connection core
   * @param clientId       Client ID of the client using the connection core
   * @param configFileName configuration loading using a file name
   * @return the new generated core
   */
  public static ILlConnection getConnectionController(String core, String prefix, String appId,
                                                          String groupId, String clientId,
                                                          String configFileName) {

    if (core.equals("MQTT")) {
      return new LlConnectionController(Collections.singletonList(prefix), appId, groupId,
          clientId, configFileName);
    } else {
      throw new LlCoreRuntimeException("No implementation for '" + core + "' is known.");
    }
  }
}
