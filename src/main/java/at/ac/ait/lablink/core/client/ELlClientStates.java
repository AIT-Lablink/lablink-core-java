//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client;

/**
 * Enum for Lablink client states.
 */
public enum ELlClientStates {


  /** The lablink client interface state not instantiated. */
  LABLINK_CLIENT_INTERFACE_STATE_NOTINSTANTIATED("NONE"),

  /** The lablink client interface state instantiated. */
  LABLINK_CLIENT_INTERFACE_STATE_INSTANTIATED("CREATE"),

  /** The lablink client interface state initialized. */
  LABLINK_CLIENT_INTERFACE_STATE_INITIALIZED("INIT"),

  /** The lablink client interface state started. */
  LABLINK_CLIENT_INTERFACE_STATE_STARTED("START"),

  /** The lablink client interface state shutdown. */
  LABLINK_CLIENT_INTERFACE_STATE_SHUTDOWN("SHUTDOWN");


  /** The value. */
  private String value;

  /**
   * Instantiates a new ll client interfaces.
   *
   * @param val the val
   */
  private ELlClientStates(String val) {
    this.value = val;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return this.value;
  }

  /**
   * Gets the from id.
   *
   * @param id the id
   * @return the from id
   */
  public static ELlClientStates getFromId(String id) {
    for (ELlClientStates type : ELlClientStates.values()) {
      if (type.getId().equals(id)) {
        return type;
      }
    }
    System.out.println("Invalid Id '" + id + "'.");
    return null;
  }

}
