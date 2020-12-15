//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ci.mqtt.impl;

import at.ac.ait.lablink.core.client.ci.mqtt.IMqttDataPoint;
import at.ac.ait.lablink.core.client.ci.mqtt.MqttDataPointBoolean;
import at.ac.ait.lablink.core.client.ci.mqtt.MqttDataPointBooleanReadOnly;
import at.ac.ait.lablink.core.client.ci.mqtt.MqttDataPointDouble;
import at.ac.ait.lablink.core.client.ci.mqtt.MqttDataPointDoubleReadOnly;
import at.ac.ait.lablink.core.client.ci.mqtt.MqttDataPointLong;
import at.ac.ait.lablink.core.client.ci.mqtt.MqttDataPointLongReadOnly;
import at.ac.ait.lablink.core.client.ci.mqtt.MqttDataPointString;
import at.ac.ait.lablink.core.client.ci.mqtt.MqttDataPointStringReadOnly;
import at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException;
import at.ac.ait.lablink.core.service.ELlServiceDataTypes;
import at.ac.ait.lablink.core.service.LlService;
import at.ac.ait.lablink.core.service.LlServiceBoolean;
import at.ac.ait.lablink.core.service.LlServiceDouble;
import at.ac.ait.lablink.core.service.LlServiceLong;
import at.ac.ait.lablink.core.service.LlServiceString;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A factory for creating MqttDataPoint objects.
 */
public class MqttDataPointFactory {

  private static Logger logger = LogManager.getLogger("MqttDataPointFactory");

  /**
   * Gets the data point.
   *
   * @param forService the for service
   * @return the data point
   * @throws DataTypeNotSupportedException the data type not supported exception
   */
  public static IMqttDataPoint getDataPoint(LlService forService)
      throws DataTypeNotSupportedException {
    IMqttDataPoint datapoint = null;
    boolean readonly = forService.isReadOnly();
    ELlServiceDataTypes servicetype = forService.getServiceDataType();

    logger.debug("Processing request for creating a [{}] IDataPoint...", servicetype);

    switch (servicetype) {
      case SERVICE_DATATYPE_DOUBLE:
        if (readonly) {
          datapoint = new MqttDataPointDoubleReadOnly((LlServiceDouble) forService);
        } else {
          datapoint = new MqttDataPointDouble((LlServiceDouble) forService);
        }
        break;
      case SERVICE_DATATYPE_LONG:
        if (readonly) {
          datapoint = new MqttDataPointLongReadOnly((LlServiceLong) forService);
          logger.debug("MQTT IDataPoint [{}] created for the service [{}].",
              "MqttDataPointLongReadOnly", forService.getName());
        } else {
          datapoint = new MqttDataPointLong((LlServiceLong) forService);
          logger.debug("MQTT IDataPoint [{}] created for the service [{}].", "MqttDataPointLong",
              forService.getName());
        }
        break;
      case SERVICE_DATATYPE_STRING:
        if (readonly) {
          datapoint = new MqttDataPointStringReadOnly((LlServiceString) forService);
          logger.debug("MQTT IDataPoint [{}] created for the service [{}].",
              "MqttDataPointStringReadOnly", forService.getName());
        } else {
          datapoint = new MqttDataPointString((LlServiceString) forService);
          logger.debug("MQTT IDataPoint [{}] created for the service [{}].", "MqttDataPointString",
              forService.getName());
        }
        break;
      case SERVICE_DATATYPE_BOOLEAN:
        if (readonly) {
          datapoint = new MqttDataPointBooleanReadOnly((LlServiceBoolean) forService);
          logger.debug("MQTT IDataPoint [{}] created for the service [{}].",
              "MqttDataPointBooleanReadOnly", forService.getName());
        } else {
          datapoint = new MqttDataPointBoolean((LlServiceBoolean) forService);
          logger.debug("MQTT IDataPoint [{}] created for the service [{}].", "MqttDataPointBoolean",
              forService.getName());
        }
        break;
      default:
        logger.error("Data type not supported [{}].", servicetype);
        throw new DataTypeNotSupportedException("Data type not supported '" + servicetype + "'.");
    }


    return datapoint;

  }
}
