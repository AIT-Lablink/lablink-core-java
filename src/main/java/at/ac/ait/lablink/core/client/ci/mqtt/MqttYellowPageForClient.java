//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ci.mqtt;

/**
 * MQTT yellow pages client.
 */
public class MqttYellowPageForClient {

  /** The client description. */
  private String clientDescription;

  /** The client comm interface. */
  private String clientCommInterface;

  /** The client name. */
  private String clientName;

  /** The group name. */
  private String groupName;

  /** The application id. */
  private String applicationId;

  /** The data points. */
  private MqttYellowPageForDataPoint[] datapoints;

  /**
   * Instantiates a new mqtt yellow pages client.
   */
  public MqttYellowPageForClient() {
    return;
  }

  /**
   * Instantiates a new mqtt yellow page for client.
   *
   * @param cdesc the cdesc
   * @param cinterface the cinterface
   * @param cname the client name
   * @param gname the group name
   * @param appname the application identifier
   * @param dpyellowpages array of data point yellow pages
   */
  public MqttYellowPageForClient(String cdesc, String cinterface, String cname, String gname,
      String appname, MqttYellowPageForDataPoint[] dpyellowpages) {
    this.setClientDescription(cdesc);
    this.setClientCommInterface(cinterface);
    this.setClientName(cname);
    this.setGroupName(gname);
    this.setApplicationId(appname);
    this.setDatapoints(dpyellowpages);
  }


  /**
   * Instantiates a new mqtt yellow page for client.
   *
   * @param cdesc A short description for the client's functionality
   * @param cinterface the current communication interface (transport)
   * @param cname the name of the client
   * @param gname the group name of the client
   * @param appname the application name of the client
   */
  public MqttYellowPageForClient(String cdesc, String cinterface, String cname, String gname,
      String appname) {
    this.setClientDescription(cdesc);
    this.setClientCommInterface(cinterface);
    this.setClientName(cname);
    this.setGroupName(gname);
    this.setApplicationId(appname);
  }

  /**
   * Gets the client name.
   *
   * @return the clientName
   */
  public String getClientName() {
    return clientName;
  }

  /**
   * Sets the client name.
   *
   * @param clientName the clientName to set
   */
  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  /**
   * Gets the group name.
   *
   * @return the groupName
   */
  public String getGroupName() {
    return groupName;
  }

  /**
   * Sets the group name.
   *
   * @param groupName the groupName to set
   */
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  /**
   * Gets the application id.
   *
   * @return the applicationId
   */
  public String getApplicationId() {
    return applicationId;
  }

  /**
   * Sets the application id.
   *
   * @param applicationId the applicationId to set
   */
  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * Gets the datapoints.
   *
   * @return the datapoints
   */
  public MqttYellowPageForDataPoint[] getDatapoints() {
    return datapoints;
  }

  /**
   * Sets the datapoints.
   *
   * @param datapoints the datapoints to set
   */
  public void setDatapoints(MqttYellowPageForDataPoint[] datapoints) {
    this.datapoints = datapoints;
  }

  /**
   * Gets the client description.
   *
   * @return the clientDescription
   */
  public String getClientDescription() {
    return clientDescription;
  }

  /**
   * Sets the client description.
   *
   * @param clientDescription the clientDescription to set
   */
  public void setClientDescription(String clientDescription) {
    this.clientDescription = clientDescription;
  }

  /**
   * Gets the client comm interface.
   *
   * @return the clientCommInterface
   */
  public String getClientCommInterface() {
    return clientCommInterface;
  }

  /**
   * Sets the client comm interface.
   *
   * @param clientCommInterface the clientCommInterface to set
   */
  public void setClientCommInterface(String clientCommInterface) {
    this.clientCommInterface = clientCommInterface;
  }

}
