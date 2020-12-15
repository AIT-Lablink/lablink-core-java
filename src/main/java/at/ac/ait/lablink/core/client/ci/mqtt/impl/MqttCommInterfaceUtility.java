//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ci.mqtt.impl;

import at.ac.ait.lablink.core.client.ELlClientAdvProperties;
import at.ac.ait.lablink.core.client.ELlClientProperties;
import at.ac.ait.lablink.core.client.ci.mqtt.MqttYellowPageForClient;
import at.ac.ait.lablink.core.client.ex.CommInterfaceNotSupportedException;
import at.ac.ait.lablink.core.client.impl.LlClient;
import at.ac.ait.lablink.core.service.ELlServiceProperties;
import at.ac.ait.lablink.core.service.LlService;
import at.ac.ait.lablink.core.service.LlServiceString;
import at.ac.ait.lablink.core.service.sync.consumer.ISyncConsumer;
import at.ac.ait.lablink.core.utility.Utility;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * MQTT implementation of utility functions for the communication interface.
 */
public class MqttCommInterfaceUtility {

  public static final String SP_ACCESS_NAME = "LL_HOST_MQTT_IMPL";

  /** The Constant MQTT_YELLOW_PAGES_DP_NAME. */
  public static final String MQTT_YELLOW_PAGES_DP_NAME = "ClientYellowPage";

  /** The Constant MQTT_YELLOW_PAGES_DP_ID. */
  public static final String MQTT_YELLOW_PAGES_DP_ID = "ClientYellowPagesJson";

  /** The Constant MQTT_YELLOW_PAGES_DP_DESC. */
  public static final String MQTT_YELLOW_PAGES_DP_DESC =
      "Provides a JSON representation of the available DataPoints";

  /** The Constant MQTT_YELLOW_PAGES_DP_UNIT. */
  public static final String MQTT_YELLOW_PAGES_DP_UNIT = "NONE";

  /**
   * Adds the client properties.
   *
   * @param client the client object
   * @param cdesc a short description of the client for yellow pages
   * @param appId the application id
   * @param groupName the group name
   * @param clientName the client name
   * @param appPropUri the application properties URI, supports $env$ parsing
   * @param syncHostPropUri the sync host properties URI, supports $env$ parsing
   * @param sync the syncConsumer implemented for the client
   * @throws CommInterfaceNotSupportedException the communication interface is not supported
   *         exception
   */
  public static void addClientProperties(LlClient client, String cdesc, String appId,
      String groupName, String clientName, String appPropUri, String syncHostPropUri,
      ISyncConsumer sync) throws CommInterfaceNotSupportedException {

    if (client.getHostImplementationSp().equals(SP_ACCESS_NAME)) {


      client.addProperty(ELlClientProperties.PROP_YELLOW_PAGE_CLIENT_DESCRIPTION, cdesc);

      client.addProperty(ELlClientProperties.PROP_MQTT_CLIENT_NAME, clientName);

      client.addProperty(ELlClientProperties.PROP_MQTT_CLIENT_GROUP_NAME, groupName);

      client.addProperty(ELlClientProperties.PROP_MQTT_CLIENT_APP_NAME, appId);

      client.addProperty(ELlClientProperties.PROP_MQTT_CLIENT_APP_PROPERTIES_URI,
          Utility.parseWithEnvironmentVariable(appPropUri));

      client.addProperty(ELlClientProperties.PROP_MQTT_CLIENT_SYNC_HOST_PROPERTIES_URI,
          Utility.parseWithEnvironmentVariable(syncHostPropUri));

      client.addAdvProperty(ELlClientAdvProperties.ADD_PROP_MQTT_SCHEDULER_CLASS, sync);
    } else {
      throw new CommInterfaceNotSupportedException();
    }

  }

  /**
   * Adds the yellow page data point properties.
   *
   * @param datapoint the datapoint
   */
  public static void addYellowPageDataPointProperties(LlServiceString datapoint) {
    addDataPointProperties(datapoint, MQTT_YELLOW_PAGES_DP_NAME, MQTT_YELLOW_PAGES_DP_DESC,
        MQTT_YELLOW_PAGES_DP_ID, MQTT_YELLOW_PAGES_DP_UNIT);
  }

  /**
   * Adds the data point properties.
   *
   * @param datapoint the datapoint
   * @param name the name
   * @param description the description
   * @param id the id
   * @param unit the unit
   */
  public static void addDataPointProperties(LlService datapoint, String name, String description,
      String id, String unit) {
    // Add properties to the service
    datapoint.addProperty(ELlServiceProperties.PROP_MQTT_DP_NAME, name);
    datapoint.addProperty(ELlServiceProperties.PROP_MQTT_DP_DESCRIPTION, description);
    datapoint.addProperty(ELlServiceProperties.PROP_MQTT_DP_IDENTIFIER, id);
    datapoint.addProperty(ELlServiceProperties.PROP_MQTT_DP_UNIT, unit);
  }

  /**
   * Gets the yellow page pojo.
   *
   * @param client the client
   * @return the yellow page pojo
   * @throws JsonParseException the json parse exception
   * @throws JsonMappingException the json mapping exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws CommInterfaceNotSupportedException the comm interface not supported exception
   */
  public static MqttYellowPageForClient getYellowPagePojo(LlClient client)
      throws JsonParseException, JsonMappingException, IOException,
      CommInterfaceNotSupportedException {

    MqttYellowPageForClient pojo = null;

    if (client.getHostImplementationSp().equals(SP_ACCESS_NAME)) {
      ObjectMapper jsonObjectMapper = new ObjectMapper();

      // jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      pojo = jsonObjectMapper.readValue(client.getYellowPageJson(), MqttYellowPageForClient.class);
    } else {
      throw new CommInterfaceNotSupportedException();
    }
    return pojo;
  }
}
