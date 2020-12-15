//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.impl;

public class LlClientUtility {

  private LlClient thisclient = null;

  public LlClientUtility(LlClient client) {
    this.thisclient = client;
  }

  public String getServiceName(String service) {
    return thisclient.getImplementedServices().get(service).getName();
  }

  @SuppressWarnings( "unchecked" )
  public boolean setServiceValue(String service, double val) {
    return thisclient.getImplementedServices().get(service).setValue(Double.valueOf(val));
  }

  @SuppressWarnings( "unchecked" )
  public boolean setServiceValue(String service, long val) {
    return thisclient.getImplementedServices().get(service).setValue(Long.valueOf(val));
  }

  @SuppressWarnings( "unchecked" )
  public boolean setServiceValue(String service, boolean val) {
    return thisclient.getImplementedServices().get(service).setValue(Boolean.valueOf(val));
  }

  @SuppressWarnings( "unchecked" )
  public boolean setServiceValue(String service, String val) {
    // return thisclient.getImplementedServices().get(service).setValue(val);
    return thisclient.getImplementedServices().get(service).setValue(val);
  }

  public double getServiceValueDouble(String service) {
    return (Double) thisclient.getImplementedServices().get(service).getValue();
  }

  public Long getServiceValueLong(String service) {
    return (Long) thisclient.getImplementedServices().get(service).getValue();
  }

  public Boolean getServiceValueBoolean(String service) {
    return (Boolean) thisclient.getImplementedServices().get(service).getValue();
  }

  public String getServiceValueString(String service) {
    return thisclient.getImplementedServices().get(service).getValue().toString();
  }

}
