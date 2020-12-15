//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ci.mqtt;

import at.ac.ait.lablink.core.service.ELlServiceProperties;
import at.ac.ait.lablink.core.service.IImplementedService;
import at.ac.ait.lablink.core.service.IServiceStateChangeNotifier;
import at.ac.ait.lablink.core.service.LlServiceDouble;
import at.ac.ait.lablink.core.service.datapoint.IDataPoint;
import at.ac.ait.lablink.core.service.datapoint.IDataPointNotifier;
import at.ac.ait.lablink.core.service.datapoint.IDataPointService;
import at.ac.ait.lablink.core.service.datapoint.impl.DoubleDataPoint;
import at.ac.ait.lablink.core.service.datapoint.payloads.ISimpleValue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Double data point for MQTT.
 */
public class MqttDataPointDouble implements IMqttDataPoint, IImplementedService<Double>,
    IServiceStateChangeNotifier<LlServiceDouble, Double> {

  /** The Constant logger. */
  private static final Logger logger = LogManager.getLogger("MqttDataPointDouble");

  /** The data point. */
  private DoubleDataPoint dataPoint;
  private MqttYellowPageForDataPoint yellowpage;

  /** The service. */
  private LlServiceDouble service;

  /**
   * Instantiates a new mqtt data point double.
   *
   * @param clientService the client service
   */
  public MqttDataPointDouble(LlServiceDouble clientService) {
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

    dataPoint = new DoubleDataPoint(Arrays.asList(dpname, dpidentifier), dpdesc, dpunit);

    dataPoint.setNotifier(new IDataPointNotifier<Double>() {
      @Override
      public void requestValueUpdate(IDataPoint<Double> dataPnt) {
        dataPoint.setValue(service.get());
        getLogger().debug("Read request '" + dataPnt.getProps().getName() + "'.");
      }

      @Override
      public void valueSetNotifier(IDataPoint<Double> dataPnt, ISimpleValue<Double> setValue) {
        service.set(setValue.getValue());
        getLogger().debug("For datapoint [{}], the value is updated to [{}].",
            dataPnt.getProps().getName(), setValue.getValue());
      }
    });
    this.yellowpage =
        new MqttYellowPageForDataPoint(dpname, dpidentifier, dpdesc, dpunit, false, getDataType());

    logger.debug("IDataPoint created with Id={}, Name={}, Desc={} and Unit={}.", dpidentifier,
        dpname, dpdesc, dpunit);

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

  @Override
  public Double getValue() {
    return this.dataPoint.getValue();
  }

  @Override
  public boolean setValue(Double newval) {
    boolean result = false;
    result = this.service.set(newval);

    if (result) {
      this.dataPoint.setValue(newval);
    }

    return result;
  }

  @Override
  public Class<Double> getServiceDataTypeClass() {
    return this.service.getServiceDataTypeClass();
  }

  @Override
  public void stateChanged(LlServiceDouble service, Double oldVal, Double newVal) {
    logger.debug(
        "Notifier -> State Changed for " + service.getName() + " from " + oldVal + " to " + newVal);
    this.dataPoint.setValue(newVal);
  }

}
