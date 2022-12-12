//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

/**
 * Enum for Lablink service data types.
 */
public enum ELlServiceDataTypes {

  /** The service datatype double. */
  SERVICE_DATATYPE_DOUBLE("Double"),

  /** The service datatype long. */
  SERVICE_DATATYPE_LONG("Long"),

  /** The service datatype string. */
  SERVICE_DATATYPE_STRING("String"),

  /** The service datatype boolean. */
  SERVICE_DATATYPE_BOOLEAN("Boolean"),

  /** The service datatype complex. */
  SERVICE_DATATYPE_COMPLEX("Complex"),

  /** The service datatype object. */
  SERVICE_DATATYPE_OBJECT("Object");

  /** The value. */
  private String value;


  /**
   * Instantiates a new e ll service data types.
   *
   * @param val the val
   */
  private ELlServiceDataTypes(String val) {
    this.value = val.toUpperCase();
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getValue() {
    return this.value;
  }

  /**
   * Gets the from id.
   *
   * @param id the id
   * @return the from id
   */
  public static ELlServiceDataTypes getFromId(String id) {
    for (ELlServiceDataTypes type : ELlServiceDataTypes.values()) {
      if (type.getValue().equalsIgnoreCase(id)) {
        return type;
      }
    }
    System.out.println("Invalid Id '" + id + "'.");
    return null;
  }
}
