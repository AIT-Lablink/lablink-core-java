//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ci.mqtt;

import at.ac.ait.lablink.core.service.ELlServiceProperties;
import at.ac.ait.lablink.core.service.IImplementedService;
import at.ac.ait.lablink.core.service.LlServiceDouble;
import at.ac.ait.lablink.core.service.datapoint.IDataPoint;
import at.ac.ait.lablink.core.service.datapoint.IDataPointNotifier;
import at.ac.ait.lablink.core.service.datapoint.IDataPointService;
import at.ac.ait.lablink.core.service.datapoint.impl.DoubleReadonlyDataPoint;
import at.ac.ait.lablink.core.service.datapoint.payloads.ISimpleValue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Implementation of data point for type Double.
 */
public class MqttDataPointDoubleReadOnly implements IMqttDataPoint, IImplementedService<Double> {

  private static final Logger logger = LogManager.getLogger("MqttDataPointDoubleReadOnly");

  private DoubleReadonlyDataPoint dataPoint;
  private MqttYellowPageForDataPoint yellowpage;

  private LlServiceDouble service;

  /**
   * Constructor.
   *
   * @param clientService client service
   */
  public MqttDataPointDoubleReadOnly(LlServiceDouble clientService) {
    this.service = clientService;

    createDataPoint(clientService.getProperty(ELlServiceProperties.PROP_MQTT_DP_IDENTIFIER),
        clientService.getProperty(ELlServiceProperties.PROP_MQTT_DP_DESCRIPTION),
        clientService.getProperty(ELlServiceProperties.PROP_MQTT_DP_NAME),
        clientService.getProperty(ELlServiceProperties.PROP_MQTT_DP_UNIT));
  }

  private Logger getLogger() {
    return logger;
  }

  private void createDataPoint(String dpidentifier, String dpdesc, String dpname, String dpunit) {

    dataPoint = new DoubleReadonlyDataPoint(Arrays.asList(dpname, dpidentifier), dpdesc, dpunit);

    dataPoint.setNotifier(new IDataPointNotifier<Double>() {
      @Override
      public void requestValueUpdate(IDataPoint<Double> dataPnt) {
        dataPoint.setValue(service.get());
        getLogger().debug("Read request '" + dataPnt.getProps().getName() + "'.");
      }

      @Override
      public void valueSetNotifier(IDataPoint<Double> dataPnt, ISimpleValue<Double> setValue) {
        // service.set(setValue.getValue());
        getLogger().error(dataPnt.getProps().getName() + " IDataPoint is readonly, can't write.");
      }
    });

    this.yellowpage =
        new MqttYellowPageForDataPoint(dpname, dpidentifier, dpdesc, dpunit, true, getDataType());

    logger.debug("Readonly IDataPoint created with Id={}, Name={}, Desc={} and Unit={}.",
        dpidentifier, dpname, dpdesc, dpunit);

  }

  @Override
  public String getName() {
    return this.dataPoint.getProps().getName();
  }

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
    return false;
  }

  @Override
  public Class<Double> getServiceDataTypeClass() {
    return this.service.getServiceDataTypeClass();
  }

}
