//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection;

import java.util.List;

/**
 * Data Bean for the identification of a client. It contains all identification items for a
 * Lablink client.
 */
public class ClientIdentifier {

  private final List<String> prefix;
  private final String appId;
  private final String groupId;
  private final String clientId;

  /**
   * Constructor.
   *
   * @param prefix   Prefix list of the client that is used for separation.
   * @param appId    Identifier for the application where the client should be used
   * @param groupId  Identifier of the group where the client is added.
   * @param clientId Identifier of the client itself.
   */
  public ClientIdentifier(List<String> prefix, String appId, String groupId, String clientId) {
    this.prefix = prefix;
    this.appId = appId;
    this.groupId = groupId;
    this.clientId = clientId;
  }

  /**
   * Get the prefix of the identifier.
   *
   * @return A list of prefix elements.
   */
  public List<String> getPrefix() {
    return prefix;
  }

  /**
   * Get the application identifier of the client.
   *
   * @return the application identifier.
   */
  public String getAppId() {
    return appId;
  }

  /**
   * Get the group identifier of the client.
   *
   * @return the groupID.
   */
  public String getGroupId() {
    return groupId;
  }

  /**
   * Get the client identifier.
   *
   * @return clientID.
   */
  public String getClientId() {
    return clientId;
  }
}
