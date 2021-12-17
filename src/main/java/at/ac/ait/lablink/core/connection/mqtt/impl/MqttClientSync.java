//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.mqtt.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.ait.lablink.core.connection.IConnectionHandler;
import at.ac.ait.lablink.core.connection.ex.LowLevelCommRuntimeException;
import at.ac.ait.lablink.core.connection.mqtt.IMqttConnectionListener;
import at.ac.ait.lablink.core.connection.mqtt.IMqttPublisher;
import at.ac.ait.lablink.core.connection.mqtt.IMqttReceiverCallback;
import at.ac.ait.lablink.core.connection.mqtt.IMqttSubscriber;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

/**
 * Implementation of the low level MQTT client.
 *
 * <p>
 * The low level MQTT client works as a wrapper interface to the MQTT client
 * library. It extends the library with additional functionality like an
 * automatic re-establishment of a lost connection to the broker or the
 * possibility to register different listeners, which are informed by the client
 * if the connection state changes.
 *
 * <p>
 * This MQTT client uses a synchronous communication with the MQTT core (sending
 * methods). Therefore, every method call of the core library will be block
 * until it's finished. This client can be used for simple use cases, where the
 * reaction time of a method call isn't very important.
 *
 * <h2>Functionality of the low Level client</h2>
 *
 * <p>
 * <b>LowLevel client properties</b><br>
 * The lowLevel client should have the ability to reconnect to the broker, if it
 * lost its connection. Therefore a reconnection time period is necessary, where
 * it tries to reconnect to a broker. The client tries to reconnect to the
 * broker for certain times or for infinite. The reconnection functionality
 * should be optionally be disabled by the settings.
 *
 * <p>
 * The client needs some address parameters for the connection to the broker.
 * These parameters should be used as properties in a configuration file.
 *
 * <p>
 * The first implementation won't be able to change the properties during
 * runtime. For future improvements the change of the properties should be
 * handled dynamically during runtime. Therefore parameters concerning the
 * connection settings (e.g., broker address), should only be handled during a
 * disconnected period.
 *
 *
 * <p>
 * <b>Initialization (Object creation)</b><br>
 * After initialization the client isn't connected to the MQTT broker. The
 * system isn't allowed to automatically reconnect to the broker. In this state
 * it isn't possible to subscribe or publish a topic. It should be possible to
 * add the MqttConnectionListeners or the MqttReceiver callback.
 *
 *
 * <p>
 * <b>Registering a callback method</b><br>
 * Some callback methods (IMqttReceiverCallback or IMqttConnectionListener)
 * could be registered to the lowLevel client. The registrations could be
 * dynamically added or removed from the client. The client will inform the
 * callback methods during its operation. If no callback is registered to the
 * client, the client will work in a correct way and it will drop all received
 * messages.
 *
 *
 * <p>
 * <b>Connecting to the MQTT broker</b><br>
 * After the initialization of the client it is possible to connect the client
 * to a broker. Therefore the connect method is called. After a successful
 * connection establishment the client will inform all MqttConnectionListeners
 * by calling the <code>onEstablishedMqttConnection()</code> method. If the
 * connection to the broker can't be established the client will inform the
 * caller with an <code>LowLevelCommRuntimeException</code> and the reconnection
 * will be activated.
 *
 *
 * <p>
 * <b>Operation Mode (connection established)</b><br>
 * During the established connection it is possible to subscribe and publish
 * messages or receive messages from the broker.
 *
 *
 * <p>
 * <b>Receiving a MQTT message</b><br>
 * If a message is received by the MQTT client it will redirect the incoming
 * message to the registered <code>MqttCallbackReceiver</code>
 *
 *
 * <p>
 * <b>Disconnecting the MQTT client</b><br>
 * By calling the disconnect method the client will perform the disconnection
 * procedure. Therefore it will call the
 * <code>onDisconnectingMqttConnection</code> of all registered
 * MqttConnectionListeners and then it will disconnect from the Mqtt broker. If
 * the disconnection fails a <code>LowLevelCommRuntimeException</code> will be
 * thrown.
 *
 * <p>
 * The manual called disconnect method disables an automatic reconnection.
 *
 *
 * <p>
 * <b>Lost the connection to the MQTT broker</b><br>
 * If the client lost the connection to the broker (e.g., network errors, broker
 * crashes) the MQTT lib will call the lost connection handler. This call will
 * be redirected to all registered MqttConnectionListeners by calling the method
 * <code>onLostMqttConnection()</code>. The client will trigger the automatic
 * reconnection sequence (if enabled) and tries to reconnect to the MQTT broker.
 *
 * <p>
 * During the disconnected state the client throws an exception if the publish
 * or subscribe methods are called.
 */
@SuppressWarnings("FieldCanBeLocal")
public class MqttClientSync implements MqttCallback, IMqttPublisher, IConnectionHandler, IMqttSubscriber {

	private static final Logger logger = LoggerFactory.getLogger(MqttClientSync.class);

	// Preset properties of the class

	/* Quality of service for published messages */
	@SuppressWarnings("FieldCanBeLocal")
	private final int qualityOfService = 0;

	/* Default settings of the class */

	private final String defaultBrokerAddress = "localhost";
	private final int defaultBrokerPort = 1883;
	private final String defaultConnectionProtocol = "tcp";
	private final boolean defaultEnableReconnection = true;
	private final int defaultReconnectInterval = 10;
	private final int defaultReconnectNumberOfTries = -1;
	private final int defaultReceivedMessagesQueueSize = 2048;

	private int mqttConnectionTimeout = MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT;

	/* Client ID for MQTT communication (Not the clientId of the LablinkClient) */
	private final String clientId;

	/*
	 * current address string of the broker. The broker address uses the
	 * representation of the MQTT library ({@link MqttClient}), e.g.,
	 * "tcp://localhost:1883".
	 */
	private final String brokerAddress;

	/* Mqtt synchronous client for publishing and receiving MQTT messages */
	private MqttClient mqttClient = null;

	/* Registered component to handle received messages */
	private IMqttReceiverCallback receiveCallback;

	private final Object receiveCallbackSyncMonitor = new Object();

	/*
	 * Registered connection listeners that should be informed about a state change.
	 */
	private final List<IMqttConnectionListener> connectionListeners = new ArrayList<IMqttConnectionListener>();

	private final Object connectionListenersSyncMonitor = new Object();

	/* Current state of the client for reconnection handling */
	private ELlClientState currentClientState = ELlClientState.DISCONNECTED_FROM_BROKER;

	/* Own timer thread which handles the reconnection functionality */
	private final ReconnectionThread reconnectionThread;

	/* Worker thread for handling received messages */
	private final ReceivedMessageConsumer receivedMessageConsumerThread;

	private final Object publishMonitor = new Object();

	/**
	 * Constructor with optional configuration object
	 *
	 * <p>
	 * The MqttClientSync can be configured with a <code>Configuration</code>
	 * object. This object can be memory based or it can be loaded from a
	 * resources/properties file. The configuration will only be updated or taken
	 * during the creation of the client.<br>
	 * The following list shows the current implemented configuration properties
	 * withs their default values (between brackets):
	 * <ul>
	 * <li><b>lowLevelComm.enableReconnection</b> (true, boolean): Switch for
	 * enabling the automatic reconnection, if the connection to the MQTT broker is
	 * lost.</li>
	 * <li><b>lowLevelComm.reconnectInterval</b> (10, int): Time interval between
	 * two reconnection tries in Seconds.</li>
	 * <li><b>lowLevelComm.reconnectNumberOfTries</b> (-1, int): Maximum number of
	 * reconnection tries. After this number of tries the client will switch to the
	 * disconnecting state. With -1 the reconnection will be try forever
	 * (infinite)</li>
	 * <li><b>lowLevelComm.brokerAddress</b> ("localhost", string): Address of the
	 * MQTT broker to be connected.</li>
	 * <li><b>lowLevelComm.brokerPort</b> (1883, int): Port of the MQTT broker to be
	 * connected.</li>
	 * <li><b>lowLevelComm.connectionProtocol</b> ("tcp", string): Communication
	 * Protocol for the MQTT broker. Usually tcp or ssl</li>
	 * <li><b>lowLevelComm.mqttConnectionTimeout</b> (30, int): Mqtt Connection
	 * Timeout in seconds</li>
	 * <li><b>lowLevelComm.receivedMessagesQueueSize</b> (100, int): Queue Size for
	 * incoming (received) messages. Incoming messages will be buffered in a queue
	 * and decoupled from the incoming Mqtt thread.</li>
	 * </ul>
	 * TODO add config parameters for SSL connection in the future
	 *
	 * @param mqttClientId MQTT client identifier (not the LablinkClient identifier)
	 *                     For identification of the client within the broker.
	 * @param config       Configuration object that is used to parametrize the MQTT
	 *                     client. Different parameters can be set. If no parameter
	 *                     is set, the client will use the default settings.
	 */
	public MqttClientSync(String mqttClientId, Configuration config) {

		if (config == null) {
			logger.info("No configuration is set for low-level MQTT client. Use default configuration.");
			config = new BaseConfiguration(); /* Initialize empty configuration */
		}

		logger.info("Initialize low-level MQTT client '{}'.", mqttClientId);
		this.clientId = mqttClientId;

		// Read configuration for MQTT broker address
		String brokerAddress = config.getString("lowLevelComm.brokerAddress", defaultBrokerAddress);
		int brokerPort = config.getInt("lowLevelComm.brokerPort", defaultBrokerPort);
		String connectionProtocol = config.getString("lowLevelComm.connectionProtocol", defaultConnectionProtocol);

		this.brokerAddress = createMqttBrokerAddress(brokerAddress, brokerPort, connectionProtocol);
		logger.info("BrokerAddress: {}", this.brokerAddress);

		this.mqttConnectionTimeout = config.getInt("lowLevelComm.mqttConnectionTimeout",
				MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT);
		logger.info("Connection Timeout: {}", this.mqttConnectionTimeout + "s");

		// Read configuration for Reconnection handling
		reconnectionThread = new ReconnectionThread(this);

		this.reconnectionThread
				.setEnableReconnection(config.getBoolean("lowLevelComm.enableReconnection", defaultEnableReconnection));
		this.reconnectionThread.setReconnectionInterval(
				config.getInt("lowLevelComm.reconnectInterval", defaultReconnectInterval) * 1000);
		this.reconnectionThread.setReconnectionTries(
				config.getInt("lowLevelComm.reconnectNumberOfTries", defaultReconnectNumberOfTries));

		logger.info("Reconnection Settings: Enabled: {} Interval: {}ms NoOfTries: {}",
				reconnectionThread.isEnableReconnection(), reconnectionThread.getReconnectionInterval(),
				reconnectionThread.getReconnectNumberOfTries());

		reconnectionThread.start();

		// Activate worker for receiving messages
		int queueSize = config.getInt("lowLevelComm.receivedMessagesQueueSize", defaultReceivedMessagesQueueSize);
		receivedMessageConsumerThread = new ReceivedMessageConsumer(queueSize, this.clientId);
		logger.info("ReceivedMessageConsumer: Queue Size: {}", queueSize);
		receivedMessageConsumerThread.start();
	}

	@Override
	public String toString() {
		return "MqttClientSync(" + clientId + ", " + brokerAddress + ')';
	}

	/**
	 * Create the Address String for the MQTT broker.
	 *
	 * <p>
	 * The method generated the address string for the MQTT broker without a
	 * validation of the input parameters.
	 *
	 * @param brokerAddress      Address of the broker (e.g., "localhost")
	 * @param brokerPort         Port of the broker (e.g., 1883)
	 * @param connectionProtocol protocol of the connection (e.g., "tcp" or "ssh")
	 * @return generated address string for the MQTT broker
	 */
	private String createMqttBrokerAddress(String brokerAddress, int brokerPort, String connectionProtocol) {
		return String.format("%s://%s:%d", connectionProtocol, brokerAddress, brokerPort);
	}

	/**
	 * Factory method for creating the Mqtt client. Can be mocked for unit tests.
	 *
	 * @param brokerAddress Address of the broker
	 * @param clientId      ID of the client
	 * @return the created MqttClient
	 * @throws MqttException will be thrown by the Mqtt client creation
	 */
	private static MqttClient createMqttClient(String brokerAddress, String clientId) throws MqttException {
		return new MqttClient(brokerAddress, clientId, null);
	}

	/**
	 * Get the actual lowLevelMqttReceiver which contains the callback handler for
	 * received messages.
	 *
	 * @return actual used lowLevelMqttReceiver
	 */
	public IMqttReceiverCallback getReceiveCallback() {
		return receiveCallback;
	}

	/**
	 * Set the IMqttReceiverCallback which contains the callback handler for
	 * received messages.
	 *
	 * @param receiveCallback IMqttReceiverCallback to be set
	 */
	public void setReceiveCallback(IMqttReceiverCallback receiveCallback) {
		if (receiveCallback == null) {
			throw new LlCoreRuntimeException("Set ReceiveCallback failed: Parameter is a null.");
		}

		logger.debug("Set new ReceiveCallback: {}", receiveCallback);
		synchronized (this.receiveCallbackSyncMonitor) {
			this.receiveCallback = receiveCallback;
			this.receivedMessageConsumerThread.setReceiveCallback(this.receiveCallback);
		}
	}

	/**
	 * Add a IMqttConnectionListener to the client. This connection listener will be
	 * informed by the the client, if an event regarding the connection will occur.
	 *
	 * @param listener IMqttConnectionListener to be added
	 */
	public void addMqttConnectionListener(IMqttConnectionListener listener) {

		if (listener == null) {
			throw new LlCoreRuntimeException("Add ConnectionListener failed: Parameter is a null.");
		}

		if (!this.connectionListeners.contains(listener)) {
			logger.debug("Add connection listener: {}", listener.toString());

			synchronized (this.connectionListenersSyncMonitor) {
				this.connectionListeners.add(listener);
			}
		}
	}

	/**
	 * Remove a connectionListener from the client.
	 *
	 * @param listener IMqttConnectionListener to be removed
	 */
	public void removeConnectionListener(IMqttConnectionListener listener) {
		logger.trace("Remove connection listener: {}", listener.toString());
		synchronized (this.connectionListenersSyncMonitor) {
			this.connectionListeners.remove(listener);
		}
	}

	/**
	 * Read the registered connection listeners (for testing purposes)
	 *
	 * @return the connection listeners.
	 */
	List<IMqttConnectionListener> getConnectionListeners() {
		return connectionListeners;
	}

	/**
	 * Read the clientId of the broker.
	 *
	 * @return the clientId of the broker.
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * Read the broker address that the client uses. The broker address uses the
	 * representation of the MQTT library ({@link MqttClient}), e.g.,
	 * "tcp://localhost:1883".
	 *
	 * @return broker address of the client.
	 */
	public String getBrokerAddress() {
		return brokerAddress;
	}

	/**
	 * Set the actual state of the client (thread safe) and trigger the reconnection
	 * timer.
	 *
	 * @param currentClientState set the current state
	 */
	private synchronized void setCurrentClientStateAndTriggerReconnect(ELlClientState currentClientState) {

		this.currentClientState = currentClientState;

		synchronized (this.reconnectionThread) {
			this.reconnectionThread.notify();
		}
	}

	ELlClientState getCurrentClientState() {
		return currentClientState;
	}

	/**
	 * Shutdown the MQTT lowLevel client.
	 *
	 * <p>
	 * This method will be used for cleanup purposes. It should be called before the
	 * program's end. It will disconnect from the broker and clean up its states.
	 */
	public void shutdown() {
		if (isConnected()) {
			try {
				disconnect();
			} catch (LowLevelCommRuntimeException ex) {
				logger.warn("Error while disconnecting from broker during shutdown.");
			}
		}

		this.reconnectionThread.shutdown();
		this.receivedMessageConsumerThread.shutdown();
	}

	@Override
	public void connect() {

		// Create MQTT client if it doesn't exist.
		if (this.mqttClient == null) {
			try {
				this.mqttClient = MqttClientSync.createMqttClient(this.brokerAddress, this.clientId);
			} catch (MqttException ex) {
				throw new LowLevelCommRuntimeException(ex);
			}
		}

		// Ignore connect method if the client is already connected
		if (isConnected()) {
			return;
		}

		// Connect to broker
		MqttConnectOptions mqttOpt = new MqttConnectOptions();
		mqttOpt.setCleanSession(true);
		mqttOpt.setConnectionTimeout(mqttConnectionTimeout);
		this.mqttClient.setCallback(this);

		try {
			this.mqttClient.connect(mqttOpt);
			this.setCurrentClientStateAndTriggerReconnect(ELlClientState.CONNECTED_TO_BROKER);

			synchronized (this.connectionListenersSyncMonitor) {
				for (IMqttConnectionListener listener : this.connectionListeners) {
					listener.onEstablishedMqttConnection();
				}
			}
		} catch (MqttException ex) {
			this.setCurrentClientStateAndTriggerReconnect(ELlClientState.TRY_TO_RECONNECT);
			throw new LowLevelCommRuntimeException(ex);
		}
		logger.info("MqttClient connected to broker {}", mqttClient.getServerURI());

	}

	@Override
	public void disconnect() {
		this.setCurrentClientStateAndTriggerReconnect(ELlClientState.DISCONNECTED_FROM_BROKER);
		if (isConnected()) {

			synchronized (this.connectionListenersSyncMonitor) {
				for (IMqttConnectionListener listener : this.connectionListeners) {
					listener.onDisconnectingMqttConnection();
				}
			}

			try {
				this.mqttClient.disconnect();
				logger.info("MqttClient disconnected from broker {}", mqttClient.getServerURI());
			} catch (MqttException ex) {
				throw new LowLevelCommRuntimeException(ex);
			}
		}
	}

	@Override
	public boolean isConnected() {
		return this.mqttClient != null && this.mqttClient.isConnected();
	}

	@Override
	public void publish(String mqttTopic, byte[] mqttPayload) {
		if (!this.isConnected()) {
			throw new LowLevelCommRuntimeException("MqttClientSync isn't connected to a broker");
		}

		MqttUtils.validateMqttTopic(mqttTopic);

		MqttMessage mqttMsg = new MqttMessage(mqttPayload);
		mqttMsg.setQos(this.qualityOfService);

		synchronized (this.publishMonitor) {
			try {
				this.mqttClient.publish(mqttTopic, mqttMsg);
			} catch (MqttException ex) {
				throw new LowLevelCommRuntimeException(ex);
			}
		}
	}

	@Override
	public void subscribe(String mqttTopic) {
		if (!this.isConnected()) {
			throw new LowLevelCommRuntimeException("MqttClientSync isn't connected to a broker");
		}

		try {
			this.mqttClient.subscribe(mqttTopic);
		} catch (MqttException ex) {
			throw new LowLevelCommRuntimeException(ex);
		}

	}

	@Override
	public void subscribe(List<String> mqttTopics) {
		if (!this.isConnected()) {
			throw new LowLevelCommRuntimeException("MqttClientSync isn't connected to a broker");
		}

		for (String mqttTopic : mqttTopics) {
			MqttUtils.validateMqttSubscription(mqttTopic);
		}

		try {
			this.mqttClient.subscribe(mqttTopics.toArray(new String[0]));
		} catch (MqttException ex) {
			throw new LowLevelCommRuntimeException(ex);
		}
	}

	@Override
	public void unsubscribe(String mqttTopic) {

		if (!this.isConnected()) {
			throw new LowLevelCommRuntimeException("MqttClientSync isn't connected to a broker");
		}
		try {
			this.mqttClient.unsubscribe(mqttTopic);
		} catch (MqttException ex) {
			throw new LowLevelCommRuntimeException(ex);
		}

	}

	@Override
	public void unsubscribe(List<String> mqttTopics) {
		if (!this.isConnected()) {
			throw new LowLevelCommRuntimeException("MqttClientSync isn't connected to a broker");
		}

		for (String mqttTopic : mqttTopics) {
			MqttUtils.validateMqttSubscription(mqttTopic);
		}

		try {
			this.mqttClient.unsubscribe(mqttTopics.toArray(new String[0]));
		} catch (MqttException ex) {
			throw new LowLevelCommRuntimeException(ex);
		}
	}

	@Override
	public void connectionLost(Throwable throwable) {

		logger.warn("MQTT connection lost: {}", throwable.toString());

		synchronized (this.connectionListenersSyncMonitor) {
			for (IMqttConnectionListener listener : this.connectionListeners) {
				listener.onLostMqttConnection();
			}
		}

		this.setCurrentClientStateAndTriggerReconnect(ELlClientState.TRY_TO_RECONNECT);
	}

	@Override
	public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
		logger.trace("New MQTT message received: Topic({}) ReceivedMessage()", topic, mqttMessage.toString());

		synchronized (this.receiveCallbackSyncMonitor) {
			if (this.receiveCallback != null && !mqttMessage.isDuplicate()) {
				this.receivedMessageConsumerThread.addNewMessage(topic, mqttMessage.getPayload());
			}
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken mqttDeliveryToken) {
		/* not used by the synchronous client */
	}

	/**
	 * Inner enumeration to identify the current state of the client.
	 *
	 */
	enum ELlClientState {
		DISCONNECTED_FROM_BROKER, CONNECTED_TO_BROKER, TRY_TO_RECONNECT
	}

	/**
	 * Thread for controlling the reconnection to the broker.
	 *
	 * <p>
	 * The client provides the functionality to automatically reconnect to a broker,
	 * if the connection can't be created or the connection was lost.
	 *
	 * <p>
	 * The reconnection thread will periodically call the connect method of the
	 * client and tries to reconnect from it. The state variable of the client
	 * controls the reconnection. If it is set by the client the reconnection thread
	 * will be notified and it will trigger the reconnection if it is necessary.
	 */
	private class ReconnectionThread extends Thread {

		private final MqttClientSync parent;
		private boolean keepRunning = true;

		/* Config values for reconnection */
		private boolean enableReconnection;
		private int reconnectionInterval; // in milliseconds
		private int reconnectionTries; // -1 or positive number

		/* actual reconnection try counter */
		private int actualTry = 1;

		/**
		 * Default constructor.
		 *
		 * @param parent MqttClientSync where the reconnection should be handled
		 */
		ReconnectionThread(MqttClientSync parent) {
			this.parent = parent;
			this.setDaemon(true);
			this.setName("ReconnectionThread: " + parent.getClientId());

			enableReconnection = parent.defaultEnableReconnection;
			reconnectionInterval = parent.defaultReconnectInterval;
			reconnectionTries = parent.defaultReconnectNumberOfTries;
		}

		/**
		 * Shutdown and cleanup procedure.
		 *
		 */
		synchronized void shutdown() {
			this.keepRunning = false;
			this.interrupt();
		}

		@Override
		public void run() {

			logger.trace("Reconnection timer thread started (activated: {}, Interval: {}, Tries: {})",
					enableReconnection, reconnectionInterval, reconnectionTries);

			while (this.keepRunning) {

				ELlClientState beginClientState = parent.getCurrentClientState();
				logger.trace("ReconnectionTimerThread activated: {}", currentClientState);

				ELlClientState currentClientState = beginClientState;

				if (!this.enableReconnection) {
					if (currentClientState == ELlClientState.TRY_TO_RECONNECT) {
						parent.setCurrentClientStateAndTriggerReconnect(ELlClientState.DISCONNECTED_FROM_BROKER);
						currentClientState = parent.getCurrentClientState();
					}
				}

				if (currentClientState == ELlClientState.CONNECTED_TO_BROKER
						|| currentClientState == ELlClientState.DISCONNECTED_FROM_BROKER) {
					actualTry = 1;
				}

				if (this.enableReconnection && currentClientState == ELlClientState.TRY_TO_RECONNECT) {
					handleReconnection();
				}

				try {
					synchronized (this) {
						if (beginClientState != parent.getCurrentClientState()) {
							continue;
						}
						if (currentClientState == ELlClientState.TRY_TO_RECONNECT) {
							this.wait(reconnectionInterval);
						} else {
							this.wait();
						}
					}
				} catch (InterruptedException ign) {
					// This is expected
				}

			}
		}

		private void handleReconnection() {

			logger.trace("Reconnection try: {}", actualTry);
			try {
				parent.connect();
			} catch (LowLevelCommRuntimeException ex) {
				logger.debug("Reconnection try ({}) was not successful.", actualTry);
			}

			if (this.reconnectionTries != -1 && actualTry >= this.reconnectionTries) {
				logger.warn("Maximum number of reconnection tries exceeds. Stop to reconnect");
				parent.setCurrentClientStateAndTriggerReconnect(ELlClientState.DISCONNECTED_FROM_BROKER);
				actualTry = 1;
			} else {
				actualTry++;
			}
		}

		void setEnableReconnection(boolean enableReconnection) {
			this.enableReconnection = enableReconnection;
		}

		void setReconnectionInterval(int reconnectionInterval) {

			if (reconnectionInterval > 0) {
				this.reconnectionInterval = reconnectionInterval;
			} else {
				throw new IllegalArgumentException(String.format("False reconnection interval in milliseconds (%d) was "
						+ "set. The parameter should be greater than 0.", reconnectionInterval));
			}
		}

		void setReconnectionTries(int reconnectionTries) {

			if (reconnectionTries == -1 || reconnectionTries > 0) {
				this.reconnectionTries = reconnectionTries;
			} else {
				throw new IllegalArgumentException(
						String.format("False reconnection tries (%d) want to be set. The parameter should "
								+ "be -1 for infinite tries or greater than 0", reconnectionTries));
			}
		}

		int getReconnectNumberOfTries() {
			return this.reconnectionTries;
		}

		int getReconnectionInterval() {
			return reconnectionInterval;
		}

		boolean isEnableReconnection() {
			return enableReconnection;
		}
	}

	/**
	 * Worker thread to handle received messages.
	 *
	 * <p>
	 * This inner class is used to implement a blocking queue for the received
	 * messages. This is necessary to decouple the Mqtt receiving callback from the
	 * further message handling. Especially if the message callback publishes new
	 * messages this decoupling is necessary.
	 */
	private class ReceivedMessageConsumer extends Thread {

		/**
		 * Data Bean for received messages.
		 *
		 */
		class ReceivedMessage {
			public String topic;
			public byte[] payload;
		}

		private boolean isRunning = true;

		private final BlockingQueue<ReceivedMessage> receivedMsgQueue;

		private IMqttReceiverCallback receiverCallback;

		private final Object syncMonitor = new Object();

		/**
		 * Construct the receiving messages' handler.
		 *
		 * @param queueSize Size of the Queue for receiving messages
		 * @param clientId  client ID
		 */
		ReceivedMessageConsumer(int queueSize, String clientId) {
			this.setDaemon(true);
			this.setName("ReceivedMessageConsumer: " + clientId);
			receivedMsgQueue = new ArrayBlockingQueue<ReceivedMessage>(queueSize);
		}

		/**
		 * Set the callback handler for handling receiving messages.
		 *
		 * @param receiveCallback handler to be set
		 */
		void setReceiveCallback(IMqttReceiverCallback receiveCallback) {
			synchronized (this.syncMonitor) {
				this.receiverCallback = receiveCallback;
			}
		}

		/**
		 * Add a new received message to the worker queue.
		 *
		 * @param topic   of the received message
		 * @param payload of the received message
		 */
		void addNewMessage(String topic, byte[] payload) {

			ReceivedMessage msg = new ReceivedMessage();
			msg.topic = topic;
			msg.payload = payload;

			try {
				receivedMsgQueue.put(msg);
			} catch (InterruptedException ex) {
				// expected
			}
		}

		/**
		 * Shutdown the ReceivedMessageConsumer Thread.
		 *
		 */
		void shutdown() {
			isRunning = false;
			this.interrupt();
		}

		@Override
		public void run() {

			while (isRunning) {
				try {
					ReceivedMessage msg = receivedMsgQueue.take();
					logger.trace("Process received message (Topic: {} IPayload: {}) No of waiting objects: {}",
							msg.topic, new String(msg.payload), receivedMsgQueue.size());

					synchronized (this.syncMonitor) {
						if (this.receiverCallback == null) {
							logger.warn("No ReceiverCallback is set in ReceivedMessageConsumerThread.");
							continue;
						}
						this.receiverCallback.handleRawMqttMessage(msg.topic, msg.payload);
					}
				} catch (InterruptedException ignore) {
					// ignore
				}
			}
		}
	}
}
