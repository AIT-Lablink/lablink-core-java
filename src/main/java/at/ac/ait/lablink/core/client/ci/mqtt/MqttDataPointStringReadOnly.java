//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ci.mqtt;

import at.ac.ait.lablink.core.service.ELlServiceProperties;
import at.ac.ait.lablink.core.service.LlServiceString;
import at.ac.ait.lablink.core.service.datapoint.IDataPoint;
import at.ac.ait.lablink.core.service.datapoint.IDataPointNotifier;
import at.ac.ait.lablink.core.service.datapoint.IDataPointService;
import at.ac.ait.lablink.core.service.datapoint.impl.StringReadonlyDataPoint;
import at.ac.ait.lablink.core.service.datapoint.payloads.ISimpleValue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Read-only String data point for MQTT.
 */
public class MqttDataPointStringReadOnly implements IMqttDataPoint {

  /** The Constant logger. */
  private static final Logger logger = LogManager.getLogger("MqttDataPointStringReadOnly");

  /** The data point. */
  private StringReadonlyDataPoint dataPoint;
  private MqttYellowPageForDataPoint yellowpage;

  /** The service. */
  private LlServiceString service;

  /**
   * Instantiates a new mqtt data point string read only.
   *
   * @param clientService the client service
   */
  public MqttDataPointStringReadOnly(LlServiceString clientService) {
    this.service = clientService;

    createDataPoint(clientService.getProperty(ELlServiceProperties.PROP_MQTT_DP_IDENTIFIER),
        clientService.getProperty(ELlServiceProperties.PROP_MQTT_DP_DESCRIPTION),
        clientService.getProperty(ELlServiceProperties.PROP_MQTT_DP_NAME),
        clientService.getProperty(ELlServiceProperties.PROP_MQTT_DP_UNIT));
  }

  /**
   * Gets the logger.
   *
   * @return the logger
   */
  private Logger getLogger() {
    return logger;
  }

  /**
   * Creates the data point.
   *
   * @param dpidentifier the dpidentifier
   * @param dpdesc the dpdesc
   * @param dpname the dpname
   * @param dpunit the dpunit
   */
  private void createDataPoint(String dpidentifier, String dpdesc, String dpname, String dpunit) {

    dataPoint = new StringReadonlyDataPoint(Arrays.asList(dpname, dpidentifier), dpdesc, dpunit);

    dataPoint.setNotifier(new IDataPointNotifier<String>() {
      @Override
      public void requestValueUpdate(IDataPoint<String> dataPnt) {
        dataPoint.setValue(service.get());
        getLogger().debug("Read request '" + dataPnt.getProps().getName() + "'.");
      }

      @Override
      public void valueSetNotifier(IDataPoint<String> dataPnt, ISimpleValue<String> setValue) {
        // service.set(setValue.getValue());
        getLogger().error(dataPnt.getProps().getName() + " IDataPoint is readonly, can't write.");
      }
    });
    this.yellowpage =
        new MqttYellowPageForDataPoint(dpname, dpidentifier, dpdesc, dpunit, true, getDataType());

    logger.debug("Readonly IDataPoint created with Id={}, Name={}, Desc={} and Unit={}.",
        dpidentifier, dpname, dpdesc, dpunit);

  }

  /** 
   * @see at.ac.ait.lablink.core.client.ci.mqtt.IMqttDataPoint#getName()
   */
  @Override
  public String getName() {
    return this.dataPoint.getProps().getName();
  }

  /** 
   * @see at.ac.ait.lablink.core.client.ci.mqtt.IMqttDataPoint#registerDataPoint(
   * at.ac.ait.lablink.core.service.datapoint.IDataPointService
   * )
   */
  @Override
  public void registerDataPoint(IDataPointService dpService) {
    dpService.registerDatapoint(dataPoint);
  }

  @Override
  public String getDataType() {
    return dataPoint.getValue().getClass().getSimpleName();
  }

  @Override
  public MqttYellowPageForDataPoint getYellowPage() {
    return this.yellowpage;
  }


}
