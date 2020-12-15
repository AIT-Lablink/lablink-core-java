//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client;

/**
 * Enum for Lablink client pseudo hosts.
 */
public enum ELlClientPseudoHosts {

  /** The lablink client interface state not instantiated. */
  LABLINK_CLIENT_PSEUDO_HOST_SIMULINK("SIMULINK");

  /** The value. */
  private String value;

  /**
   * Instantiates a new ll client interfaces.
   *
   * @param val the val
   */
  private ELlClientPseudoHosts(String val) {
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
  public static ELlClientPseudoHosts getFromId(String id) {
    for (ELlClientPseudoHosts type : ELlClientPseudoHosts.values()) {
      if (type.getId().equals(id)) {
        return type;
      }
    }
    System.out.println("Invalid Id '" + id + "'.");
    return null;
  }

}
