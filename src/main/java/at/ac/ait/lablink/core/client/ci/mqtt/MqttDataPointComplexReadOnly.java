//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ci.mqtt;

import at.ac.ait.lablink.core.service.ELlServiceProperties;
import at.ac.ait.lablink.core.service.IImplementedService;
import at.ac.ait.lablink.core.service.LlServiceComplex;
import at.ac.ait.lablink.core.service.datapoint.IDataPoint;
import at.ac.ait.lablink.core.service.datapoint.IDataPointNotifier;
import at.ac.ait.lablink.core.service.datapoint.IDataPointService;
import at.ac.ait.lablink.core.service.datapoint.impl.ComplexReadonlyDataPoint;
import at.ac.ait.lablink.core.service.datapoint.payloads.ISimpleValue;
import at.ac.ait.lablink.core.service.types.Complex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Implementation of data point for type Complex.
 */
public class MqttDataPointComplexReadOnly implements IMqttDataPoint, IImplementedService<Complex> {

  private static final Logger logger = LogManager.getLogger("MqttDataPointComplexReadOnly");

  private ComplexReadonlyDataPoint dataPoint;
  private MqttYellowPageForDataPoint yellowpage;

  private LlServiceComplex service;

  /**
   * Constructor.
   *
   * @param clientService client service
   */
  public MqttDataPointComplexReadOnly(LlServiceComplex clientService) {
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

    dataPoint = new ComplexReadonlyDataPoint(Arrays.asList(dpname, dpidentifier), dpdesc, dpunit);

    dataPoint.setNotifier(new IDataPointNotifier<Complex>() {
      @Override
      public void requestValueUpdate(IDataPoint<Complex> dataPnt) {
        dataPoint.setValue(service.get());
        getLogger().debug("Read request '" + dataPnt.getProps().getName() + "'.");
      }

      @Override
      public void valueSetNotifier(IDataPoint<Complex> dataPnt, ISimpleValue<Complex> setValue) {
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
  public Complex getValue() {
    return this.dataPoint.getValue();
  }

  @Override
  public boolean setValue(Complex newval) {
    return false;
  }

  @Override
  public Class<Complex> getServiceDataTypeClass() {
    return this.service.getServiceDataTypeClass();
  }

}
