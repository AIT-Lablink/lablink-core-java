//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.is;

import asg.cliche.Command;
import asg.cliche.Param;

import at.ac.ait.lablink.core.client.ELlClientProperties;
import at.ac.ait.lablink.core.client.ex.InvalidCastForServiceValueException;
import at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException;
import at.ac.ait.lablink.core.client.impl.LlClient;
import at.ac.ait.lablink.core.service.ELlServiceProperties;
import at.ac.ait.lablink.core.service.IImplementedService;
import at.ac.ait.lablink.core.service.LlService;
import at.ac.ait.lablink.core.service.LlServicePseudo;

import java.util.Map;
import java.util.Map.Entry;

/**
 * The Lablink client shell implementation.
 */
public class LlClientIShell {

  /** The for service. */
  private String forService = null;

  /** The Constant SERVICE_DOES_NOT_EXISTS. */
  public static final String SERVICE_DOES_NOT_EXISTS =
      "ERROR: A service with this name does not exists.";

  /** The client. */
  private LlClient client;

  /**
   * Instantiates a new ll client I shell.
   *
   * @param client the client
   */
  public LlClientIShell(LlClient client) {
    this.client = client;
  }

  /**
   * Sets the for service.
   *
   * @param service the service
   * @return the string
   */
  @Command(description = "Set service name for subsequent commands.", name = "set-srvc-name",
      abbrev = "ssn")
  public String setForService(
      @Param(name = "service-name", description = "Name of the service") String service) {

    String reply = null;

    if (service.equals("null")) {
      this.forService = null;
      reply = "Service name cleared.";
    } else {
      this.forService = service;
      reply = "Service name is set to [" + service + "] for subsequent 'f' prefixed commands.";
    }

    return reply;

  }

  /**
   * Read current state of service whose name is saved with {@code ssn}.
   * @return reply message
   * @throws InvalidCastForServiceValueException the invalid cast for service value exception
   */
  @Command(description = "Read current state of service whose name is saved with ssn.",
      name = "get-srvc-val", abbrev = "fgsv")
  public String getValueFixed() throws InvalidCastForServiceValueException {
    String reply = null;
    if (this.forService == null) {
      reply = "No service name set. Please use ssn command to set a name.";
    } else {
      reply = this.forService + " = [" + this.getValue(forService) + "]";
    }
    return reply;
  }

  /**
   * Gets the value.
   *
   * @param service the service
   * @return the value
   * @throws InvalidCastForServiceValueException the invalid cast for service value exception
   */
  @Command(description = "Read current state of a service.", name = "get-srvc-val", abbrev = "gsv")
  public String getValue(
      @Param(name = "service-name", description = "Name of the service") String service)
      throws InvalidCastForServiceValueException {
    String reply = null;
    try {
      reply = this.client.getServiceValueAuto(service);
    } catch (Exception ex) {
      reply = SERVICE_DOES_NOT_EXISTS;
    }

    return reply;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  @Command(description = "Read client name.", name = "get-name", abbrev = "gn")
  public String getName() {
    return this.client.getName();
  }

  /**
   * Gets the desc.
   *
   * @return the desc
   */
  @Command(description = "Read client desciption.", name = "get-desc", abbrev = "gd")
  public String getDesc() {
    return this.client.getProperty(ELlClientProperties.PROP_YELLOW_PAGE_CLIENT_DESCRIPTION);
  }

  /**
   * Gets the protocol.
   *
   * @return the protocol
   */
  @Command(description = "Read client desciption.", name = "get-prot", abbrev = "gp")
  public String getProtocol() {
    return this.client.getHostImplementationSp();
  }

  /**
   * Gets the yellow pages.
   *
   * @return the yellow pages
   */
  @Command(description = "Read client desciption.", name = "get-ypage", abbrev = "gyp")
  public String getYellowPages() {
    return this.client.getYellowPageJson();
  }

  /**
   * Gets the service type.
   *
   * @param service the service
   * @return the service type
   */
  @Command(description = "Read datatype of the service.", abbrev = "gst", name = "get-srvc-type")
  public String getServiceType(
      @Param(name = "service-name", description = "Name of the service") String service) {
    String reply = SERVICE_DOES_NOT_EXISTS;

    if (this.client.isServiceExists(service)) {
      reply = this.client.getServices().get(service).getServiceDataType().toString();
    }

    return reply;
  }

  /**
   * Sets the service val dbl.
   *
   * @param service the service
   * @param val the val
   * @return the string
   * @throws ServiceIsNotRegisteredWithClientException the service is not registered with client
   *         exception
   */
  @Command(description = "Set service value to a double.", abbrev = "svd",
      name = "set-srvc-val-dbl")
  public String setServiceValDbl(
      @Param(name = "service-name", description = "Name of the service") String service,
      @Param(name = "value-double", description = "Double value") double val)
      throws ServiceIsNotRegisteredWithClientException {

    String reply = SERVICE_DOES_NOT_EXISTS;

    if (this.client.isServiceExists(service)) {
      reply = (this.client.setServiceValue(service, val) ? "Success" : "Failed");
    }

    return reply;
  }

  /**
   * Sets the service val dbl.
   *
   * @param service the service
   * @param val the val
   * @return the string
   * @throws ServiceIsNotRegisteredWithClientException the service is not registered with client
   *         exception
   */
  @Command(description = "Set service value to a double.", abbrev = "sd",
      name = "new-set-srvc-val-dbl")
  @SuppressWarnings( "unchecked" )
  public String setNewServiceValDbl(
      @Param(name = "service-name", description = "Name of the service") String service,
      @Param(name = "value-double", description = "Double value") double val)
      throws ServiceIsNotRegisteredWithClientException {

    String reply = SERVICE_DOES_NOT_EXISTS;

    if (this.client.isServiceExists(service)) {
      reply =
          (this.client.getImplementedServices().get(service).setValue(val) ? "Success" : "Failed");
    }

    return reply;
  }

  /**
   * Sets the service val lng.
   *
   * @param service the service
   * @param val the val
   * @return the string
   * @throws ServiceIsNotRegisteredWithClientException the service is not registered with client
   *         exception
   */
  @Command(description = "Set service value to a long.", abbrev = "svl", name = "set-srvc-val-lng")
  public String setServiceValLng(
      @Param(name = "service-name", description = "Name of the service") String service,
      @Param(name = "value-long", description = "Long value") long val)
      throws ServiceIsNotRegisteredWithClientException {

    String reply = SERVICE_DOES_NOT_EXISTS;

    if (this.client.isServiceExists(service)) {
      reply = (this.client.setServiceValue(service, val) ? "Success" : "Failed");
    }

    return reply;
  }

  /**
   * Sets the service val bool.
   *
   * @param service the service
   * @param val the val
   * @return the string
   * @throws ServiceIsNotRegisteredWithClientException the service is not registered with client
   *         exception
   */
  @Command(description = "Set service value to a boolean.", abbrev = "svb",
      name = "set-srvc-val-bol")
  public String setServiceValBol(
      @Param(name = "service-name", description = "Name of the service") String service,
      @Param(name = "boolean-val", description = "Boolean value") boolean val)
      throws ServiceIsNotRegisteredWithClientException {

    String reply = SERVICE_DOES_NOT_EXISTS;

    if (this.client.isServiceExists(service)) {
      reply = (this.client.setServiceValue(service, val) ? "Success" : "Failed");
    }

    return reply;
  }

  /**
   * Sets the service val str.
   *
   * @param service the service
   * @param val the val
   * @return the string
   * @throws ServiceIsNotRegisteredWithClientException the service is not registered with client
   *         exception
   */
  @Command(description = "Set service value to a string.", abbrev = "svs",
      name = "set-srvc-val-str")
  public String setServiceValStr(
      @Param(name = "service-name", description = "Name of the service") String service,
      @Param(name = "string-val", description = "String value") String val)
      throws ServiceIsNotRegisteredWithClientException {

    String reply = SERVICE_DOES_NOT_EXISTS;

    if (this.client.isServiceExists(service)) {
      reply = (this.client.setServiceValue(service, val) ? "Success" : "Failed");
    }

    return reply;
  }


  /**
   * List services.
   *
   * @return the string
   */
  @Command(description = "List all the registered services.", abbrev = "ls", name = "list-srvc")
  public String listServices() {

    String reply = SERVICE_DOES_NOT_EXISTS;
    int count = 0;

    reply = "Name\t\tDataType\t\tState\n";

    // for (Map.Entry<String, LlService> service : this.client.getServices().entrySet()) {
    for (Map.Entry<String, IImplementedService> service : this.client.getImplementedServices()
        .entrySet()) {
      reply +=
          service.getKey() + "\t" + service.getValue().getServiceDataTypeClass().getSimpleName()
              + "\t" + service.getValue().getValue().toString() + "\n";
      ++count;
    }

    if (count > 0) {
      reply += "\nFound " + count + " registered service(s).";
    } else {
      reply = "No registered services are found.";
    }

    return reply;
  }

  /**
   * List services.
   *
   * @return the string
   */
  @Command(description = "List all the registered psudo services.", abbrev = "lps",
      name = "list-srvc-psudo")
  public String listPsudoServices() {

    String reply = SERVICE_DOES_NOT_EXISTS;
    int count = 0;

    reply = "Name\t\tDataType\t\tState\n";

    for (Map.Entry<String, LlServicePseudo> service : this.client.getPseudoServices().entrySet()) {
      reply += service.getKey() + "\t" + service.getValue().getServiceDataType().toString() + "\t"
          + service.getValue().get().toString() + "\n";
      ++count;
    }

    if (count > 0) {
      reply += "\nFound " + count + " registered psudo service(s).";
    } else {
      reply = "No registered services are found.";
    }

    return reply;
  }

  /**
   * List service properties.
   *
   * @return the string
   */
  @Command(description = "List all the properties of registered services.", abbrev = "lpsp",
      name = "list-psudo-srvc-prop")
  public String listPseudoServiceProperties() {

    String reply = SERVICE_DOES_NOT_EXISTS;
    int count = 0;

    for (Map.Entry<String, LlServicePseudo> service : this.client.getPseudoServices().entrySet()) {
      reply = "Name\tDatatype\tValue\n";
      String sname = service.getKey();
      for (Entry<ELlServiceProperties, String> prop : service.getValue().getProperties()
          .entrySet()) {
        reply += sname + "." + prop.getKey().toString() + " = [" + prop.getValue() + "]\n";
      }
      ++count;
    }

    if (count > 0) {
      reply += "\nFound " + count + " psudo registered service(s).";
    } else {
      reply = "No registered psudo services are found.";
    }

    return reply;
  }

  /**
   * List service properties.
   *
   * @return the string
   */
  @Command(description = "List all the properties of registered services.", abbrev = "lsp",
      name = "list-srvc-prop")
  public String listServiceProperties() {

    String reply = SERVICE_DOES_NOT_EXISTS;
    int count = 0;

    reply = "Name\t\tValue\n";

    for (Map.Entry<String, LlService> service : this.client.getServices().entrySet()) {
      String sname = service.getKey();
      for (Entry<ELlServiceProperties, String> prop : service.getValue().getProperties()
          .entrySet()) {
        reply += sname + "." + prop.getKey().toString() + " = [" + prop.getValue() + "]\n";
      }
      ++count;
    }

    if (count > 0) {
      reply += "\nFound " + count + " registered service(s).";
    } else {
      reply = "No registered services are found.";
    }

    return reply;
  }


}
