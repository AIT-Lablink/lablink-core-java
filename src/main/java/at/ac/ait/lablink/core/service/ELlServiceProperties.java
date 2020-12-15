//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service;

/**
 * Enum for Lablink service properties.
 */
public enum ELlServiceProperties {

  /** The prop mqtt dp name. */
  PROP_MQTT_DP_NAME("DataPointName"),

  /** The prop mqtt dp identifier. */
  PROP_MQTT_DP_IDENTIFIER("DataPointIdentifier"),

  /** The prop mqtt dp unit. */
  PROP_MQTT_DP_UNIT("DataPointUnit"),

  /** The prop mqtt dp description. */
  PROP_MQTT_DP_DESCRIPTION("DataPointDescription"),

  /** The prop coap server resource path. */
  PROP_COAP_RESOURCE_PATH("CoapResourcePath"),

  /** The prop coap server resource path. */
  PROP_COAP_RESOURCE_TITLE("CoapResourceTile"),

  /** The prop coap server resource path. */
  PROP_COAP_RESOURCE_MIN("CoapResourceMin"),

  /** The prop coap server resource path. */
  PROP_COAP_RESOURCE_MAX("CoapResourceMax"),

  /** The prop coap server resource path. */
  PROP_COAP_RESOURCE_DESCRIPTION("CoapResourceDescription"),

  /** The prop coap server resource path. */
  PROP_COAP_RESOURCE_IS_OBSERVABLE("CoapResourceIsObservable"),

  /** The prop coap server resource path. */
  PROP_COAP_SERVER_PORT("CoapServerPort"),

  /** The prop simulink service port index. */
  PROP_SIMULINK_SERVICE_PORT_INDEX("SimulinkServicePortIndex"),

  /** The prop simulink service port direction. */
  PROP_SIMULINK_SERVICE_PORT_DIRECTION("SimulinkServicePortDirection"),

  /** The prop simulink service description. */
  PROP_SIMULINK_SERVICE_DESCRIPTION("SimulinkServiceDescription"),

  /** The prop simulink service unit. */
  PROP_SIMULINK_SERVICE_UNIT("SimulinkServiceUnit");

  /** The value. */
  private String value;

  /**
   * Instantiates a new modbus watch item types.
   *
   * @param val the val
   */
  private ELlServiceProperties(String val) {
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
  public static ELlServiceProperties getFromId(String id) {
    for (ELlServiceProperties type : ELlServiceProperties.values()) {
      if (type.getId().equals(id)) {
        return type;
      }
    }
    System.out.println("Invalid Id '" + id + "'.");
    return null;
  }
}
