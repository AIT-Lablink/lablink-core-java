//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import at.ac.ait.lablink.core.connection.ILlConnection;
import at.ac.ait.lablink.core.connection.encoding.encodables.Header;
import at.ac.ait.lablink.core.connection.encoding.encodables.IPayload;
import at.ac.ait.lablink.core.connection.messaging.IMessageCallback;
import at.ac.ait.lablink.core.connection.messaging.MsgHeader;
import at.ac.ait.lablink.core.connection.rpc.IRpcRequester;
import at.ac.ait.lablink.core.connection.rpc.RpcHeader;
import at.ac.ait.lablink.core.connection.rpc.reply.IRpcReplyCallback;
import at.ac.ait.lablink.core.connection.rpc.request.IRpcRequestCallback;
import at.ac.ait.lablink.core.connection.topic.MsgSubject;
import at.ac.ait.lablink.core.connection.topic.MsgSubscription;
import at.ac.ait.lablink.core.connection.topic.RpcDestination;
import at.ac.ait.lablink.core.connection.topic.RpcSubject;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;
import at.ac.ait.lablink.core.payloads.ErrorMessage;
import at.ac.ait.lablink.core.payloads.StatusMessage;
import at.ac.ait.lablink.core.service.datapoint.payloads.StringValue;

import io.moquette.server.Server;
import io.moquette.server.config.MemoryConfig;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.net.BindException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Base for integration tests for ILlConnection implementations.
 */
public abstract class LablinkConnectionBaseIT {

  private static final Logger logger = LogManager.getLogger();


  protected Configuration testConfiguration;
  protected ILlConnection labLinkConnection;

  // Broker instances for testing
  private Server mqttBroker;
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
    try {
      mqttBroker.startServer(new MemoryConfig(mqttBrokerConf));
    } catch (BindException ex) {
      logger.warn("Can't start server: " + ex.getMessage());
      Thread.sleep(1000);
      mqttBroker.startServer(new MemoryConfig(mqttBrokerConf));
    }
    logger.debug("MQTT Broker started");

    testConfiguration = new BaseConfiguration();
    //testConfiguration.addProperty("lowLevelComm.brokerAddress", "localhost");
    //testConfiguration.addProperty("lowLevelComm.brokerPort", 1883);
    //testConfiguration.addProperty("lowLevelComm.connectionProtocol", "tcp");
    //testConfiguration.addProperty("lowLevelComm.enableReconnection", true);
    //testConfiguration.addProperty("lowLevelComm.reconnectInterval", 10);
    //testConfiguration.addProperty("lowLevelComm.reconnectNumberOfTries", -1);
    //testConfiguration.addProperty("lowLevelComm.mqttConnectionTimeout", 30);
    //testConfiguration.addProperty("lowLevelComm.receivedMessagesQueueSize", 100);
    //testConfiguration.addProperty("encoding.maxStackSize", 200);
    //testConfiguration.addProperty("rpc.request.noOfReturns", 1);
    testConfiguration.addProperty("rpc.request.timeoutMs", 1000);

  }


  @After
  public void tearDown() throws Exception {
    logger.debug("TearDown");

    //Stop the Lablink Connection
    if (labLinkConnection != null) {
      labLinkConnection.shutdown();
    }

    // Stop MQTT broker
    mqttBroker.stopServer();
    Thread.sleep(100);
    logger.debug("MQTT broker stopped");
  }

  @Test
  public void initialisation_clientShouldBeDisconnected_test() throws Exception {
    assertFalse(labLinkConnection.isConnected());
  }

  @Test
  public void connect_clientShouldBeConnected_test() throws Exception {
    labLinkConnection.connect();
    assertTrue(labLinkConnection.isConnected());
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void connect_NoBrokerAvailable_ThrowError_test() throws Exception {
    mqttBroker.stopServer();
    Thread.sleep(100);

    labLinkConnection.connect();
  }

  @Test
  public void disconnect_clientShouldBeDisconnected_test() throws Exception {
    labLinkConnection.connect();
    labLinkConnection.disconnect();
    assertFalse(labLinkConnection.isConnected());
  }


  @Test
  public void disconnect_NotConnected_DoNothing_test() throws Exception {
    labLinkConnection.disconnect();
  }


  @Test
  public void disconnect_BrokerLost_DoNothing_test() throws Exception {
    labLinkConnection.connect();
    mqttBroker.stopServer();
    Thread.sleep(100);
    labLinkConnection.disconnect();
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void publishMessage_NotConnected_ShouldThrow_test() throws Exception {
    MsgSubject subject = MsgSubject.getBuilder().addSubjectElement("Test").build();
    IPayload payload = new StatusMessage(StatusMessage.StatusCode.OK);
    labLinkConnection.publishMessage(subject, payload);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void publishMessage_EmptySubject_ShouldThrow_test() throws Exception {
    labLinkConnection.connect();
    MsgSubject subject = MsgSubject.getBuilder().build();
    IPayload payload = new StatusMessage(StatusMessage.StatusCode.OK);
    labLinkConnection.publishMessage(subject, payload);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void publishMessage_NoPayload_ShouldThrow_test() throws Exception {
    labLinkConnection.connect();
    MsgSubject subject = MsgSubject.getBuilder().addSubjectElement("Test").build();
    IPayload payload = null;
    labLinkConnection.publishMessage(subject, payload);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void publishMessage_NoPayloadList_ShouldThrow_test() throws Exception {
    labLinkConnection.connect();
    MsgSubject subject = MsgSubject.getBuilder().addSubjectElement("Test").build();
    List<IPayload> payloads = null;
    labLinkConnection.publishMessage(subject, payloads);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void publishMessage_EmptyPayloadList_ShouldThrow_test() throws Exception {
    labLinkConnection.connect();
    MsgSubject subject = MsgSubject.getBuilder().addSubjectElement("Test").build();
    List<IPayload> payloads = new ArrayList<IPayload>();
    labLinkConnection.publishMessage(subject, payloads);
  }

  @Test
  public void publishMessage_SinglePayload_ShouldBeSent_test() throws Exception {
    labLinkConnection.connect();
    MsgSubject subject = MsgSubject.getBuilder().addSubjectElement("Test").build();
    IPayload payload = new StatusMessage(StatusMessage.StatusCode.OK);
    labLinkConnection.publishMessage(subject, payload);
  }

  @Test
  public void publishMessage_PayloadListSamePayloads_ShouldBeSent_test() throws Exception {
    labLinkConnection.connect();
    MsgSubject subject = MsgSubject.getBuilder().addSubjectElement("Test").build();
    List<IPayload> payloads = new ArrayList<IPayload>();
    IPayload payload = new StatusMessage(StatusMessage.StatusCode.OK);
    for (int i = 0; i < 5; i++) {
      payloads.add(payload);
    }
    labLinkConnection.publishMessage(subject, payloads);
  }

  @Test
  public void publishMessage_PayloadListMultiplePayloads_ShouldBeSent_test() throws Exception {
    labLinkConnection.connect();
    List<IPayload> payloads = new ArrayList<IPayload>();
    payloads.add(new StatusMessage(StatusMessage.StatusCode.OK));
    payloads.add(new ErrorMessage(ErrorMessage.EErrorCode.PROCESSING_ERROR, "TestErrorMessage"));
    payloads.add(new StatusMessage(StatusMessage.StatusCode.OK));
    payloads.add(new ErrorMessage(ErrorMessage.EErrorCode.VALIDATION_ERROR, "TestErrorMessage2"));
    payloads.add(new StatusMessage(StatusMessage.StatusCode.OK));
    MsgSubject subject = MsgSubject.getBuilder().addSubjectElement("Test").build();
    labLinkConnection.publishMessage(subject, payloads);
  }


  @Test
  public void publishMessage_MultiSenderSamePayload_test() throws Exception {
    labLinkConnection.connect();
    final MsgSubject subject = MsgSubject.getBuilder().addSubjectElement("Test").build();
    final IPayload payload = new StatusMessage(StatusMessage.StatusCode.OK);

    PublishMultiExecutor[] threads = new PublishMultiExecutor[10];

    for (int i = 0; i < 10; i++) {
      threads[i] = new PublishMultiExecutor(subject, Collections.singletonList(payload));
    }
    for (int i = 0; i < 10; i++) {
      threads[i].start();
    }
    for (int i = 0; i < 10; i++) {
      threads[i].join();
      assertFalse("An exception occurs during parallel execution (Nr. " + i + ")",
          threads[i].exceptionOccurs);
    }
  }


  @Test
  public void publishMessage_MultiSenderDifferentPayload_test() throws Exception {
    labLinkConnection.connect();
    final MsgSubject subject = MsgSubject.getBuilder().addSubjectElement("Test").build();
    final List<IPayload> payloads = new ArrayList<IPayload>();
    payloads.add(new StatusMessage(StatusMessage.StatusCode.OK));
    payloads.add(new ErrorMessage(ErrorMessage.EErrorCode.PROCESSING_ERROR, "TestErrorMessage"));
    payloads.add(new StatusMessage(StatusMessage.StatusCode.OK));
    payloads.add(new ErrorMessage(ErrorMessage.EErrorCode.VALIDATION_ERROR, "TestErrorMessage2"));
    payloads.add(new StatusMessage(StatusMessage.StatusCode.OK));

    PublishMultiExecutor[] threads = new PublishMultiExecutor[10];
    for (int i = 0; i < 10; i++) {
      threads[i] = new PublishMultiExecutor(subject, payloads);
    }

    for (int i = 0; i < 10; i++) {
      threads[i].start();
    }
    for (int i = 0; i < 10; i++) {
      threads[i].join();
      assertFalse("An exception occurs during parallel execution (Nr. " + i + ")",
          threads[i].exceptionOccurs);
    }
  }

  private class PublishMultiExecutor extends Thread {

    boolean exceptionOccurs = false;

    private MsgSubject subject;
    private List<IPayload> payloads;

    public PublishMultiExecutor(MsgSubject subject, List<IPayload> payloads) {
      this.subject = subject;
      this.payloads = payloads;
    }

    @Override
    public void run() {
      try {
        labLinkConnection.publishMessage(subject, payloads);
      } catch (Exception ex) {
        ex.printStackTrace();
        exceptionOccurs = true;
      }
    }
  }

  @Test
  public void publishMessage_publishStatusMessage_test() throws Exception {
    labLinkConnection.connect();
    MsgSubject subject = MsgSubject.getBuilder().addSubjectElement("Test").build();
    IPayload payload = new StatusMessage(StatusMessage.StatusCode.OK);
    labLinkConnection.publishMessage(subject, payload);
  }

  @Test
  public void publishMessage_publishErrorMessage_test() throws Exception {
    labLinkConnection.connect();
    MsgSubject subject = MsgSubject.getBuilder().addSubjectElement("Test").build();
    IPayload payload = 
        new ErrorMessage(ErrorMessage.EErrorCode.PROCESSING_ERROR, "TestErrorMessage");
    labLinkConnection.publishMessage(subject, payload);
  }

  @Test
  public void publishMessage_publishStringValue_test() throws Exception {
    labLinkConnection.connect();
    MsgSubject subject = MsgSubject.getBuilder().addSubjectElement("Test").build();
    IPayload payload = new StringValue("Hallo Welt");
    labLinkConnection.publishMessage(subject, payload);
  }

  @Test(expected = NullPointerException.class)
  public void registerMessageHandler_registerNullObject_ThrowException_test() throws Exception {
    MsgSubscription
        subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectAllChildren().build();
    labLinkConnection.registerMessageHandler(subscription, null);
  }

  private class TestMsgCallback implements IMessageCallback {

    @Override
    public void handleError(Header header, List<ErrorMessage> errors) throws Exception {
      logger.info("HandleError called " + errors);
    }

    @Override
    public void handleMessage(MsgHeader header, List<IPayload> payloads) throws Exception {
      logger.info("handleMessage called Header: " + header 
          + " (" + payloads + ") " + payloads.size());
    }
  }

  @Test(expected = NullPointerException.class)
  public void registerMessageHandler_registerNullSubject_ThrowException_test() throws Exception {
    IMessageCallback callback = new TestMsgCallback();
    labLinkConnection.registerMessageHandler(null, callback);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void registerMessageHandler_registerEmptySubject_ThrowException_test() throws Exception {
    MsgSubscription
        subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL).build();
    IMessageCallback callback = new TestMsgCallback();
    labLinkConnection.registerMessageHandler(subscription, callback);
  }

  @Test(expected = NullPointerException.class)
  public void registerMessageHandler_registerNullElementSubject_ThrowException_test()
      throws Exception {
    MsgSubscription
        subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_GROUP)
            .setSrcGroupId(null).build();
    IMessageCallback callback = new TestMsgCallback();
    labLinkConnection.registerMessageHandler(subscription, callback);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void registerMessageHandler_registerEmptyElementSubject_ThrowException_test()
      throws Exception {
    MsgSubscription
        subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_GROUP)
            .setSrcGroupId("").build();
    IMessageCallback callback = new TestMsgCallback();
    labLinkConnection.registerMessageHandler(subscription, callback);
  }

  @Test
  public void registerMessageHandler_registerSameCallbackTwice_ThrowException_test()
      throws Exception {
    MsgSubscription
        subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectAllChildren().build();
    IMessageCallback callback = new TestMsgCallback();
    labLinkConnection.registerMessageHandler(subscription, callback);
    labLinkConnection.registerMessageHandler(subscription, callback);
  }

  @Test
  public void registerMessageHandler_registerSameCallbackDifferentSubjects_DoNothing_test()
      throws Exception {
    MsgSubscription
        subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectAllChildren().build();
    IMessageCallback callback = new TestMsgCallback();
    labLinkConnection.registerMessageHandler(subscription, callback);
    subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectElement("Test").addSubjectAnyElement().build();
    labLinkConnection.registerMessageHandler(subscription, callback);
  }

  @Test
  public void registerMessageHandler_registerDifferentCallbacksSameSubject_DoNothing_test()
      throws Exception {
    MsgSubscription
        subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectAllChildren().build();
    IMessageCallback callback = new TestMsgCallback();
    labLinkConnection.registerMessageHandler(subscription, callback);

    callback = new TestMsgCallback();
    labLinkConnection.registerMessageHandler(subscription, callback);
  }

  @Test
  public void unregisterMessageHandler_unregisterExistingOne_CorrectWork_test() throws Exception {
    MsgSubscription
        subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectAllChildren().build();
    IMessageCallback callback = new TestMsgCallback();
    labLinkConnection.registerMessageHandler(subscription, callback);

    labLinkConnection.unregisterMessageHandler(subscription, callback);
  }

  @Test
  public void unregisterMessageHandler_unregisterNonAvailableSubject_ThrowException_test()
      throws Exception {

    MsgSubscription
        subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectAllChildren().build();
    IMessageCallback callback = new TestMsgCallback();
    labLinkConnection.registerMessageHandler(subscription, callback);

    subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectElement("Test").addSubjectAllChildren().build();
    labLinkConnection.unregisterMessageHandler(subscription, callback);
  }


  @Test
  public void unregisterMessageHandler_unregisterNonAvailableCallback_CorrectWork_test()
      throws Exception {
    MsgSubscription
        subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectAllChildren().build();
    IMessageCallback callback = new TestMsgCallback();
    labLinkConnection.registerMessageHandler(subscription, callback);
    IMessageCallback callback2 = new TestMsgCallback();

    labLinkConnection.unregisterMessageHandler(subscription, callback2);
  }

  @Test
  public void unregisterMessageHandler_unregisterTwoCallbacks_CorrectWork_test() throws Exception {
    MsgSubscription
        subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectAllChildren().build();
    IMessageCallback callback = new TestMsgCallback();
    labLinkConnection.registerMessageHandler(subscription, callback);
    IMessageCallback callback2 = new TestMsgCallback();
    labLinkConnection.registerMessageHandler(subscription, callback2);

    labLinkConnection.unregisterMessageHandler(subscription, callback);
    labLinkConnection.unregisterMessageHandler(subscription, callback2);
  }

  @Test(expected = NullPointerException.class)
  public void unregisterMessageHandler_NullSubject_ThrowException_test() throws Exception {
    IMessageCallback callback = new TestMsgCallback();
    labLinkConnection.unregisterMessageHandler(null, callback);
  }

  @Test
  public void unregisterMessageHandler_NullCallback_DoNothing_test() throws Exception {
    MsgSubscription
        subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectAllChildren().build();
    labLinkConnection.unregisterMessageHandler(subscription, null);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void receiveMessage_singleHandler_BeCalledOnce_test() throws Exception {
    TestMsgCallback callback = spy(new TestMsgCallback());
    MsgSubscription
        subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectElement("Test").build();
    labLinkConnection.connect();
    labLinkConnection.registerMessageHandler(subscription, callback);

    labLinkConnection.publishMessage(MsgSubject.getBuilder().addSubjectElement("Test").build(),
        new StatusMessage(StatusMessage.StatusCode.OK));
    Thread.sleep(300);
    verify(callback, times(1)).handleMessage(any(MsgHeader.class), anyList());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void receiveMessage_TwoHandlerSameSubject_BothShouldBeCalled_test() throws Exception {

    TestMsgCallback callback = spy(new TestMsgCallback());
    TestMsgCallback callback2 = spy(new TestMsgCallback());
    MsgSubscription
        subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectElement("Test").build();
    labLinkConnection.connect();
    labLinkConnection.registerMessageHandler(subscription, callback);
    labLinkConnection.registerMessageHandler(subscription, callback2);

    labLinkConnection.publishMessage(MsgSubject.getBuilder().addSubjectElement("Test").build(),
        new StatusMessage(StatusMessage.StatusCode.OK));
    Thread.sleep(600);
    verify(callback, times(1)).handleMessage(any(MsgHeader.class), anyList());
    verify(callback2, times(1)).handleMessage(any(MsgHeader.class), anyList());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void receiveMessage_SameHandlerDifferentSubjects_ShouldBeCalledTwice_test()
      throws Exception {
    TestMsgCallback callback = spy(new TestMsgCallback());
    MsgSubscription
        subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectElement("Test").addSubjectAllChildren().build();
    labLinkConnection.connect();
    labLinkConnection.registerMessageHandler(subscription, callback);
    subscription =
        MsgSubscription.getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
            .addSubjectElement("Test").addSubjectAnyElement().addSubjectElement("Hallo").build();
    labLinkConnection.registerMessageHandler(subscription, callback);

    labLinkConnection.publishMessage(
        MsgSubject.getBuilder().addSubjectElement("Test").addSubjectElement("Baba")
            .addSubjectElement("Hallo").build(), new StatusMessage(StatusMessage.StatusCode.OK));
    Thread.sleep(600);
    verify(callback, times(2)).handleMessage(any(MsgHeader.class), anyList());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void receiveMessage_TwoHandlerDifferentSubjects_OnlyOneShouldBeCalled_test()
      throws Exception {

    TestMsgCallback callback = spy(new TestMsgCallback());
    MsgSubscription subscription = MsgSubscription
        .getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
        .addSubjectElement("Test").build();
    labLinkConnection.connect();
    labLinkConnection.registerMessageHandler(subscription, callback);

    TestMsgCallback callback2 = spy(new TestMsgCallback());
    subscription = MsgSubscription
        .getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
        .addSubjectElement("Test").addSubjectElement("Hallo").build();
    labLinkConnection.registerMessageHandler(subscription, callback2);

    labLinkConnection.publishMessage(MsgSubject.getBuilder().addSubjectElement("Test").build(),
        new StatusMessage(StatusMessage.StatusCode.OK));
    Thread.sleep(300);
    verify(callback, times(1)).handleMessage(any(MsgHeader.class), anyList());
    verify(callback2, times(0)).handleMessage(any(MsgHeader.class), anyList());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void receiveMessage_SingleCallbackStatusAndErrorMsgReceived_ErrorAndMsgHandlerCalled_test()
      throws Exception {
    TestMsgCallback callback = spy(new TestMsgCallback());

    MsgSubscription subscription = MsgSubscription
        .getBuilder(MsgSubscription.EMsgSourceChooser.RECEIVE_FROM_ALL)
        .addSubjectElement("Test").addSubjectAllChildren().build();
    labLinkConnection.connect();
    labLinkConnection.registerMessageHandler(subscription, callback);

    List<IPayload> payloads = new ArrayList<IPayload>();
    payloads.add(new ErrorMessage(ErrorMessage.EErrorCode.PROCESSING_ERROR, "TestError"));
    payloads.add(new StatusMessage(StatusMessage.StatusCode.OK));
    payloads.add(new ErrorMessage(ErrorMessage.EErrorCode.PROCESSING_ERROR, "TestError"));
    payloads.add(new StatusMessage(StatusMessage.StatusCode.OK,"Test"));
    labLinkConnection.publishMessage(
        MsgSubject.getBuilder().addSubjectElement("Test").addSubjectElement("Baba")
            .addSubjectElement("Hallo").build(), payloads);
    Thread.sleep(300);

    ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
    verify(callback, times(1)).handleMessage(any(MsgHeader.class), argument.capture());
    assertEquals("IPayload should only contain two elements.", 2, argument.getValue().size());

    ArgumentCaptor<List> errorArgument = ArgumentCaptor.forClass(List.class);
    verify(callback, times(1)).handleError(any(Header.class), errorArgument.capture());
    assertEquals("Errors should only contain two element.", 2, errorArgument.getValue().size());
  }


  class TestRpcRequestCallback implements IRpcRequestCallback {

    @Override
    public void handleError(Header header, List<ErrorMessage> error) throws Exception {
      logger.info("HandleError called " + error);
    }

    @Override
    public List<IPayload> handleRequest(RpcHeader header, List<IPayload> payloads) {
      logger.info("handleMessage called Header: " + header + " (" + payloads + ")");
      return null;
    }
  }

  @Test(expected = NullPointerException.class)
  public void registerRequestHandler_registerNullObject_ThrowException_test() throws Exception {
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    labLinkConnection.registerRequestHandler(subject, null);
  }

  @Test(expected = NullPointerException.class)
  public void registerRequestHandler_registerNullSubject_throwException_test() throws Exception {
    IRpcRequestCallback callback = new TestRpcRequestCallback();
    labLinkConnection.registerRequestHandler(null, callback);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void registerRequestHandler_registerEmptySubject_ThrowException_test() throws Exception {
    IRpcRequestCallback callback = new TestRpcRequestCallback();
    RpcSubject subject = RpcSubject.getBuilder().build();
    labLinkConnection.registerRequestHandler(subject, callback);
  }

  @Test(expected = NullPointerException.class)
  public void registerRequestHandler_registerNullElement_Subject_ThrowException_test()
      throws Exception {
    IRpcRequestCallback callback = new TestRpcRequestCallback();
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement(null).build();
    labLinkConnection.registerRequestHandler(subject, callback);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void registerRequestHandler_registerEmptyElementSubject_ThrowException_test()
      throws Exception {
    IRpcRequestCallback callback = new TestRpcRequestCallback();
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("").build();
    labLinkConnection.registerRequestHandler(subject, callback);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void registerRequestHandler_registerSameCallbackTwice_ThrowException_test()
      throws Exception {
    IRpcRequestCallback callback = new TestRpcRequestCallback();
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    labLinkConnection.registerRequestHandler(subject, callback);
    labLinkConnection.registerRequestHandler(subject, callback);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void registerRequestHandler_registerTwoCallbackSameSubject_ThrowException_test()
      throws Exception {
    IRpcRequestCallback callback = new TestRpcRequestCallback();
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    labLinkConnection.registerRequestHandler(subject, callback);
    IRpcRequestCallback callback2 = new TestRpcRequestCallback();
    labLinkConnection.registerRequestHandler(subject, callback2);
  }

  @Test
  public void unregisterRequestHandler_unregisterExistingOne_CorrectWork_test() throws Exception {
    IRpcRequestCallback callback = new TestRpcRequestCallback();
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    labLinkConnection.registerRequestHandler(subject, callback);

    labLinkConnection.unregisterRequestHandler(subject, callback);
  }

  @Test
  public void unregisterRequestHandler_unregisterNonAvailableSubject_ThrowException_test()
      throws Exception {
    IRpcRequestCallback callback = new TestRpcRequestCallback();
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    labLinkConnection.registerRequestHandler(subject, callback);
    RpcSubject subject2 = RpcSubject.getBuilder().addSubjectElement("Hallo").build();
    labLinkConnection.unregisterRequestHandler(subject2, callback);
  }

  @Test
  public void unregisterRequestHandler_unregisterNonAvailableCallback_DoNothing_test()
      throws Exception {
    IRpcRequestCallback callback = new TestRpcRequestCallback();
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    labLinkConnection.registerRequestHandler(subject, callback);
    IRpcRequestCallback callback2 = new TestRpcRequestCallback();
    labLinkConnection.unregisterRequestHandler(subject, callback2);
  }

  @Test(expected = NullPointerException.class)
  public void unregisterRequestHandler_NullSubject_ThrowException_test() throws Exception {
    IRpcRequestCallback callback = new TestRpcRequestCallback();
    labLinkConnection.unregisterRequestHandler(null, callback);
  }

  @Test
  public void unregisterRequestHandler_NullCallback_DoNothing_test() throws Exception {
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    labLinkConnection.unregisterRequestHandler(subject, null);
  }


  class TestRpcReplyCallback implements IRpcReplyCallback {

    @Override
    public void handleError(Header header, List<ErrorMessage> error) throws Exception {
      logger.info("HandleError called " + error);
    }

    @Override
    public void handleReply(RpcHeader header, List<IPayload> payloads) {
      logger.info("handleReply called Header: " + header + " (" + payloads + ")");
    }
  }

  @Test(expected = NullPointerException.class)
  public void registerReplyHandler_NullSubject_ThrowException_test() throws Exception {
    IRpcReplyCallback callback = new TestRpcReplyCallback();
    labLinkConnection.registerReplyHandler(null, callback);
  }

  @Test(expected = NullPointerException.class)
  public void registerReplyHandler_NullCallback_ThrowException_test() throws Exception {
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    labLinkConnection.registerReplyHandler(subject, null);
  }

  @Test
  public void registerReplyHandler_SameCallbackDifferentTopic_ReturnSecondRequester_test()
      throws Exception {
    IRpcReplyCallback callback = new TestRpcReplyCallback();
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    IRpcRequester requester1 = labLinkConnection.registerReplyHandler(subject, callback);
    RpcSubject subject2 = RpcSubject.getBuilder().addSubjectElement("Hallo").build();
    IRpcRequester requester2 = labLinkConnection.registerReplyHandler(subject2, callback);

    assertNotEquals("There should be two different requester.", requester1, requester2);
  }

  @Test
  public void registerReplyHandler_DifferentCallbackSameTopic_ReturnSecondRequester_test()
      throws Exception {
    IRpcReplyCallback callback = new TestRpcReplyCallback();
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    IRpcRequester requester1 = labLinkConnection.registerReplyHandler(subject, callback);
    IRpcRequester requester2 = labLinkConnection.registerReplyHandler(subject, callback);

    assertNotEquals("There should be two different requester.", requester1, requester2);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void rpcSendRequest_NotConnected_ShouldThrow_test() throws Exception {
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    IRpcRequestCallback requestCallback = new TestRpcRequestCallback();
    labLinkConnection.registerRequestHandler(subject, requestCallback);

    IRpcReplyCallback callback = mock(IRpcReplyCallback.class);
    IRpcRequester requester = labLinkConnection.registerReplyHandler(subject, callback);

    RpcDestination
        dest =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_ALL).build();
    requester.sendRequest(dest, new StatusMessage(StatusMessage.StatusCode.OK));
    Thread.sleep(500);
    verify(callback).handleError(any(Header.class),anyList());
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void rpcSendRequest_EmptyDestination_ShouldThrow_test() throws Exception {
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    IRpcRequestCallback requestCallback = new TestRpcRequestCallback();
    labLinkConnection.registerRequestHandler(subject, requestCallback);

    IRpcReplyCallback callback = new TestRpcReplyCallback();
    IRpcRequester requester = labLinkConnection.registerReplyHandler(subject, callback);

    labLinkConnection.connect();
    RpcDestination dest = RpcDestination
        .getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_GROUP)
        .setGroupId("").build();
    requester.sendRequest(dest, new StatusMessage(StatusMessage.StatusCode.OK));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void rpcSendRequest_NoPayload_ShouldThrow_test() throws Exception {
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    IRpcRequestCallback requestCallback = new TestRpcRequestCallback();
    labLinkConnection.registerRequestHandler(subject, requestCallback);

    IRpcReplyCallback callback = mock(IRpcReplyCallback.class);
    IRpcRequester requester = labLinkConnection.registerReplyHandler(subject, callback);

    labLinkConnection.connect();
    RpcDestination
        dest =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_ALL).build();
    requester.sendRequest(dest, Collections.<IPayload>emptyList());
    Thread.sleep(500);
    verify(callback).handleError(any(Header.class),anyList());
  }

  @Test
  public void rpcSendRequest_SinglePayload_ShouldBeSent_test() throws Exception {
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    IRpcRequestCallback requestCallback = new TestRpcRequestCallback();
    labLinkConnection.registerRequestHandler(subject, requestCallback);

    TestRpcReplyCallbackWithOutput callback = new TestRpcReplyCallbackWithOutput();
    IRpcRequester requester = labLinkConnection.registerReplyHandler(subject, callback);

    labLinkConnection.connect();
    RpcDestination
        dest =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_ALL).build();
    requester.sendRequest(dest, new StatusMessage(StatusMessage.StatusCode.OK));
    Thread.sleep(300);
    assertTrue("An response should be received.", callback.errorResponse || callback.response);
  }

  @Test
  public void rpcSendRequest_NoReply_Timeout_test() throws Exception {
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();

    TestRpcReplyCallbackWithOutput callback = new TestRpcReplyCallbackWithOutput();
    IRpcRequester requester = labLinkConnection.registerReplyHandler(subject, callback);

    labLinkConnection.connect();
    RpcDestination
        dest =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_ALL).build();
    requester.sendRequest(dest, new StatusMessage(StatusMessage.StatusCode.OK));

    Thread.sleep(1500);
    assertTrue("An error should be received thrown by timeout.", callback.errorResponse);
  }

  @Test
  public void rpcSendRequest_NoReplyPayloadNull_ErrorMessage_test() throws Exception {
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    IRpcRequestCallback requestCallback = new IRpcRequestCallback() {
      @Override
      public List<IPayload> handleRequest(RpcHeader header, List<IPayload> payloads) {
        return null;
      }

      @Override
      public void handleError(Header header, List<ErrorMessage> error) throws Exception {

      }
    };
    labLinkConnection.registerRequestHandler(subject, requestCallback);

    TestRpcReplyCallbackWithOutput callback = new TestRpcReplyCallbackWithOutput();
    IRpcRequester requester = labLinkConnection.registerReplyHandler(subject, callback);

    labLinkConnection.connect();
    RpcDestination
        dest =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_ALL).build();
    requester.sendRequest(dest, new StatusMessage(StatusMessage.StatusCode.OK));
    Thread.sleep(300);
    assertTrue("An Response should be received", callback.response);
  }


  class TestRpcReplyCallbackWithOutput implements IRpcReplyCallback {

    boolean response = false;
    boolean errorResponse = false;

    @Override
    public void handleReply(RpcHeader header, List<IPayload> payloads) {
      response = true;
      logger.info("handleReply called Header: " + header + " (" + payloads + ")");
    }

    @Override
    public void handleError(Header header, List<ErrorMessage> error) throws Exception {
      errorResponse = true;
      logger.info("HandleError called " + error);
    }
  }

  @Test
  public void rpcSendRequest_NoReplyPayloadEmpty_StatusMessageIsReturned_test() throws Exception {
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    IRpcRequestCallback requestCallback = new IRpcRequestCallback() {
      @Override
      public List<IPayload> handleRequest(RpcHeader header, List<IPayload> payloads) {
        logger.debug("Request received", payloads);
        return Collections.emptyList();
      }

      @Override
      public void handleError(Header header, List<ErrorMessage> error) throws Exception {

      }
    };
    labLinkConnection.registerRequestHandler(subject, requestCallback);

    TestRpcReplyCallbackWithOutput callback = new TestRpcReplyCallbackWithOutput();
    IRpcRequester requester = labLinkConnection.registerReplyHandler(subject, callback);

    labLinkConnection.connect();
    RpcDestination
        dest =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_ALL).build();
    requester.sendRequest(dest, new StatusMessage(StatusMessage.StatusCode.OK));

    Thread.sleep(300);
    assertTrue("It should be no response received", callback.response);
    assertFalse("There should be an error response received", callback.errorResponse);
  }

  @Test
  public void rpcSendRequest_SendPacket_CorrectReply_test() throws Exception {
    RpcSubject subject = RpcSubject.getBuilder().addSubjectElement("Test").build();
    IRpcRequestCallback requestCallback = new IRpcRequestCallback() {
      @Override
      public List<IPayload> handleRequest(RpcHeader header, List<IPayload> payloads) {
        logger.debug("Request received", payloads);
        return Collections.singletonList((IPayload) new StatusMessage(StatusMessage.StatusCode.OK));
      }

      @Override
      public void handleError(Header header, List<ErrorMessage> error) throws Exception {

      }
    };
    labLinkConnection.registerRequestHandler(subject, requestCallback);

    TestRpcReplyCallbackWithOutput callback = new TestRpcReplyCallbackWithOutput();
    IRpcRequester requester = labLinkConnection.registerReplyHandler(subject, callback);

    labLinkConnection.connect();
    RpcDestination
        dest =
        RpcDestination.getBuilder(RpcDestination.ERpcDestinationChooser.SEND_TO_ALL).build();
    requester.sendRequest(dest, new StatusMessage(StatusMessage.StatusCode.OK));

    Thread.sleep(300);
    assertTrue("It should be no response received", callback.response);
    assertFalse("There should be an error response received", callback.errorResponse);
  }

}