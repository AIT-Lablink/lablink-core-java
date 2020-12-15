//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ci;

/**
 * Enum for Lablink client properties.
 */
public enum ELlClientCommInterfaces {

  /** The lablink interface mqtt. */
  LABLINK_COMM_INTERFACE_MQTT("MQTTCommInterface"),

  /** The lablink interface coap. */
  LABLINK_COMM_INTERFACE_COAP("COAPCommInterface");

  /** The value. */
  private String value;

  /**
   * Instantiates a new ll client interfaces.
   *
   * @param val the val
   */
  private ELlClientCommInterfaces(String val) {
    this.value = val.toUpperCase();
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
  public static ELlClientCommInterfaces getFromId(String id) {
    for (ELlClientCommInterfaces type : ELlClientCommInterfaces.values()) {
      if (type.getId().equals(id)) {
        return type;
      }
    }
    System.out.println("Invalid Id '" + id + "'.");
    return null;
  }
}
