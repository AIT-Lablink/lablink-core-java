//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client;

/**
 * The Enum ELlClientProperties.
 */
public enum ELlClientAdvProperties {

  /** The prop mqtt dp name. */
  ADD_PROP_MQTT_SCHEDULER_CLASS("MQTTSchedulerClass");

  /** The value. */
  private String value;

  /**
   * Instantiates a new modbus watch item types.
   *
   * @param val the val
   */
  private ELlClientAdvProperties(String val) {
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
  public static ELlClientAdvProperties getFromId(String id) {
    for (ELlClientAdvProperties type : ELlClientAdvProperties.values()) {
      if (type.getId().equals(id)) {
        return type;
      }
    }
    System.out.println("Invalid Id '" + id + "'.");
    return null;
  }
}
