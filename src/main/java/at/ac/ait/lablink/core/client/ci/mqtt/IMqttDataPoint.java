//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ci.mqtt;

import at.ac.ait.lablink.core.service.datapoint.IDataPointService;


public interface IMqttDataPoint {
  public String getName();

  public void registerDataPoint(IDataPointService dpService);

  public String getDataType();

  public MqttYellowPageForDataPoint getYellowPage();
}
