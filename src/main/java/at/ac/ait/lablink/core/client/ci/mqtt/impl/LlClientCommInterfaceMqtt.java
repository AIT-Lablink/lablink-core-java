//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.ci.mqtt.impl;

import com.google.auto.service.AutoService;

import at.ac.ait.lablink.core.Configuration;
import at.ac.ait.lablink.core.client.ELlClientAdvProperties;
import at.ac.ait.lablink.core.client.ELlClientProperties;
import at.ac.ait.lablink.core.client.ELlClientStates;
import at.ac.ait.lablink.core.client.ILlClientLogic;
import at.ac.ait.lablink.core.client.ci.ELlClientCommInterfaces;
import at.ac.ait.lablink.core.client.ci.ILlClientCommInterface;
import at.ac.ait.lablink.core.client.ci.mqtt.IMqttDataPoint;
import at.ac.ait.lablink.core.client.ci.mqtt.MqttYellowPageForClient;
import at.ac.ait.lablink.core.client.ci.mqtt.MqttYellowPageForDataPoint;
import at.ac.ait.lablink.core.client.ex.ClientNotReadyException;
import at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException;
import at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException;
import at.ac.ait.lablink.core.connection.ILlConnection;
import at.ac.ait.lablink.core.connection.impl.LlConnectionFactory;
import at.ac.ait.lablink.core.rd.ResourceDiscoveryClientMeta;
import at.ac.ait.lablink.core.service.IImplementedService;
import at.ac.ait.lablink.core.service.LlServiceString;
import at.ac.ait.lablink.core.service.datapoint.IDataPointService;
import at.ac.ait.lablink.core.service.datapoint.impl.DataPointServiceImpl;
import at.ac.ait.lablink.core.service.sync.consumer.ISyncConsumer;
import at.ac.ait.lablink.core.service.sync.consumer.impl.SyncClientServiceImpl;
import at.ac.ait.lablink.core.spi.ALlHostImplementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MQTT implementation of the Lablink client communication interface.
 */
@AutoService(ILlClientCommInterface.class)

@ALlHostImplementation(accessName = MqttCommInterfaceUtility.SP_ACCESS_NAME,
    description = "Provides an implementation of the MQTT.")

public class LlClientCommInterfaceMqtt implements ILlClientCommInterface {

  /** The client logic. */
  private ILlClientLogic clientLogic = null;

  /** The rd meta. */
  private ResourceDiscoveryClientMeta rdMeta;

  /** The yellowpage. */
  private MqttYellowPageForClient yellowpage;

  /** The gen yellowpage. */
  private String genYellowpage;

  /** The dpyellowpages. */
  private List<MqttYellowPageForDataPoint> dpyellowpages =
      new ArrayList<MqttYellowPageForDataPoint>();

  /** The Constant LOG. */
  private static final Logger LOG = LogManager.getLogger("LlClientCommInterfaceMqtt");

  /** The datapoints. */
  private Map<String, IMqttDataPoint> datapoints = new HashMap<String, IMqttDataPoint>();

  /** The holds the implemented services. */
  private Map<String, IImplementedService> impservices = new HashMap<String, IImplementedService>();

  /** The lab link. */
  private ILlConnection lablink = null;

  /** The lab link dp service. */
  private IDataPointService lablinkDpService = null;

  /** The lab link sync service. */
  private SyncClientServiceImpl lablinkSyncService;

  /** The ll sync consumer. */
  private ISyncConsumer llSyncConsumer;

  // /**
  // * Instantiates a new Mqtt client communication interface.
  // *
  // * @param clogic the clogic
  // */
  // public LlClientCommInterfaceMqtt(ILlClientLogic clogic) {
  // this.clientLogic = clogic;
  // // this.state = ELlClientStates.LABLINK_CLIENT_INTERFACE_STATE_INSTANTIATED;
  // }

  public LlClientCommInterfaceMqtt() {

  }

  /**
   * Create yellow page IDataPoint.
   *
   * @throws DataTypeNotSupportedException the data type not supported exception
   */
  private void createYellowPageDp() throws DataTypeNotSupportedException {
    MqttYellowPageDp yellowPageDp =
        new MqttYellowPageDp(MqttCommInterfaceUtility.MQTT_YELLOW_PAGES_DP_NAME, true);
    MqttCommInterfaceUtility.addYellowPageDataPointProperties(yellowPageDp);
    IMqttDataPoint datapoint = MqttDataPointFactory.getDataPoint(yellowPageDp);

    datapoint.registerDataPoint(this.lablinkDpService);
    this.datapoints.put(MqttCommInterfaceUtility.MQTT_YELLOW_PAGES_DP_NAME, datapoint);
    this.dpyellowpages.add(datapoint.getYellowPage());
  }

  /**
   * Creates the data points.
   *
   * @throws NoServicesInClientLogicException the no services in client logic exception
   * @throws DataTypeNotSupportedException the data type not supported exception
   */
  private void createDataPoints()
      throws NoServicesInClientLogicException, DataTypeNotSupportedException {

    if (this.clientLogic.getServices().size() > 0) {
      this.clientLogic.getServices().forEach((key, val) -> {
        LOG.debug("Processing the service [{}]...", key);
        try {
          IMqttDataPoint datapoint = MqttDataPointFactory.getDataPoint(val);
          datapoint.registerDataPoint(this.lablinkDpService);
          this.datapoints.put(key, datapoint);
          this.dpyellowpages.add(datapoint.getYellowPage());
          LOG.debug(">>>>>>> Yellow page size is {}.", this.dpyellowpages.size());
          IImplementedService iservice = (IImplementedService) datapoint;
          this.impservices.put(key, iservice);

        } catch (DataTypeNotSupportedException ex) {
          LOG.error(ex.getMessage() + " when processing the service [{}].", key);
        }

      });
    } else {
      throw new NoServicesInClientLogicException();
    }

    // if (this.clientLogic.getServices().size() > 0) {
    // for (Map.Entry<String, LlService> service : this.clientLogic.getServices().entrySet()) {
    // LOG.debug("Processing the service {}...", service.getKey());
    // IMqttDataPoint datapoint = MqttDataPointFactory.getDataPoint(service.getValue());
    // datapoint.registerDataPoint(this.lablinkDpService);
    // this.datapoints.put(service.getKey(), datapoint);
    // this.dpyellowpages.add(datapoint.getYellowPage());
    // LOG.debug(">>>>>>> Yellow page size is {}.", this.dpyellowpages.size());
    //
    // // Add implementation service
    // IImplementedService iservice = (IImplementedService) datapoint;
    // this.impservices.put(service.getKey(), iservice);
    //
    // }
    // } else {
    // throw new NoServicesInClientLogicException();
    // }

  }

  /**
   * Setup lab link client.
   *
   * @throws ConfigurationException the configuration exception
   */
  private void setupLablinkClient() throws ConfigurationException {
    // Lablink

    final String clientName =
        this.clientLogic.getProperty(ELlClientProperties.PROP_MQTT_CLIENT_NAME);

    final String groupName =
        this.clientLogic.getProperty(ELlClientProperties.PROP_MQTT_CLIENT_GROUP_NAME);

    final String scenarioName =
        this.clientLogic.getProperty(ELlClientProperties.PROP_MQTT_CLIENT_APP_NAME);

    final String llPropUri =
        this.clientLogic.getProperty(ELlClientProperties.PROP_MQTT_CLIENT_APP_PROPERTIES_URI);

    final String llSyncPropUri =
        this.clientLogic.getProperty(ELlClientProperties.PROP_MQTT_CLIENT_SYNC_HOST_PROPERTIES_URI);


    LOG.info("Setting up Mqtt interface...");

    this.lablink = LlConnectionFactory.getDefaultConnectionController(
        Configuration.LABLINK_AIT, scenarioName, groupName, clientName, llPropUri);

    LOG.info("Lablink Mqtt client will be initialized with {}, {}, {}, {}.'",
        Configuration.LABLINK_AIT, groupName, clientName, llPropUri);

    LOG.info("Setting up IDataPoint services...");

    this.lablinkDpService = new DataPointServiceImpl(lablink, null);

    LOG.info("IDataPoint Service created.");

    LOG.info("Setting up Sync services...");

    PropertiesConfiguration lablinkCfg = new PropertiesConfiguration(llSyncPropUri);

    this.lablinkSyncService = new SyncClientServiceImpl(this.lablink, lablinkCfg);

    this.llSyncConsumer = (ISyncConsumer) this.clientLogic
        .getAdvProperty(ELlClientAdvProperties.ADD_PROP_MQTT_SCHEDULER_CLASS);

    this.lablinkSyncService.registerSyncConsumer(this.llSyncConsumer);

    LOG.info("MQTT setup Done.");

    setupResourceDiscoveryMeta(scenarioName, groupName, clientName);

  }

  /**
   * Setup resource discovery meta.
   *
   * @param sname the sname
   * @param gname the gname
   * @param cname the cname
   */
  private void setupResourceDiscoveryMeta(String sname, String gname, String cname) {

    LOG.info("Generating yellow pages for {} services...", this.dpyellowpages.size());

    final String ypCliDesc =
        this.clientLogic.getProperty(ELlClientProperties.PROP_YELLOW_PAGE_CLIENT_DESCRIPTION);

    this.yellowpage = new MqttYellowPageForClient(ypCliDesc,
        ELlClientCommInterfaces.LABLINK_COMM_INTERFACE_MQTT.toString(), cname, gname, sname);

    // Create a unique ID for client to be used by the resource discovery service
    String clientId = Configuration.LABLINK_AIT + Configuration.LABLINK_RD_SEPERATOR + sname
        + Configuration.LABLINK_RD_SEPERATOR + gname + Configuration.LABLINK_RD_SEPERATOR + cname;

    // Create the resource discovery meta data for RD service
    this.rdMeta = new ResourceDiscoveryClientMeta(clientId,
        ELlClientCommInterfaces.LABLINK_COMM_INTERFACE_MQTT.toString(), cname, ypCliDesc,
        this.yellowpage, this.yellowpage.getClass());

    LOG.debug("Client will be identified as [{}] in resource discovery service.",
        this.rdMeta.getClientIdentification());

  }

  /**
   * Setup yellow page.
   */
  private void setupYellowPage() {
    this.yellowpage.setDatapoints(this.dpyellowpages.toArray(new MqttYellowPageForDataPoint[0]));
  }

  /** 
   * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#init()
   */
  @Override
  public void init() throws ClientNotReadyException, ConfigurationException,
      NoServicesInClientLogicException, DataTypeNotSupportedException {
    this.setupLablinkClient();
    this.createDataPoints();
    this.createYellowPageDp();
    this.setupYellowPage();
    LOG.info("Client initialized successfully.");
  }

  /** 
   * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#start()
   */
  @Override
  public void start() throws ClientNotReadyException {
    LOG.info("Establising connection to Mqtt...");
    this.lablink.connect();

    LOG.info("Starting MqttSyn service...");
    this.lablinkSyncService.start();
  }

  /** 
   * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#stop()
   */
  @Override
  public void stop() throws ClientNotReadyException {

  }

  /** 
   * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#shutdown()
   */
  @Override
  public void shutdown() throws ClientNotReadyException {

  }

  /** 
   * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#create()
   */
  @Override
  public void create() throws ClientNotReadyException {

  }

  /** 
   * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#getState()
   */
  @Override
  public ELlClientStates getState() {
    return null;
  }

  /** 
   * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#getYellowPageJson()
   */
  @Override
  public String getYellowPageJson() {
    if (this.genYellowpage == null) {
      ObjectMapper jsonObjectMapper = new ObjectMapper();

      try {
        // this.genYellowpage = jsonObjectMapper.writeValueAsString(this.yellowpage);
        // this.genYellowpage =
        // jsonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.yellowpage);
        this.genYellowpage = jsonObjectMapper.writeValueAsString(this.yellowpage);
      } catch (JsonProcessingException ex) {
        LOG.error(ex.getMessage());
      }
    }
    return this.genYellowpage;
  }

  /**
   * The Class MqttYellowPageDp.
   */
  class MqttYellowPageDp extends LlServiceString {

    /**
     * Instantiates a new mqtt yellow page dp.
     *
     * @param mqttYellowPagesDpName the mqtt yellow pages dp name
     * @param readonly the readonly
     */
    public MqttYellowPageDp(String mqttYellowPagesDpName, boolean readonly) {
      super(mqttYellowPagesDpName, readonly);
    }

    /** 
     * @see at.ac.ait.lablink.core.service.LlService#get()
     */
    @Override
    public String get() {
      return getYellowPageJson();
    }

    /** 
     * @see at.ac.ait.lablink.core.service.LlService#set(java.lang.Object)
     */
    @Override
    public boolean set(String newval) {
      return false;
    }

  }

  /** 
   * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#getResourceDiscoveryMeta()
   */
  @Override
  public ResourceDiscoveryClientMeta getResourceDiscoveryMeta() {
    this.rdMeta.setClientJson(this.yellowpage);
    return this.rdMeta;
  }


  /** 
   * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#getImplementedServices()
   */
  @Override
  public Map<String, IImplementedService> getImplementedServices() {
    return this.impservices;
  }

  public static String getPublicName() {
    return "LablinkMQTTImplementation";
  }

  @Override
  public void setClientLogic(ILlClientLogic clogic) {
    this.clientLogic = clogic;
  }

}
