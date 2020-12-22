//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.mqtt.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import at.ac.ait.lablink.core.connection.ex.LowLevelCommRuntimeException;
import at.ac.ait.lablink.core.connection.impl.PortUtils;
import at.ac.ait.lablink.core.connection.mqtt.IMqttConnectionListener;
import at.ac.ait.lablink.core.connection.mqtt.IMqttReceiverCallback;

import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.interception.messages.InterceptSubscribeMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;
import io.moquette.parser.proto.messages.AbstractMessage;
import io.moquette.parser.proto.messages.PublishMessage;
import io.moquette.server.Server;
import io.moquette.server.config.MemoryConfig;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.BindException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Integration tests for class MqttClientSync.
 *
 * <p>It will use an embedded Mqtt broker (Moquette) for testing the connection to a broker.
 */
public class MqttClientSyncIT {

  private Configuration testConfiguration;
  private MqttClientSync mqttClient;

  private MqttReceiverCallbackTestImpl mqttReceiver;
  private IMqttConnectionListener mqttConnectionListener;

  private static final Logger logger = LoggerFactory.getLogger(MqttClientSyncIT.class);

  // Broker instances for testing
  private static Server mqttBroker;
  private static BrokerInterceptor mqttBrokerInterceptor;
  private Properties mqttBrokerConf;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

    int[] usedPorts = {1883, 8936};

    for (int portNr : usedPorts) {
      if (!PortUtils.portAvailable(portNr)) {
        fail("Port " + portNr
            + " for embedded MQTT broker can't be opened. Maybe an MQTT broker is already running"
            + ".");
      }
    }
  }

  @Before
  public void setUp() throws Exception {
    logger.debug("Setup");

    // Configure and start MQTT broker for testing (Moquette)
    mqttBrokerConf = new Properties();
    mqttBrokerConf.setProperty("port", "1883");
    mqttBrokerConf.setProperty("host", "localhost");
    mqttBrokerConf.setProperty("websocket_port", "8936");
    mqttBrokerConf.setProperty("allow_anonymous", "true");
    mqttBrokerConf.setProperty("persistent_store",
        System.getProperty("user.dir") + "/target/moquette_store.mapdb");

    PortUtils.waitForAvailablePort(1883);
    PortUtils.waitForAvailablePort(8936);

    mqttBroker = new Server();
    mqttBrokerInterceptor = new BrokerInterceptor();
    List<? extends InterceptHandler>
        userHandlers =
        Collections.singletonList(mqttBrokerInterceptor);
    try {
      mqttBroker.startServer(new MemoryConfig(mqttBrokerConf), userHandlers);
    } catch (BindException ex) {
      logger.warn("Can't start server: {}", ex.getMessage());
      Thread.sleep(1000);
      mqttBroker.startServer(new MemoryConfig(mqttBrokerConf), userHandlers);
    }

    logger.debug("MQTT Broker started");

    testConfiguration = new BaseConfiguration();
    testConfiguration.addProperty("lowLevelComm.enableReconnection", true);
    testConfiguration.addProperty("lowLevelComm.reconnectInterval", 1);
    testConfiguration.addProperty("lowLevelComm.reconnectNumberOfTries", -1);
    testConfiguration.addProperty("lowLevelComm.brokerAddress", "localhost");
    testConfiguration.addProperty("lowLevelComm.brokerPort", 1883);
    testConfiguration.addProperty("lowLevelComm.connectionProtocol", "tcp");

    mqttReceiver = new MqttReceiverCallbackTestImpl();
    mqttConnectionListener = new MqttConnectionListenerTestImpl();
    mqttClient = new MqttClientSync("at.ac.ait.IT_group1_client1", testConfiguration);
    mqttClient.setReceiveCallback(mqttReceiver);
    mqttClient.addMqttConnectionListener(mqttConnectionListener);

  }


  @After
  public void tearDown() throws Exception {
    logger.debug("TearDown");

    if (mqttClient != null) {
      mqttClient.shutdown();
    }
    // Stop MQTT broker
    mqttBroker.stopServer();
    Thread.sleep(100);
    logger.debug("MQTT broker stopped");
  }

  @Test
  public void connectToBroker_correctConnection_integrationTest() {

    mqttClient.connect();
    assertEquals("at.ac.ait.IT_group1_client1",
        mqttBrokerInterceptor.getLastConnectMsg().getClientID());
  }

  @Test(expected = LowLevelCommRuntimeException.class)
  public void connectToBroker_wrongAddress_ThrowsError_integrationTest() {
    testConfiguration.setProperty("lowLevelComm.brokerAddress", "192.168.1.254");
    testConfiguration.setProperty("lowLevelComm.brokerPort", 1883);
    testConfiguration.setProperty("lowLevelComm.mqttConnectionTimeout", 2);
    MqttClientSync client2 = new MqttClientSync("at.ac.ait.IT_group1_client2", testConfiguration);
    client2.setReceiveCallback(mqttReceiver);
    client2.connect();
  }

  @Test(expected = LowLevelCommRuntimeException.class)
  public void connectToBroker_wrongPort_ThrowsError_integrationTest() {
    testConfiguration.setProperty("lowLevelComm.brokerAddress", "localhost");
    testConfiguration.setProperty("lowLevelComm.brokerPort", 1882);
    MqttClientSync client2 = new MqttClientSync("at.ac.ait.IT_group1_client2", testConfiguration);
    client2.setReceiveCallback(mqttReceiver);
    client2.addMqttConnectionListener(mqttConnectionListener);
    client2.connect();
  }

  @Test
  public void disconnectFromBroker_correctDisconnection_integrationTest() {
    mqttClient.connect();
    mqttClient.disconnect();
  }

  @Test
  public void disconnectFromBroker_NoConnectionAvailable_integrationTest() {
    mqttClient.disconnect();
  }

  @Test
  public void lostBrokerConnection_brokerShutdown_integrationTest() {

    IMqttConnectionListener listenerMock = mock(IMqttConnectionListener.class);
    mqttClient.addMqttConnectionListener(listenerMock);

    mqttClient.connect();
    assertEquals("at.ac.ait.IT_group1_client1",
        mqttBrokerInterceptor.getLastConnectMsg().getClientID());

    verify(listenerMock, times(1)).onEstablishedMqttConnection();

    mqttBroker.stopServer();

    verify(listenerMock, times(1)).onLostMqttConnection();
  }

  @Test
  public void lostBrokerConnection_reconnect_integrationTest() throws IOException {

    IMqttConnectionListener listenerMock = mock(IMqttConnectionListener.class);
    mqttClient.addMqttConnectionListener(listenerMock);

    mqttClient.connect();
    assertEquals("at.ac.ait.IT_group1_client1",
        mqttBrokerInterceptor.getLastConnectMsg().getClientID());

    mqttBroker.stopServer();

    verify(listenerMock, times(1)).onLostMqttConnection();

    try {
      Thread.sleep(6000);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    List<? extends InterceptHandler>
        userHandlers =
        Collections.singletonList(mqttBrokerInterceptor);
    mqttBroker.startServer(new MemoryConfig(mqttBrokerConf), userHandlers);
    try {
      Thread.sleep(10000);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }

    verify(listenerMock, times(2)).onEstablishedMqttConnection();
  }

  @Test
  public void subscribeMessage_oneTimeCalled_integrationTest() {
    mqttClient.connect();

    mqttClient.subscribe("Test/#");
    assertEquals("Test/#", mqttBrokerInterceptor.getLastSubscribeMsg().getTopicFilter());
    mqttClient.subscribe("Test/+/Test2/#");
    assertEquals("Test/+/Test2/#", mqttBrokerInterceptor.getLastSubscribeMsg().getTopicFilter());

    mqttClient.disconnect();
  }

  @Test
  public void subscribeMessage_twoTimesCalled_integrationTest() {
    mqttClient.connect();

    mqttClient.subscribe("Test/#");
    assertEquals("Test/#", mqttBrokerInterceptor.getLastSubscribeMsg().getTopicFilter());
    mqttClient.subscribe("Test/#");
    assertEquals("Test/#", mqttBrokerInterceptor.getLastSubscribeMsg().getTopicFilter());

    mqttClient.disconnect();
  }

  @Test(expected = LowLevelCommRuntimeException.class)
  public void subscribeMessage_NotConnected_integrationTest() {

    mqttClient.subscribe("Test/#");
    assertEquals("Test/#", mqttBrokerInterceptor.getLastSubscribeMsg().getTopicFilter());
  }

  @Test
  public void unsubscribeMessage_deleteIfAvailable_integrationTest() {
    mqttClient.connect();

    mqttClient.subscribe("Test/#");
    assertEquals("Test/#", mqttBrokerInterceptor.getLastSubscribeMsg().getTopicFilter());
    mqttClient.unsubscribe("Test/#");
    assertEquals("Test/#", mqttBrokerInterceptor.getLastUnsubscribeMsg().getTopicFilter());

    mqttClient.disconnect();
  }

  @Test(expected = LowLevelCommRuntimeException.class)
  public void unsubscribeMessage_BrokerNotAvailable_integrationTest() {
    mqttClient.unsubscribe("Test/#");
  }

  @Test
  public void unsubscribeMessage_deleteNotAvailable_integrationTest() {
    mqttClient.connect();

    mqttClient.unsubscribe("Test/#");
    assertEquals("Test/#", mqttBrokerInterceptor.getLastUnsubscribeMsg().getTopicFilter());

    mqttClient.disconnect();
  }

  @Test(expected = LowLevelCommRuntimeException.class)
  public void publishMessage_NotConnected_integrationTest() {

    mqttClient.publish("Test/Hallo", "Message".getBytes());
    InterceptPublishMessage lastPublishMsg = mqttBrokerInterceptor.getLastPublishMsg();
    assertNull(lastPublishMsg);
  }

  @Test
  public void publishMessage_Connected_integrationTest() {

    mqttClient.connect();
    mqttClient.publish("Test/Hallo", "Message".getBytes());
    InterceptPublishMessage lastPublishMsg = mqttBrokerInterceptor.getLastPublishMsg();

    if (lastPublishMsg == null) {
      fail("Broker didn't received a message. Maybe a timing problem.");
    }
    assertEquals("Test/Hallo", lastPublishMsg.getTopicName());
    assertEquals("Message", new String(lastPublishMsg.getPayload().array()));

    mqttClient.disconnect();
  }

  @Test
  public void receiveMessage_Connected_integrationTest() {

    mqttClient.connect();
    mqttClient.subscribe("Hallo/+/Test");

    PublishMessage msg = new PublishMessage();
    msg.setPayload(Charset.forName("ASCII").encode("TestString"));
    msg.setTopicName("Hallo/Topic/Test");
    msg.setQos(AbstractMessage.QOSType.LEAST_ONE);
    mqttBroker.internalPublish(msg);

    assertEquals("Hallo/Topic/Test", mqttReceiver.getLastTopic());
    assertEquals("TestString", new String(mqttReceiver.getLastPayload()));
  }

  @Test
  public void publishReceiveMessage_Connected_integrationTest() {

    mqttClient.connect();
    mqttClient.subscribe("Hallo/+/Test");

    MqttClientSync mqttClient2 =
        new MqttClientSync("at.ac.ait.IT_group1_client2", testConfiguration);
    mqttClient2.connect();
    mqttClient2.publish("Hallo/Topic/Test", "TestString".getBytes());
    mqttClient2.disconnect();
    mqttClient2.shutdown();

    assertEquals("Hallo/Topic/Test", mqttReceiver.getLastTopic());
    assertEquals("TestString", new String(mqttReceiver.getLastPayload()));

  }

  /**
   * Implementation of the callback functions for the MQTT client for testing purposes.
   */
  private class MqttReceiverCallbackTestImpl implements IMqttReceiverCallback {

    private final Object sync = new Object();
    private String lastTopic;
    private byte[] lastPayload;

    @Override
    public void handleRawMqttMessage(String topic, byte[] mqttPayload) {
      logger.debug(
          "MqttReceiverCallbackTestImpl: handleRawMqttMessage: " + topic + " (" + new String(
              mqttPayload) + ")");

      synchronized (sync) {
        lastTopic = topic;
        lastPayload = mqttPayload;

        sync.notify();
      }
    }


    public String getLastTopic() {
      String returnMsg;

      synchronized (sync) {
        if (lastTopic == null) {

          try {
            sync.wait(2000);
          } catch (InterruptedException ignored) {
            // ignore this exception
          }
        }
      }
      returnMsg = lastTopic;
      lastTopic = null;
      return returnMsg;
    }

    public byte[] getLastPayload() {
      byte[] returnMsg;

      synchronized (sync) {
        if (lastPayload == null) {

          try {
            sync.wait(2000);
          } catch (InterruptedException ignored) {
            // ignore this exception
          }
        }
      }
      returnMsg = lastPayload;
      lastPayload = null;
      return returnMsg;
    }
  }

  /**
   * Implementation of a connection listener.
   */
  private class MqttConnectionListenerTestImpl implements IMqttConnectionListener {

    @Override
    public void onEstablishedMqttConnection() {
      logger.debug("MqttConnectionListenerTestImpl: onEstablishedMqttConnection");
    }

    @Override
    public void onLostMqttConnection() {
      logger.debug("MqttConnectionListenerTestImpl: onLostMqttConnection");
    }

    @Override
    public void onDisconnectingMqttConnection() {
      logger.debug("MqttConnectionListenerTestImpl: onDisconnectingMqttConnection");
    }
  }

  /**
   * Interceptor Class for Moquette Broker.
   *
   * <p>This class will be used to used messages received by the broker for testing purposes.
   */
  private static class BrokerInterceptor extends AbstractInterceptHandler {

    private final Object syncPublish = new Object();
    private final Object syncConnect = new Object();
    private final Object syncDisconnect = new Object();
    private final Object syncSubscribe = new Object();
    private final Object syncUnsubscribe = new Object();


    private InterceptPublishMessage lastPublishMsg;
    private InterceptConnectMessage lastConnectMsg;
    private InterceptDisconnectMessage lastDisconnectMsg;
    private InterceptSubscribeMessage lastSubscribeMsg;
    private InterceptUnsubscribeMessage lastUnsubscribeMsg;


    @Override
    public void onPublish(InterceptPublishMessage msg) {
      logger.debug(
          "BrokerInterceptor: Received on topic: " + msg.getTopicName() + " content: " + new String(
              msg.getPayload().array()));
      synchronized (syncPublish) {
        lastPublishMsg = msg;
        syncPublish.notify();
      }
    }

    @Override
    public void onConnect(InterceptConnectMessage msg) {
      logger.debug("BrokerInterceptor: Client connected: " + msg.getClientID());
      synchronized (syncConnect) {
        lastConnectMsg = msg;
        syncConnect.notify();
      }
    }

    @Override
    public void onDisconnect(InterceptDisconnectMessage msg) {
      logger.debug("BrokerInterceptor: Client disconnected: " + msg.getClientID());
      synchronized (syncDisconnect) {
        lastDisconnectMsg = msg;
        syncDisconnect.notify();
      }
    }


    @Override
    public void onSubscribe(InterceptSubscribeMessage msg) {
      logger.debug(
          "BrokerInterceptor: Topic subscribed: " + msg.getClientID() + " " + msg.getTopicFilter());
      synchronized (syncSubscribe) {
        lastSubscribeMsg = msg;
        syncSubscribe.notify();
      }
    }

    @Override
    public void onUnsubscribe(InterceptUnsubscribeMessage msg) {
      logger.debug("BrokerInterceptor: Topic unsubscribed: " + msg.getClientID() + " " + msg
          .getTopicFilter());
      synchronized (syncUnsubscribe) {
        lastUnsubscribeMsg = msg;
        syncUnsubscribe.notify();
      }
    }

    InterceptPublishMessage getLastPublishMsg() {
      InterceptPublishMessage returnMsg;

      synchronized (syncPublish) {
        if (lastPublishMsg == null) {

          try {
            syncPublish.wait(2000);
          } catch (InterruptedException ignored) {
            // ignore this exception
          }
        }
      }
      returnMsg = lastPublishMsg;
      lastPublishMsg = null;
      return returnMsg;
    }

    InterceptConnectMessage getLastConnectMsg() {
      InterceptConnectMessage returnMsg;

      synchronized (syncConnect) {
        if (lastConnectMsg == null) {

          try {
            syncConnect.wait(2000);
          } catch (InterruptedException ignored) {
            // ignore this exception
          }
        }
      }
      returnMsg = lastConnectMsg;
      lastConnectMsg = null;
      return returnMsg;
    }

    InterceptDisconnectMessage getLastDisconnectMsg() {
      InterceptDisconnectMessage returnMsg;

      synchronized (syncDisconnect) {
        if (lastDisconnectMsg == null) {

          try {
            syncDisconnect.wait(2000);
          } catch (InterruptedException ignored) {
            // ignore this exception
          }
        }
      }
      returnMsg = lastDisconnectMsg;
      lastDisconnectMsg = null;
      return returnMsg;
    }

    InterceptSubscribeMessage getLastSubscribeMsg() {
      InterceptSubscribeMessage returnMsg;

      synchronized (syncSubscribe) {
        if (lastSubscribeMsg == null) {

          try {
            syncSubscribe.wait(2000);
          } catch (InterruptedException ignored) {
            // ignore this exception
          }
        }
      }
      returnMsg = lastSubscribeMsg;
      lastSubscribeMsg = null;
      return returnMsg;
    }

    InterceptUnsubscribeMessage getLastUnsubscribeMsg() {
      InterceptUnsubscribeMessage returnMsg;

      synchronized (syncUnsubscribe) {
        if (lastUnsubscribeMsg == null) {
          try {
            syncUnsubscribe.wait(2000);
          } catch (InterruptedException ignored) {
            // ignore this exception
          }
        }
      }
      returnMsg = lastUnsubscribeMsg;
      lastUnsubscribeMsg = null;
      return returnMsg;
    }
  }
}