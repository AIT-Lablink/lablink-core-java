//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.client.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import asg.cliche.ShellFactory;
import at.ac.ait.lablink.core.Configuration;
import at.ac.ait.lablink.core.client.ELlClientAdvProperties;
import at.ac.ait.lablink.core.client.ELlClientProperties;
import at.ac.ait.lablink.core.client.ELlClientStates;
import at.ac.ait.lablink.core.client.ILlClientFsmLogic;
import at.ac.ait.lablink.core.client.ILlClientLogic;
import at.ac.ait.lablink.core.client.LlClientFsm;
import at.ac.ait.lablink.core.client.ci.ILlClientCommInterface;
import at.ac.ait.lablink.core.client.ci.impl.LlClientCommInterfaceFactory;
import at.ac.ait.lablink.core.client.ci.mqtt.MqttYellowPageForClient;
import at.ac.ait.lablink.core.client.ex.ClientNotReadyException;
import at.ac.ait.lablink.core.client.ex.DataTypeNotSupportedException;
import at.ac.ait.lablink.core.client.ex.InvalidCastForServiceValueException;
import at.ac.ait.lablink.core.client.ex.NoServicesInClientLogicException;
import at.ac.ait.lablink.core.client.ex.NoSuchCommInterfaceException;
import at.ac.ait.lablink.core.client.ex.NoSuchPseudoHostException;
import at.ac.ait.lablink.core.client.ex.PseudoHostException;
import at.ac.ait.lablink.core.client.ex.ServiceIsNotRegisteredWithClientException;
import at.ac.ait.lablink.core.client.ex.ServiceTypeDoesNotMatchClientType;
import at.ac.ait.lablink.core.is.LlClientIShell;
import at.ac.ait.lablink.core.is.ShellUtility;
import at.ac.ait.lablink.core.rd.ResourceDiscoveryClientMeta;
import at.ac.ait.lablink.core.rd.ResourceDiscoveryPeriodicServer;
import at.ac.ait.lablink.core.service.ELlServiceDataTypes;
import at.ac.ait.lablink.core.service.IImplementedService;
import at.ac.ait.lablink.core.service.LlService;
import at.ac.ait.lablink.core.service.LlServicePseudo;
import at.ac.ait.lablink.core.utility.LlAddressUtility;
import at.ac.ait.lablink.core.utility.Utility;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;

// TODO: Auto-generated Javadoc
/**
 * Implementation of Lablink client base functionality, including the client
 * logic and communication interface.
 */
public final class LlClient implements ILlClientLogic, ILlClientCommInterface, ILlClientFsmLogic {

	/** The id. */
	private final long id = LlAddressUtility.getRandomClientId();

	/** The port on which this client's measures would be exposed to Prometheus. */
	private static final int PROMETHEUS_MEASURES_PORT = 7979;

	/** The logger. */
	private static Logger logger = LogManager.getLogger("LlClient");

	/** The ready. */
	private boolean ready = false;

	/** The pseudo client. */
	private boolean pseudoClient = false;

	/** The state. */
	private ELlClientStates state = ELlClientStates.LABLINK_CLIENT_INTERFACE_STATE_NOTINSTANTIATED;

	/** The REsource Discovery Server. */
	private ResourceDiscoveryPeriodicServer rdServer;

	/** The FSM. */
	private LlClientFsm fsm;

	/** The name. */
	private String name;

	/** The Yellow Pages for DP broadcast on LAN. */
	private MqttYellowPageForClient yellopages;

	/** The gen yellowpages. */
	private String genYellowpages;

	/** The ypages client scope. */
	private String[] ypagesClientScope;

	/** The properties. */
	private Map<ELlClientProperties, String> properties = new HashMap<ELlClientProperties, String>();

	/** The advanced properties. */
	private Map<ELlClientAdvProperties, Object> advProperties = new HashMap<ELlClientAdvProperties, Object>();

	/** The client logic services. */
	private Map<String, LlService> services = new HashMap<String, LlService>();

	/** The client logic Pseudo services. */
	private Map<String, LlServicePseudo> pseudoServices = new HashMap<String, LlServicePseudo>();

	/** The client interface types. */
	// private ELlClientCommInterfaces clientCommInterfaceType;

	private String hostImplementation;

	/** The client communication interface. */
	private ILlClientCommInterface clientCommInterface = null;

	/** The pseudo host type. */
	// private ELlClientPseudoHosts pseudoHostType;

	/** The sthread. */
	private Thread sthread;

	/** The shell. */
	private LlClientIShell shell;

	/** The show shell. */
	private boolean showShell = false;

	/** The Prometheus measures port. */
	private int prometheusMeasuresPort = PROMETHEUS_MEASURES_PORT;

	/** The Prometheus measures server. */
	private HTTPServer prometheusMeasuresServer;

	/** The run prometheus server. */
	private boolean runPrometheusServer = false;

	/**
	 * Instantiates a new ll client.
	 *
	 * @param cname     the name this client will be identified with
	 * @param hostSp    the access name of the host implementation to use
	 * @param giveShell the flag to indicated if ishell should be shown
	 * @param isPseudo  the is the flag indicating a pseudo client
	 * @param pmport    the port number on which the Prometheus measures will be
	 *                  available
	 * @param scope     scope
	 */
	public LlClient(String cname, String hostSp, boolean giveShell, boolean isPseudo, int pmport, String... scope) {
		if (isPseudo) {
			logger.info("PseudoClient will be instantiated with name={} and will work with the PseudoHost={}.", cname,
					hostSp);
		} else {
			logger.info("CustomClient will be instantiated with name={} and will work with the CustomHost={}.", cname,
					hostSp);
		}

		// this.clientCommInterfaceType = null;
		this.setName(cname);
		this.fsm = new LlClientFsm(this);
		this.showShell = giveShell;
		this.pseudoClient = isPseudo;
		// this.pseudoHostType = null;
		this.hostImplementation = hostSp;
		this.prometheusMeasuresPort = pmport;
	}

	/**
	 * Instantiates a new Lablink Client.
	 *
	 * @param cname     the name this client will be identified with
	 * @param hostSp    the access name of the host implementation to use
	 * @param giveShell the flag to indicated if ishell should be shown
	 * @param isPseudo  the is the flag indicating a pseudo client
	 * @param scope     scope
	 */
	public LlClient(String cname, String hostSp, boolean giveShell, boolean isPseudo, String... scope) {
		this(cname, hostSp, giveShell, isPseudo, PROMETHEUS_MEASURES_PORT, scope);
	}

	/**
	 * Start shell.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void startShell() throws IOException {

		this.shell = new LlClientIShell(this);

		this.sthread = new Thread() {
			@Override
			public void run() {
				try {
					ShellFactory.createConsoleShell(ShellUtility.SHELL_PROMT, ShellUtility.SHELL_WELCOME_MESSAGE, shell)
							.commandLoop();
				} catch (IOException ex) {
					logger.error(ex.getMessage());
				}
			}
		};

		sthread.setName("Shell");
		sthread.start();

	}

	/**
	 * Adds the service.
	 *
	 * @param service the service
	 * @throws ServiceTypeDoesNotMatchClientType the service type do not match
	 *                                           client type
	 */
	public void addService(LlService service) throws ServiceTypeDoesNotMatchClientType {
		if (this.isPseudoClient()) {
			throw new ServiceTypeDoesNotMatchClientType();
		} else {
			this.services.put(service.getName(), service);
			logger.debug("Service added with name=[{}], Readonly=[{}]", service.getName(), service.isReadOnly());
		}
	}

	/**
	 * Adds the service.
	 *
	 * @param service the service
	 * @throws ServiceTypeDoesNotMatchClientType the service type do not match
	 *                                           client type
	 */
	public void addService(LlServicePseudo service) throws ServiceTypeDoesNotMatchClientType {
		if (!this.isPseudoClient()) {
			throw new ServiceTypeDoesNotMatchClientType();
		} else {

			this.pseudoServices.put(service.getName(), service);
			logger.debug("Pseudo Service added with name=[{}], Readonly=[{}]", service.getName(), service.isReadOnly());
		}
	}

	/**
	 * Creates the client with the specified interface. This is the function where
	 * most of the work is being done of creating a client based on the specified
	 * interface.
	 *
	 * @throws ClientNotReadyException      the client not ready exception
	 * @throws NoSuchCommInterfaceException the no such communication interface
	 *                                      exception
	 */
	@Override
	public void create() throws ClientNotReadyException, NoSuchCommInterfaceException {
		this.fsm.transitionTo(LlClientFsm.EPossibleTransitionTriggers.CREATE);
	}

	/////////////////////////////////////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 *
	 * @return the properties
	 * @see at.ac.ait.lablink.core.client.ILlClientLogic#getProperties()
	 */
	@Override
	public Map<ELlClientProperties, String> getProperties() {
		return this.properties;
	}

	/**
	 * 
	 *
	 * @return the services
	 * @see at.ac.ait.lablink.core.client.ILlClientLogic#getServices()
	 */
	@Override
	public Map<String, LlService> getServices() {
		return this.services;
	}

	/**
	 * 
	 *
	 * @return the adv properties
	 * @see at.ac.ait.lablink.core.client.ILlClientLogic#getAdvProperties()
	 */
	@Override
	public Map<ELlClientAdvProperties, Object> getAdvProperties() {
		return this.advProperties;
	}

	/**
	 * Adds the property.
	 *
	 * @param property the property
	 * @param value    the value
	 */
	public void addProperty(ELlClientProperties property, String value) {
		this.properties.put(property, value);
		logger.debug("Client property={} value='{}' added.", property, value);
	}

	/**
	 * Adds the adv property.
	 *
	 * @param property the property
	 * @param value    the value
	 */
	public void addAdvProperty(ELlClientAdvProperties property, Object value) {
		this.advProperties.put(property, value);
		logger.debug("Advanced client property {} added.", property);
	}

	/**
	 * Gets the property.
	 *
	 * @param key the key
	 * @return the property
	 */
	@Override
	public String getProperty(ELlClientProperties key) {
		return this.properties.get(key);
	}

	/**
	 * Gets the adv property.
	 *
	 * @param key the key
	 * @return the adv property
	 */
	@Override
	public Object getAdvProperty(ELlClientAdvProperties key) {
		return this.advProperties.get(key);
	}

	/////////////////////////////////////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 *
	 * @throws ClientNotReadyException          the client not ready exception
	 * @throws ConfigurationException           the configuration exception
	 * @throws NoServicesInClientLogicException the no services in client logic
	 *                                          exception
	 * @throws DataTypeNotSupportedException    the data type not supported
	 *                                          exception
	 * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#init()
	 */
	@Override
	public void init() throws ClientNotReadyException, ConfigurationException, NoServicesInClientLogicException,
			DataTypeNotSupportedException {
		// checkReady();
		// clientCommInterface.init();
		this.fsm.transitionTo(LlClientFsm.EPossibleTransitionTriggers.INIT);
	}

	/**
	 * 
	 *
	 * @throws ClientNotReadyException the client not ready exception
	 * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#start()
	 */
	@Override
	public void start() throws ClientNotReadyException {
		// checkReady();
		this.fsm.transitionTo(LlClientFsm.EPossibleTransitionTriggers.START);
	}

	/**
	 * 
	 *
	 * @throws ClientNotReadyException the client not ready exception
	 * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#stop()
	 */
	@Override
	public void stop() throws ClientNotReadyException {
		// clientCommInterface.stop();
	}

	/**
	 * 
	 *
	 * @throws ClientNotReadyException the client not ready exception
	 * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#shutdown()
	 */
	@Override
	public void shutdown() throws ClientNotReadyException {
		this.fsm.transitionTo(LlClientFsm.EPossibleTransitionTriggers.SHUTDOWN);
	}

	/////////////////////////////////////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * Checks if is ready.
	 *
	 * @return the ready
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 * Sets the ready.
	 *
	 * @param ready the ready to set
	 */
	public void setReady(boolean ready) {
		this.ready = ready;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	private void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the resource discovery JSON.
	 *
	 * @return the resource discovery JSON
	 */
	private String getResourceDiscoveryJson() {
		return Utility.getResourceDiscoveryMetaJson(this.getResourceDiscoveryMeta());
	}

	/**
	 * Run Resource discovery advertisement.
	 */
	private void runRd() {
		logger.debug("Starting resource advertisement server...");

		try {
			this.rdServer = new ResourceDiscoveryPeriodicServer(getResourceDiscoveryMeta());
			this.rdServer.start();
			logger.debug("Resource advertisement started.");
		} catch (JsonGenerationException ex) {
			logger.error(ex.getMessage());
		} catch (JsonMappingException ex) {
			logger.error(ex.getMessage());
		} catch (IOException ex) {
			logger.error(ex.getMessage());
		}

		logger.debug("Resource advertisement now running.");

	}

	// ===========================================================================
	// CommInterface Logic
	// ===========================================================================

	/**
	 * 
	 *
	 * @return the state
	 * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#getState()
	 */
	@Override
	public ELlClientStates getState() {
		return this.clientCommInterface.getState();
	}

	/**
	 * 
	 *
	 * @return the yellow page JSON
	 * @see at.ac.ait.lablink.core.client.ILlClientLogic#getYellowPageJson()
	 */
	@Override
	public String getYellowPageJson() {
		return this.clientCommInterface.getYellowPageJson();
	}

	/**
	 * 
	 *
	 * @return the resource discovery meta
	 * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#getResourceDiscoveryMeta()
	 */
	@Override
	public ResourceDiscoveryClientMeta getResourceDiscoveryMeta() {
		ResourceDiscoveryClientMeta meta = this.clientCommInterface.getResourceDiscoveryMeta();
		meta.setClientScope(this.ypagesClientScope);
		meta.setClientTransport("TRANSPORT_MQTT");
		meta.setClientEncoding(Configuration.RESOURCE_DISCOVERY_ENCODING_USE);
		return meta;
	}

	// ===========================================================================
	// FSM Logic (ILlClientFsmLogic)
	// ===========================================================================

	/**
	 * 
	 *
	 * @throws ClientNotReadyException      the client not ready exception
	 * @throws NoSuchCommInterfaceException the no such communication interface
	 *                                      exception
	 * @throws NoSuchPseudoHostException    the no such pseudo host exception
	 * @throws NoSuchMethodException        the no such method exception
	 * @throws IllegalAccessException       the illegal access exception
	 * @throws InstantiationException       the instantiation exception
	 * @throws InvocationTargetException    the invocation target exception
	 * @see at.ac.ait.lablink.core.client.ILlClientFsmLogic#onCreateSuccess()
	 */
	@Override
	public void onCreateSuccess()
			throws ClientNotReadyException, NoSuchCommInterfaceException, NoSuchPseudoHostException,
			NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
		this.clientCommInterface = LlClientCommInterfaceFactory.getHostImplementation(this);
		this.clientCommInterface.create();
		if (this.isPseudoClient()) {
			logger.info("Pseudo Client [{}] created for host [{}].", this.name, this.hostImplementation);
		} else {
			logger.info("Client [{}] created with requested interface [{}].", this.name, this.hostImplementation);
		}
	}

	/**
	 * 
	 *
	 * @throws ConfigurationException           the configuration exception
	 * @throws ClientNotReadyException          the client not ready exception
	 * @throws NoServicesInClientLogicException the no services in client logic
	 *                                          exception
	 * @throws DataTypeNotSupportedException    the data type not supported
	 *                                          exception
	 * @throws PseudoHostException              the pseudo host exception
	 * @see at.ac.ait.lablink.core.client.ILlClientFsmLogic#onInitSuccess()
	 */
	@Override
	public void onInitSuccess() throws ConfigurationException, ClientNotReadyException,
			NoServicesInClientLogicException, DataTypeNotSupportedException, PseudoHostException {
		this.clientCommInterface.init();
		logger.info("Client [{}] Initialized.", this.name);
	}

	/**
	 * 
	 *
	 * @throws ClientNotReadyException the client not ready exception
	 * @throws PseudoHostException     the pseudo host exception
	 * @throws IOException             Signals that an I/O exception has occurred.
	 * @see at.ac.ait.lablink.core.client.ILlClientFsmLogic#onStartSuccess()
	 */
	@Override
	public void onStartSuccess() throws ClientNotReadyException, PseudoHostException, IOException {
		this.clientCommInterface.start();
		logger.info("Client [{}] started. The runtime Id is [{}].", this.name, this.getRuntimeId());
		this.runRd();
		this.runPrometheusMeasures();

		if (this.showShell) {
			try {
				this.startShell();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 
	 *
	 * @throws ClientNotReadyException the client not ready exception
	 * @see at.ac.ait.lablink.core.client.ILlClientFsmLogic#onShutdownSuccess()
	 */
	@Override
	public void onShutdownSuccess() throws ClientNotReadyException {
		this.clientCommInterface.shutdown();
		logger.info("Client [{}] shutdown.", this.name);
	}

	/**
	 * 
	 *
	 * @return the implemented services
	 * @see at.ac.ait.lablink.core.client.ci.ILlClientCommInterface#getImplementedServices()
	 */
	@Override
	public Map<String, IImplementedService> getImplementedServices() {
		return this.clientCommInterface.getImplementedServices();
	}

	/**
	 * Gets the service description.
	 *
	 * @param service the service
	 * @return the service description
	 * @throws ServiceIsNotRegisteredWithClientException the service is not
	 *                                                   registered with client
	 *                                                   exception
	 */
	public String getServiceDescription(String service) throws ServiceIsNotRegisteredWithClientException {

		if (!this.isServiceExists(service)) {
			throw new ServiceIsNotRegisteredWithClientException();
		}

		return clientCommInterface.getImplementedServices().get(service).getName();
	}

	/**
	 * Sets the service value.
	 *
	 * @param service the service
	 * @param val     the val
	 * @return true, if successful
	 * @throws ServiceIsNotRegisteredWithClientException the service is not
	 *                                                   registered with client
	 *                                                   exception
	 */
	@SuppressWarnings("unchecked")
	public boolean setServiceValue(String service, double val) throws ServiceIsNotRegisteredWithClientException {

		if (!this.isServiceExists(service)) {
			throw new ServiceIsNotRegisteredWithClientException();
		}

		return clientCommInterface.getImplementedServices().get(service).setValue(Double.valueOf(val));
	}

	/**
	 * Sets the service value.
	 *
	 * @param service the service
	 * @param val     the val
	 * @return true, if successful
	 * @throws ServiceIsNotRegisteredWithClientException the service is not
	 *                                                   registered with client
	 *                                                   exception
	 */
	@SuppressWarnings("unchecked")
	public boolean setServiceValue(String service, long val) throws ServiceIsNotRegisteredWithClientException {

		if (!this.isServiceExists(service)) {
			throw new ServiceIsNotRegisteredWithClientException();
		}

		return clientCommInterface.getImplementedServices().get(service).setValue(Long.valueOf(val));
	}

	/**
	 * Sets the service value.
	 *
	 * @param service the service
	 * @param val     the val
	 * @return true, if successful
	 * @throws ServiceIsNotRegisteredWithClientException the service is not
	 *                                                   registered with client
	 *                                                   exception
	 */
	@SuppressWarnings("unchecked")
	public boolean setServiceValue(String service, boolean val) throws ServiceIsNotRegisteredWithClientException {

		if (!this.isServiceExists(service)) {
			throw new ServiceIsNotRegisteredWithClientException();
		}

		return clientCommInterface.getImplementedServices().get(service).setValue(Boolean.valueOf(val));
	}

	/**
	 * Sets the service value.
	 *
	 * @param servicename the servicename
	 * @param val         the val
	 * @return true, if successful
	 * @throws ServiceIsNotRegisteredWithClientException the service is not
	 *                                                   registered with client
	 *                                                   exception
	 */
	@SuppressWarnings("unchecked")
	public boolean setServiceValue(String servicename, String val) throws ServiceIsNotRegisteredWithClientException {

		if (!this.isServiceExists(servicename)) {
			throw new ServiceIsNotRegisteredWithClientException();
		}

		// return clientCommInterface.getImplementedServices().get(servicename)
		// .setValue(new String(val));
		return clientCommInterface.getImplementedServices().get(servicename).setValue(val);
	}

	/**
	 * Sets the service value.
	 *
	 * @param service the service
	 * @param val     the val
	 * @return true, if successful
	 * @throws ServiceIsNotRegisteredWithClientException the service is not
	 *                                                   registered with client
	 *                                                   exception
	 */
	public boolean setServiceValue(LlService service, double val) throws ServiceIsNotRegisteredWithClientException {
		return setServiceValue(service.getName(), val);
	}

	/**
	 * Sets the service value.
	 *
	 * @param service the service
	 * @param val     the val
	 * @return true, if successful
	 * @throws ServiceIsNotRegisteredWithClientException the service is not
	 *                                                   registered with client
	 *                                                   exception
	 */
	public boolean setServiceValue(LlServicePseudo service, double val)
			throws ServiceIsNotRegisteredWithClientException {
		return setServiceValue(service.getName(), val);
	}

	/**
	 * Sets the service value.
	 *
	 * @param service the service
	 * @param val     the val
	 * @return true, if successful
	 * @throws ServiceIsNotRegisteredWithClientException the service is not
	 *                                                   registered with client
	 *                                                   exception
	 */
	public boolean setServiceValue(LlService service, long val) throws ServiceIsNotRegisteredWithClientException {
		return setServiceValue(service.getName(), val);
	}

	/**
	 * Sets the service value.
	 *
	 * @param service the service
	 * @param val     the val
	 * @return true, if successful
	 * @throws ServiceIsNotRegisteredWithClientException the service is not
	 *                                                   registered with client
	 *                                                   exception
	 */
	public boolean setServiceValue(LlServicePseudo service, long val) throws ServiceIsNotRegisteredWithClientException {
		return setServiceValue(service.getName(), val);
	}

	/**
	 * Sets the service value.
	 *
	 * @param service the service
	 * @param val     the val
	 * @return true, if successful
	 * @throws ServiceIsNotRegisteredWithClientException the service is not
	 *                                                   registered with client
	 *                                                   exception
	 */
	public boolean setServiceValue(LlService service, String val) throws ServiceIsNotRegisteredWithClientException {
		return setServiceValue(service.getName(), val);
	}

	/**
	 * Sets the service value.
	 *
	 * @param service the service
	 * @param val     the val
	 * @return true, if successful
	 * @throws ServiceIsNotRegisteredWithClientException the service is not
	 *                                                   registered with client
	 *                                                   exception
	 */
	public boolean setServiceValue(LlServicePseudo service, String val)
			throws ServiceIsNotRegisteredWithClientException {
		return setServiceValue(service.getName(), val);
	}

	/**
	 * Sets the service value.
	 *
	 * @param service the service
	 * @param val     the val
	 * @return true, if successful
	 * @throws ServiceIsNotRegisteredWithClientException the service is not
	 *                                                   registered with client
	 *                                                   exception
	 */
	public boolean setServiceValue(LlService service, boolean val) throws ServiceIsNotRegisteredWithClientException {
		return setServiceValue(service.getName(), val);
	}

	/**
	 * Sets the service value.
	 *
	 * @param service the service
	 * @param val     the val
	 * @return true, if successful
	 * @throws ServiceIsNotRegisteredWithClientException the service is not
	 *                                                   registered with client
	 *                                                   exception
	 */
	public boolean setServiceValue(LlServicePseudo service, boolean val)
			throws ServiceIsNotRegisteredWithClientException {
		return setServiceValue(service.getName(), val);
	}

	/**
	 * Checks if is service exists.
	 *
	 * @param sname the sname
	 * @return true, if is service exists
	 */
	public boolean isServiceExists(String sname) {
		return this.isPseudoClient() ? (this.getPseudoServices().get(sname) != null)
				: (this.getServices().get(sname) != null);
	}

	/**
	 * Gets the service value double.
	 *
	 * @param service the service
	 * @return the service value double
	 * @throws InvalidCastForServiceValueException the invalid cast for service
	 *                                             value exception
	 */
	public Double getServiceValueDouble(String service) throws InvalidCastForServiceValueException {
		Double curval = null;

		try {
			curval = (Double) clientCommInterface.getImplementedServices().get(service).getValue();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw new InvalidCastForServiceValueException();
		}

		return curval;
	}

	/**
	 * Gets the service value double.
	 *
	 * @param service the service
	 * @return the service value double
	 * @throws InvalidCastForServiceValueException the invalid cast for service
	 *                                             value exception
	 */
	public Double getServiceValueDouble(LlService service) throws InvalidCastForServiceValueException {
		return getServiceValueDouble(service.getName());
	}

	/**
	 * Gets the service value long.
	 *
	 * @param service the service
	 * @return the service value long
	 * @throws InvalidCastForServiceValueException the invalid cast for service
	 *                                             value exception
	 */
	public Long getServiceValueLong(LlService service) throws InvalidCastForServiceValueException {
		return getServiceValueLong(service.getName());
	}

	/**
	 * Gets the service value long.
	 *
	 * @param service the service
	 * @return the service value long
	 * @throws InvalidCastForServiceValueException the invalid cast for service
	 *                                             value exception
	 */
	public Long getServiceValueLong(String service) throws InvalidCastForServiceValueException {
		Long curval = null;

		try {
			curval = (Long) clientCommInterface.getImplementedServices().get(service).getValue();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw new InvalidCastForServiceValueException();
		}

		return curval;

	}

	/**
	 * Gets the service value boolean.
	 *
	 * @param service the service
	 * @return the service value boolean
	 * @throws InvalidCastForServiceValueException the invalid cast for service
	 *                                             value exception
	 */
	public Boolean getServiceValueBoolean(LlService service) throws InvalidCastForServiceValueException {

		return getServiceValueBoolean(service.getName());
	}

	/**
	 * Gets the service value boolean.
	 *
	 * @param service the service
	 * @return the service value boolean
	 * @throws InvalidCastForServiceValueException the invalid cast for service
	 *                                             value exception
	 */
	public Boolean getServiceValueBoolean(String service) throws InvalidCastForServiceValueException {
		Boolean curval = null;

		try {
			curval = (Boolean) clientCommInterface.getImplementedServices().get(service).getValue();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw new InvalidCastForServiceValueException();
		}

		return curval;

	}

	/**
	 * Gets the service value string.
	 *
	 * @param service the service
	 * @return the service value string
	 * @throws InvalidCastForServiceValueException the invalid cast for service
	 *                                             value exception
	 */
	public String getServiceValueString(LlService service) throws InvalidCastForServiceValueException {
		return getServiceValueString(service.getName());
	}

	/**
	 * Gets the service value string.
	 *
	 * @param service the service
	 * @return the service value string
	 */
	public String getServiceValueString(String service) {
		return clientCommInterface.getImplementedServices().get(service).getValue().toString();
	}

	/**
	 * Gets the service string value.
	 *
	 * @param service the service
	 * @return the service string value
	 */
	public Optional<String> getServiceStringValue(String service) {
		return Optional.ofNullable(clientCommInterface.getImplementedServices().get(service).getValue().toString());
	}

	/**
	 * Gets the service value.
	 *
	 * @param service the service
	 * @return the service value
	 * @throws InvalidCastForServiceValueException the invalid cast for service
	 *                                             value exception
	 */
	public ByteBuffer getServiceValue(LlService service) throws InvalidCastForServiceValueException {

		ByteBuffer bb = null;

		ELlServiceDataTypes stype = service.getServiceDataType();
		String sname = service.getName();

		if (stype == null) {
			throw new InvalidCastForServiceValueException();
		}

		switch (stype) {
		case SERVICE_DATATYPE_DOUBLE:
			bb = ByteBuffer.allocate(Double.SIZE);
			bb.putDouble(getServiceValueDouble(sname));
			bb.rewind();
			break;
		case SERVICE_DATATYPE_LONG:
			bb = ByteBuffer.allocate(Long.SIZE);
			bb.putLong(getServiceValueLong(sname));
			bb.rewind();
			break;
		case SERVICE_DATATYPE_STRING:
			String sval = getServiceValueString(sname);
			ByteBuffer.allocate(sval.length());
			bb.put(sval.getBytes());
			bb.rewind();
			break;
		case SERVICE_DATATYPE_BOOLEAN:
			short bval = (short) (getServiceValueBoolean(sname).booleanValue() ? 1 : 0);
			bb.putShort(bval);
			bb.rewind();
			break;
		default:
			logger.error("Data type not supported [{}].", stype);
			throw new InvalidCastForServiceValueException();
		}

		return bb.asReadOnlyBuffer();
	}

	/**
	 * Gets the service value as a String. The function will determine the service
	 * data type automatically.
	 *
	 * @param service the service
	 * @return the service value auto
	 */
	public String getServiceValueAuto(LlService service) {

		return service.getCurState().toString();
	}

	/**
	 * Gets the service value auto.
	 *
	 * @param service the service
	 * @return the service value auto
	 * @throws ServiceIsNotRegisteredWithClientException the service is not
	 *                                                   registered with client
	 *                                                   exception
	 */
	public String getServiceValueAuto(String service) throws ServiceIsNotRegisteredWithClientException {

		if (!this.isServiceExists(service)) {
			throw new ServiceIsNotRegisteredWithClientException();
		}

		return this.getServices().get(service).getCurState().toString();

	}

	/**
	 * Gets the runtime id.
	 *
	 * @return the runtime id
	 */
	public long getRuntimeId() {
		return this.id;
	}

	/**
	 * Checks if this is a pseudo client.
	 *
	 * @return the pseudoClient
	 */
	@Override
	public boolean isPseudoClient() {
		return pseudoClient;
	}

	/**
	 * Gets the pseudo services.
	 *
	 * @return the pseudo services
	 * @see at.ac.ait.lablink.core.client.ILlClientLogic#getPseudoServices()
	 */
	@Override
	public Map<String, LlServicePseudo> getPseudoServices() {
		return this.pseudoServices;
	}

	/**
	 * Sets the client logic.
	 *
	 * @param clogic the new client logic
	 */
	@Override
	public void setClientLogic(ILlClientLogic clogic) {
		return;
	}

	/**
	 * Gets the host implementation SP.
	 *
	 * @return the host implementation SP
	 */
	@Override
	public String getHostImplementationSp() {
		return this.hostImplementation;
	}

	/**
	 * Adds the shutdown hook.
	 *
	 * @param hook the hook
	 */
	public void addShutdownHook(Thread hook) {
		Runtime.getRuntime().addShutdownHook(hook);
	}

	/**
	 * Sets the flag to run promethus measures.
	 */
	private void setRunPromethusMeasures() {
		this.services.forEach((k, v) -> {
			if (v.isExposedToPrometheus()) {
				this.runPrometheusServer = true;
			}
		});
	}

	/**
	 * Start prometheus measures.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void startPrometheusMeasures() throws IOException {
		this.prometheusMeasuresServer = new HTTPServer.Builder().withPort(this.prometheusMeasuresPort).build();
		logger.info("Prometheus measures are now availabe for scraping.");
	}

	/**
	 * Run prometheus measures.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void runPrometheusMeasures() throws IOException {
		this.setRunPromethusMeasures();
		if (this.runPrometheusServer) {
			DefaultExports.initialize();
			logger.info("Attempting to start Prometheus measures on port {}", this.prometheusMeasuresPort);
			this.startPrometheusMeasures();
		} else {
			logger.info(
					"No service is configured to be exposed for Prometheus. Server will not run and, therefore, no measures will be exposed.");
		}
	}
}
