//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.rd;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.ArrayUtils;

/**
 * Meta information for resource discovery.
 */
@JsonPropertyOrder({"clientScope", "clientIdentification", "clientCommInterface", "clientTransport",
    "clientEncoding", "clientName", "clientDescription", "clientJsonClass", "clientJsonClass"})
public class ResourceDiscoveryClientMeta {

  /** The client identification. */
  private String clientIdentification;

  /** The client comm interface. */
  private String clientCommInterface;

  /** The client name. */
  private String clientName;

  /** The client description. */
  private String clientDescription;

  /** The client scope. */
  private String[] clientScope;

  /** The client json. */
  private Object clientJson;

  /** The client json class. */
  private Class clientJsonClass;

  /** The client transport. */
  private String clientTransport;

  /** The client encoding. */
  private String clientEncoding;

  /**
   * Instantiates a new resource discovery meta.
   */
  @JsonPropertyOrder({"clientScope", "clientIdentification", "clientCommInterface",})
  public ResourceDiscoveryClientMeta() {}

  /**
   * Instantiates a new resource discovery meta.
   *
   * @param clientId the unique client identification
   * @param clientCi the client communication interface
   * @param clientName the client name
   * @param clientDescription the client description
   * @param ypJson the yellow page JSON
   * @param jsonClass the class for deserializing the client JSON
   * @param clientScope the client scope
   */
  public ResourceDiscoveryClientMeta(String clientId, String clientCi, String clientName,
      String clientDescription, Object ypJson, Class jsonClass, String... clientScope) {
    setClientIdentification(clientId);
    setClientCommInterface(clientCi);
    setClientName(clientName);
    setClientDescription(clientDescription);
    setClientJson(ypJson);
    setClientJsonClass(jsonClass);
    setClientScope(clientScope);
  }

  /**
   * Instantiates a new resource discovery meta.
   *
   * @param clientId the unique client identification
   * @param clientCi the client communication interface
   * @param clientName the client name
   */
  public ResourceDiscoveryClientMeta(String clientId, String clientCi, String clientName) {
    setClientIdentification(clientId);
    setClientCommInterface(clientCi);
    setClientName(clientName);
  }



  /**
   * Gets the client identification.
   *
   * @return the clientIdentification
   */
  public String getClientIdentification() {
    return clientIdentification;
  }

  /**
   * Sets the client identification.
   *
   * @param clientIdentification the clientIdentification to set
   */
  public void setClientIdentification(String clientIdentification) {
    this.clientIdentification =
        (clientIdentification.replaceAll("[^a-zA-Z0-9.-]", "")).toLowerCase();
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
   * Gets the client json.
   *
   * @return the clientJson
   */
  public Object getClientJson() {
    return clientJson;
  }

  /**
   * Sets the client json.
   *
   * @param clientJson the clientJson to set
   */
  public void setClientJson(Object clientJson) {
    this.clientJson = clientJson;
  }

  /**
   * Gets the client json class.
   *
   * @return the clientJsonClass
   */
  public String getClientJsonClassName() {
    return clientJsonClass.getName();
  }

  /**
   * Gets the c json class.
   *
   * @return the cJsonClass
   */
  public Class getcJsonClass() {
    return clientJsonClass;
  }

  /**
   * Sets the c json class.
   *
   * @param jsonClass the new c json class
   */
  public void setcJsonClass(Class jsonClass) {
    this.clientJsonClass = jsonClass;
  }

  /**
   * Gets the client json class.
   *
   * @return the clientJsonClass
   */
  public Class getClientJsonClass() {
    return clientJsonClass;
  }

  /**
   * Sets the client json class.
   *
   * @param clientJsonClass the clientJsonClass to set
   */
  public void setClientJsonClass(Class clientJsonClass) {
    this.clientJsonClass = clientJsonClass;
  }

  /**
   * Gets the client scope.
   *
   * @return the clientScope
   */
  public String[] getClientScope() {
    return clientScope;
  }

  /**
   * Sets the client scope.
   *
   * @param clientScope the clientScope to set
   */
  public void setClientScope(String[] clientScope) {

    if (ArrayUtils.isEmpty(clientScope)) {
      this.clientScope = new String[] {"GLOBAL"};
    } else {
      this.clientScope = clientScope;
    }
  }

  /**
   * Gets the client transport.
   *
   * @return the clientTransport
   */
  public String getClientTransport() {
    return clientTransport;
  }

  /**
   * Sets the client transport.
   *
   * @param clientTransport the clientTransport to set
   */
  public void setClientTransport(String clientTransport) {
    this.clientTransport = clientTransport;
  }

  /**
   * Gets the client encoding.
   *
   * @return the clientEncoding
   */
  public String getClientEncoding() {
    return clientEncoding;
  }

  /**
   * Sets the client encoding.
   *
   * @param clientEncoding the clientEncoding to set
   */
  public void setClientEncoding(String clientEncoding) {
    this.clientEncoding = clientEncoding;
  }
}
