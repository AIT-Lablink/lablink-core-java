//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.mqtt.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import at.ac.ait.lablink.core.connection.ex.LowLevelCommRuntimeException;
import at.ac.ait.lablink.core.connection.mqtt.IMqttConnectionListener;
import at.ac.ait.lablink.core.connection.mqtt.IMqttReceiverCallback;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for class MqttClientSync.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = MqttClientSync.class)
@PowerMockIgnore("javax.management.*")
public class MqttClientSyncTest {


  private MqttClient mqttClientMock;

  private IMqttReceiverCallback mqttReceiver;

  private IMqttConnectionListener mqttConnectionListener;
  private Configuration testConfiguration;

  private MqttClientSync client;


  @Before
  public void setUp() throws Exception {
    mqttClientMock = mock(MqttClient.class);
    whenNew(MqttClient.class).withAnyArguments().thenReturn(mqttClientMock);
    mqttReceiver = mock(IMqttReceiverCallback.class);
    mqttConnectionListener = mock(IMqttConnectionListener.class);

    testConfiguration = new BaseConfiguration();
    testConfiguration.addProperty("lowLevelComm.enableReconnection", false);
    testConfiguration.addProperty("lowLevelComm.reconnectInterval", 10000);
    testConfiguration.addProperty("lowLevelComm.reconnectNumberOfTries", -1);
    testConfiguration.addProperty("lowLevelComm.brokerAddress", "localhost");
    testConfiguration.addProperty("lowLevelComm.brokerPort", 1883);
    testConfiguration.addProperty("lowLevelComm.connectionProtocol", "tcp");
  }

  @After
  public void tearDown() throws Exception {

    if (client != null) {
      client.shutdown();
      client = null;
    }
  }

  @Test
  public void createMqttClient_defaultConstructor_test() {

    client = new MqttClientSync("TestClientName", null);

    assertEquals("TestClientName", client.getClientId());
    assertEquals("Default address of the broker", "tcp://localhost:1883",
        client.getBrokerAddress());
  }

  @Test
  public void createMqttClient_withBrokerConfigParameters_test() {

    testConfiguration.setProperty("lowLevelComm.brokerAddress", "127.0.0.1");
    testConfiguration.setProperty("lowLevelComm.brokerPort", 1885);
    testConfiguration.setProperty("lowLevelComm.connectionProtocol", "tcp");

    client = new MqttClientSync("TestClientNameConfig", testConfiguration);

    assertEquals("TestClientNameConfig", client.getClientId());
    assertEquals("Configured address of the broker", "tcp://127.0.0.1:1885",
        client.getBrokerAddress());
  }

  @Test
  public void createMqttClient_withBrokerConfigPartlyParametersSetOnlyBrokerAddress_test() {

    testConfiguration.setProperty("lowLevelComm.brokerAddress", "127.0.0.1");

    client = new MqttClientSync("TestClientNameConfig", testConfiguration);

    assertEquals("TestClientNameConfig", client.getClientId());
    assertEquals("Configured address of the broker", "tcp://127.0.0.1:1883",
        client.getBrokerAddress());
  }

  @Test
  public void createMqttClient_withBrokerConfigPartlyParametersSetOnlyBrokerPort_test() {

    testConfiguration.setProperty("lowLevelComm.brokerPort", 1885);

    client = new MqttClientSync("TestClientNameConfig", testConfiguration);

    assertEquals("TestClientNameConfig", client.getClientId());
    assertEquals("Configured address of the broker", "tcp://localhost:1885",
        client.getBrokerAddress());
  }

  @Test
  public void createMqttClient_withBrokerConfigPartlyParametersSetOnlyConnectionProtocol_test() {

    testConfiguration.setProperty("lowLevelComm.connectionProtocol", "ssl");

    client = new MqttClientSync("TestClientNameConfig", testConfiguration);

    assertEquals("TestClientNameConfig", client.getClientId());
    assertEquals("Configured address of the broker", "ssl://localhost:1883",
        client.getBrokerAddress());
  }

  @Test
  public void setMqttReceiver_checkIfItIsSet_test() {
    client = new MqttClientSync("TestClient", null);

    client.setReceiveCallback(mqttReceiver);
    assertEquals(mqttReceiver, client.getReceiveCallback());
  }

  @Test
  public void setMqttReceiver_OverrideMqttReceiver_test() {
    client = new MqttClientSync("TestClient", null);

    client.setReceiveCallback(mqttReceiver);
    assertEquals(mqttReceiver, client.getReceiveCallback());

    IMqttReceiverCallback cb2 = mock(IMqttReceiverCallback.class);
    client.setReceiveCallback(cb2);
    assertEquals(cb2, client.getReceiveCallback());
  }

  @Test
  public void addConnectionListener_addSingleListener_test() {
    client = new MqttClientSync("TestClient", null);

    assertTrue("Client has already connection listeners registered",
        client.getConnectionListeners().isEmpty());
    client.addMqttConnectionListener(mqttConnectionListener);
    assertEquals("One listener should be registered", 1, client.getConnectionListeners().size());
    assertTrue("Listener isn't registered",
        client.getConnectionListeners().contains(mqttConnectionListener));
  }

  @Test
  public void addConnectionListener_addMultipleListener_test() {

    client = new MqttClientSync("TestClient", null);
    IMqttConnectionListener listener1 = mock(IMqttConnectionListener.class);

    assertTrue("Client has already connection listeners registered",
        client.getConnectionListeners().isEmpty());
    client.addMqttConnectionListener(mqttConnectionListener);
    client.addMqttConnectionListener(listener1);
    assertEquals("Two listener should be registered", 2, client.getConnectionListeners().size());
    assertTrue("Listener isn't registered",
        client.getConnectionListeners().contains(mqttConnectionListener));
    assertTrue("Listener isn't registered", client.getConnectionListeners().contains(listener1));
  }

  @Test
  public void addConnectionListener_addSameListenerTwice_test() {
    client = new MqttClientSync("TestClient", null);
    IMqttConnectionListener listener1 = mock(IMqttConnectionListener.class);

    assertTrue("Client has already connection listeners registered",
        client.getConnectionListeners().isEmpty());
    client.addMqttConnectionListener(mqttConnectionListener);
    client.addMqttConnectionListener(listener1);
    client.addMqttConnectionListener(mqttConnectionListener);
    assertEquals("Two listener should be registered", 2, client.getConnectionListeners().size());
    assertTrue("Listener isn't registered",
        client.getConnectionListeners().contains(mqttConnectionListener));
    assertTrue("Listener isn't registered", client.getConnectionListeners().contains(listener1));
  }

  @Test
  public void removeConnectionListener_removeAvailableListener_test() {
    client = new MqttClientSync("TestClient", null);
    IMqttConnectionListener listener1 = mock(IMqttConnectionListener.class);

    assertTrue("Client has already connection listeners registered",
        client.getConnectionListeners().isEmpty());

    client.addMqttConnectionListener(mqttConnectionListener);
    client.addMqttConnectionListener(listener1);
    assertEquals("Two listener should be registered", 2, client.getConnectionListeners().size());
    assertTrue("Listener isn't registered", client.getConnectionListeners().contains(listener1));
    client.removeConnectionListener(listener1);
    assertEquals("One listener should be registered", 1, client.getConnectionListeners().size());
    assertTrue("Listener isn't registered",
        client.getConnectionListeners().contains(mqttConnectionListener));
  }

  @Test
  public void removeConnectionListener_removeNonAvailableListener_test() {
    client = new MqttClientSync("TestClient", null);
    IMqttConnectionListener listener1 = mock(IMqttConnectionListener.class);

    assertTrue("Client has already connection listeners registered",
        client.getConnectionListeners().isEmpty());

    client.addMqttConnectionListener(mqttConnectionListener);

    client.removeConnectionListener(listener1);
    assertEquals("One listener should be registered", 1, client.getConnectionListeners().size());
    assertTrue("Listener isn't registered",
        client.getConnectionListeners().contains(mqttConnectionListener));
  }

  @Test
  public void removeConnectionListener_removeFromEmptyList_test() {

    client = new MqttClientSync("TestClient", null);

    assertTrue("Client has already connection listeners registered",
        client.getConnectionListeners().isEmpty());

    client.removeConnectionListener(mqttConnectionListener);
    assertTrue("There shouldn't be any connection listeners registered",
        client.getConnectionListeners().isEmpty());
  }

  @Test
  public void receiveMessageArrived_informCallback_test() throws Exception {

    client = new MqttClientSync("testId", null);
    MqttReceivedMessage msg = new MqttReceivedMessage();
    msg.setPayload("TestPayload".getBytes());

    //No received callback set
    msg.setDuplicate(false);
    client.messageArrived("testTopic", msg);
    msg.setDuplicate(true);
    client.messageArrived("testTopic", msg);

    client.setReceiveCallback(mqttReceiver);
    msg.setDuplicate(false);
    client.messageArrived("testTopic", msg);

    client.setReceiveCallback(mqttReceiver);
    msg.setDuplicate(true);
    client.messageArrived("testTopic", msg);

    Thread.sleep(200);
    verify(mqttReceiver, times(1)).handleRawMqttMessage("testTopic", msg.getPayload());
  }

  @Test
  public void onLostMqttConnection_callConnectionListener_test() {
    client = new MqttClientSync("TestClient", null);
    client.addMqttConnectionListener(mqttConnectionListener);

    Throwable testThrow = new Throwable("Test");
    client.connectionLost(testThrow);

    verify(mqttConnectionListener, times(1)).onLostMqttConnection();
  }

  @Test
  public void onEstablishedConnection_callConnectionListener_test() {
    client = new MqttClientSync("TestClient", null);
    client.addMqttConnectionListener(mqttConnectionListener);

    client.connect();

    verify(mqttConnectionListener, times(1)).onEstablishedMqttConnection();
  }

  @Test
  public void onDisconnectionConnection_callConnectionListener_test() {
    client = new MqttClientSync("TestClient", null);
    client.addMqttConnectionListener(mqttConnectionListener);

    when(mqttClientMock.isConnected()).thenReturn(false);
    client.connect();
    when(mqttClientMock.isConnected()).thenReturn(true);
    client.disconnect();

    verify(mqttConnectionListener, times(1)).onEstablishedMqttConnection();
  }

  @Test
  public void subscribeTopic_connectedClient_test() throws MqttException {
    client = new MqttClientSync("testId", null);
    client.connect();
    when(mqttClientMock.isConnected()).thenReturn(true);

    client.subscribe("TestTopic");
    verify(mqttClientMock).subscribe("TestTopic");
  }


  @Test(expected = LowLevelCommRuntimeException.class)
  public void subscribeTopic_disconnectedClient_test() throws MqttException {
    client = new MqttClientSync("TestId", null);
    when(mqttClientMock.isConnected()).thenReturn(false);

    client.subscribe("TestTopic");
    verify(mqttClientMock).subscribe("TestTopic");
  }

  @Test
  public void unsubscribeTopic_connectedClient_test() throws MqttException {
    client = new MqttClientSync("TestId", null);
    client.connect();
    when(mqttClientMock.isConnected()).thenReturn(true);

    client.unsubscribe("TestTopic");
    verify(mqttClientMock).unsubscribe("TestTopic");
  }


  @Test(expected = LowLevelCommRuntimeException.class)
  public void unsubscribeTopic_disconnectedClient_test() throws MqttException {
    client = new MqttClientSync("TestId", null);
    when(mqttClientMock.isConnected()).thenReturn(false);

    client.unsubscribe("TestTopic");
    verify(mqttClientMock).unsubscribe("TestTopic");
  }


  @Test
  public void subscribeTopics_connectedClient_test() throws MqttException {
    client = new MqttClientSync("TestId", null);
    client.connect();
    when(mqttClientMock.isConnected()).thenReturn(true);

    List<String> parameter = new ArrayList<String>();
    parameter.add("at/ac/test/hallo");
    parameter.add("at/+/test/hallo");
    parameter.add("at/ac/test/+");
    parameter.add("at/ac/test/#");

    client.subscribe(parameter);
    verify(mqttClientMock, times(1)).subscribe((String[]) any());
  }

  @Test
  public void unsubscribeTopics_connectedClient_test() throws MqttException {
    client = new MqttClientSync("TestId", null);
    client.connect();
    when(mqttClientMock.isConnected()).thenReturn(true);

    List<String> parameter = new ArrayList<String>();
    parameter.add("at/ac/test/hallo");
    parameter.add("at/+/test/hallo");
    parameter.add("at/ac/test/+");
    parameter.add("at/ac/test/#");

    client.unsubscribe(parameter);
    verify(mqttClientMock, times(1)).unsubscribe((String[]) any());
  }


  @Test
  public void publishMessage_connectedClient_test() throws MqttException {
    client = new MqttClientSync("TestId", null);
    client.connect();
    when(mqttClientMock.isConnected()).thenReturn(true);

    client.publish("TestTopic", "Test".getBytes());

    ArgumentCaptor<MqttMessage> argumentCaptor = ArgumentCaptor.forClass(MqttMessage.class);
    verify(mqttClientMock).publish(eq("TestTopic"), argumentCaptor.capture());
    MqttMessage capturedMsg = argumentCaptor.<MqttMessage>getValue();

    assertEquals(0, capturedMsg.getQos());
    assertEquals("Test", new String(capturedMsg.getPayload()));
  }

  @Test(expected = LowLevelCommRuntimeException.class)
  public void publishMessage_disconnectedClient_test() throws MqttException {

    client = new MqttClientSync("TestId", null);
    when(mqttClientMock.isConnected()).thenReturn(false);

    client.publish("TestTopic", "Test".getBytes());

    ArgumentCaptor<MqttMessage> argumentCaptor = ArgumentCaptor.forClass(MqttMessage.class);
    verify(mqttClientMock).publish(eq("TestTopic"), argumentCaptor.capture());
    MqttMessage capturedMsg = argumentCaptor.<MqttMessage>getValue();

    assertEquals(0, capturedMsg.getQos());
    assertEquals("Test", new String(capturedMsg.getPayload()));
  }

  @Test
  public void connectClient_canConnect_test() throws MqttException {

    client = new MqttClientSync("TestId", null);
    client.addMqttConnectionListener(mqttConnectionListener);
    IMqttConnectionListener listener1 = mock(IMqttConnectionListener.class);
    client.addMqttConnectionListener(listener1);
    client.connect();

    verify(mqttClientMock).connect(Mockito.<MqttConnectOptions>any());
    assertEquals(MqttClientSync.ELlClientState.CONNECTED_TO_BROKER, client.getCurrentClientState());
    verify(mqttConnectionListener, times(1)).onEstablishedMqttConnection();
    verify(listener1, times(1)).onEstablishedMqttConnection();
  }

  @Test(expected = LowLevelCommRuntimeException.class)
  public void connectClient_cannotConnect_test() throws MqttException {

    client = new MqttClientSync("TestId", null);
    doThrow(new MqttException(-1)).when(mqttClientMock).connect(any(MqttConnectOptions.class));

    client.connect();
  }

  @Test(expected = LowLevelCommRuntimeException.class)
  public void connectClient_cannotCreateClient_test() throws Exception {

    client = new MqttClientSync("TestId", null);
    whenNew(MqttClient.class).withAnyArguments().thenThrow(new MqttException(-1));

    client.connect();
  }

  @Test
  public void connectClient_doubleConnect_test() throws MqttException {

    client = new MqttClientSync("TestId", null);
    client.addMqttConnectionListener(mqttConnectionListener);
    IMqttConnectionListener listener1 = mock(IMqttConnectionListener.class);
    client.addMqttConnectionListener(listener1);
    when(mqttClientMock.isConnected()).thenReturn(false);
    client.connect();
    when(mqttClientMock.isConnected()).thenReturn(true);
    client.connect();
    verify(mqttClientMock, times(1)).connect(Mockito.<MqttConnectOptions>any());
    assertEquals(MqttClientSync.ELlClientState.CONNECTED_TO_BROKER, client.getCurrentClientState());
    verify(mqttConnectionListener, times(1)).onEstablishedMqttConnection();
    verify(listener1, times(1)).onEstablishedMqttConnection();
  }

  @Test
  public void disconnectClient_alreadyDisconnected_test()
      throws MqttException {

    client = new MqttClientSync("TestId", null);
    client.addMqttConnectionListener(mqttConnectionListener);
    IMqttConnectionListener listener1 = mock(IMqttConnectionListener.class);
    client.addMqttConnectionListener(listener1);
    when(mqttClientMock.isConnected()).thenReturn(false);

    client.disconnect();
    verify(mqttClientMock, times(0)).disconnect();
    assertEquals(MqttClientSync.ELlClientState.DISCONNECTED_FROM_BROKER,
        client.getCurrentClientState());
    verify(mqttConnectionListener, times(0)).onDisconnectingMqttConnection();
    verify(listener1, times(0)).onDisconnectingMqttConnection();
  }

  @Test
  public void disconnectClient_whenConnected_test() throws MqttException {

    client = new MqttClientSync("TestId", null);
    client.addMqttConnectionListener(mqttConnectionListener);
    IMqttConnectionListener listener1 = mock(IMqttConnectionListener.class);
    client.addMqttConnectionListener(listener1);
    when(mqttClientMock.isConnected()).thenReturn(false);
    client.connect();
    when(mqttClientMock.isConnected()).thenReturn(true);

    client.disconnect();
    verify(mqttClientMock, times(1)).disconnect();
    assertEquals(MqttClientSync.ELlClientState.DISCONNECTED_FROM_BROKER,
        client.getCurrentClientState());
    verify(mqttConnectionListener, times(1)).onDisconnectingMqttConnection();
    verify(listener1, times(1)).onDisconnectingMqttConnection();
  }

  @Test(expected = LowLevelCommRuntimeException.class)
  public void disconnectClient_cannotDisconnect_test() throws MqttException {

    client = new MqttClientSync("TestId", null);

    doThrow(new MqttException(-1)).when(mqttClientMock).disconnect();
    client.addMqttConnectionListener(mqttConnectionListener);
    IMqttConnectionListener listener1 = mock(IMqttConnectionListener.class);
    client.addMqttConnectionListener(listener1);
    when(mqttClientMock.isConnected()).thenReturn(false);
    client.connect();
    when(mqttClientMock.isConnected()).thenReturn(true);

    client.disconnect();
  }

  @Test
  public void connectionLost_callConnectionListener_test() {
    client = new MqttClientSync("TestId", null);

    Throwable testThrow = new Throwable("Test");

    client.connectionLost(testThrow);
    client.setReceiveCallback(mqttReceiver);
    client.addMqttConnectionListener(mqttConnectionListener);
    client.connectionLost(testThrow);

    verify(mqttConnectionListener, times(1)).onLostMqttConnection();
  }

  @Test
  public void shutdownClient_testIfItWillDisconnect_test()
      throws MqttException {
    client = new MqttClientSync("TestId", null);
    when(mqttClientMock.isConnected()).thenReturn(true);
    client.connect();

    client.shutdown();

    verify(mqttClientMock, times(1)).disconnect();
  }

  @Test
  public void reconnect_disabledReconnectionOnConnect_test() throws MqttException {
    testConfiguration.setProperty("lowLevelComm.enableReconnection", false);
    testConfiguration.setProperty("lowLevelComm.reconnectInterval", 10);

    client = new MqttClientSync("testId", testConfiguration);
    when(mqttClientMock.isConnected()).thenReturn(false);
    doThrow(new MqttException(-1)).when(mqttClientMock).connect(any(MqttConnectOptions.class));

    try {
      client.connect();
    } catch (LowLevelCommRuntimeException ign) {
      // This is expected.
    }

    try {
      Thread.sleep(500);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }

    assertEquals(MqttClientSync.ELlClientState.DISCONNECTED_FROM_BROKER,
        client.getCurrentClientState());
  }

  @Test
  public void reconnect_disabledReconnectionOnConnectionLost_test() throws MqttException {
    testConfiguration.setProperty("lowLevelComm.enableReconnection", false);
    testConfiguration.setProperty("lowLevelComm.reconnectInterval", 10);

    client = new MqttClientSync("testId", testConfiguration);
    when(mqttClientMock.isConnected()).thenReturn(false);

    Throwable testThrow = new Throwable("Test");
    client.connectionLost(testThrow);

    try {
      Thread.sleep(500);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }

    assertEquals(MqttClientSync.ELlClientState.DISCONNECTED_FROM_BROKER,
        client.getCurrentClientState());
  }

  @Test
  public void reconnect_enabledReconnectionOnConnect_test() throws MqttException {
    testConfiguration.setProperty("lowLevelComm.enableReconnection", true);
    testConfiguration.setProperty("lowLevelComm.reconnectInterval", 1);
    testConfiguration.setProperty("lowLevelComm.reconnectNumberOfTries", -1);

    client = new MqttClientSync("testId", testConfiguration);
    when(mqttClientMock.isConnected()).thenReturn(false);
    doThrow(new MqttException(-1)).when(mqttClientMock).connect(any(MqttConnectOptions.class));

    try {
      client.connect();
    } catch (LowLevelCommRuntimeException ign) {
      //This is expected
    }

    try {
      Thread.sleep(10000);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }

    assertEquals(MqttClientSync.ELlClientState.TRY_TO_RECONNECT, client.getCurrentClientState());
    verify(mqttClientMock, atLeast(5)).connect(any(MqttConnectOptions.class));
  }

  @Test
  public void reconnect_enabledReconnectionTestMaximumTries_test() throws MqttException {
    testConfiguration.setProperty("lowLevelComm.enableReconnection", true);
    testConfiguration.setProperty("lowLevelComm.reconnectInterval", 1);
    testConfiguration.setProperty("lowLevelComm.reconnectNumberOfTries", 5);

    client = new MqttClientSync("testId", testConfiguration);
    when(mqttClientMock.isConnected()).thenReturn(false);
    doThrow(new MqttException(-1)).when(mqttClientMock).connect(any(MqttConnectOptions.class));
    try {
      client.connect();
    } catch (LowLevelCommRuntimeException ign) {
      //This is expected.
    }

    assertEquals(MqttClientSync.ELlClientState.TRY_TO_RECONNECT, client.getCurrentClientState());

    try {
      Thread.sleep(10000);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }

    assertEquals(MqttClientSync.ELlClientState.DISCONNECTED_FROM_BROKER,
        client.getCurrentClientState());
    verify(mqttClientMock, times(6))
        .connect(any(MqttConnectOptions.class)); //5 times from reconnect + 1 from connect
  }

  @Test
  public void reconnect_enabledReconnectionOnConnectionLost_test() throws MqttException {
    testConfiguration.setProperty("lowLevelComm.enableReconnection", true);
    testConfiguration.setProperty("lowLevelComm.reconnectInterval", 1);
    testConfiguration.setProperty("lowLevelComm.reconnectNumberOfTries", -1);

    client = new MqttClientSync("testId", testConfiguration);
    when(mqttClientMock.isConnected()).thenReturn(false);
    doThrow(new MqttException(-1)).when(mqttClientMock).connect(any(MqttConnectOptions.class));
    Throwable testThrow = new Throwable("Test");
    client.connectionLost(testThrow);

    try {
      Thread.sleep(10000);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }

    assertEquals(MqttClientSync.ELlClientState.TRY_TO_RECONNECT, client.getCurrentClientState());
    verify(mqttClientMock, atLeast(5)).connect(any(MqttConnectOptions.class));
  }
}
