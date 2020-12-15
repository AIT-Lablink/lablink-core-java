//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client;

/**
 * The Enum ELlClientProperties.
 */
public enum ELlClientProperties {

  /** The prop mqtt dp name. */
  PROP_YELLOW_PAGE_CLIENT_DESCRIPTION("YellowPageClientDescripton"),

  /** The prop mqtt dp name. */
  PROP_MQTT_CLIENT_NAME("MQTTClientName"),

  /** The prop mqtt dp identifier. */
  PROP_MQTT_CLIENT_GROUP_NAME("MQTTGroupName"),

  /** The prop mqtt dp unit. */
  PROP_MQTT_CLIENT_APP_NAME("MQTTAppName"),

  /** The prop mqtt dp description. */
  PROP_MQTT_CLIENT_APP_PROPERTIES_URI("MQTTAppPropertiesURI"),

  /** The prop mqtt dp description. */
  PROP_MQTT_CLIENT_SYNC_HOST_PROPERTIES_URI("MQTTSyncHostPropertiesURI"),

  /** The prop coap server resource path. */
  PROP_COAP_SERVER_PORT("CoapServerPort"),



  /** The prop simulink client name. */
  PROP_SIMULINK_RD_CLIENT_NAME("SimulinkRdCLientName"),

  /** The prop simulink client description. */
  PROP_SIMULINK_RD_CLIENT_DESCRIPTION("SimulinkRdCLientDescription"),

  /** The prop simulink model name. */
  PROP_SIMULINK_START_MATLAB("SimulinkStartMatlab"),

  /** The prop simulink model name. */
  PROP_SIMULINK_MODEL_NAME("SimulinkModelName"),

  /** The prop simulink model path. */
  PROP_SIMULINK_MODEL_PATH("SimulinkModelPath");

  /** The value. */
  private String value;

  /**
   * Instantiates a new modbus watch item types.
   *
   * @param val the val
   */
  private ELlClientProperties(String val) {
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
  public static ELlClientProperties getFromId(String id) {
    for (ELlClientProperties type : ELlClientProperties.values()) {
      if (type.getId().equals(id)) {
        return type;
      }
    }
    System.out.println("Invalid Id '" + id + "'.");
    return null;
  }
}
