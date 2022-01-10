//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.services;

import static org.junit.Assert.fail;

import at.ac.ait.lablink.core.connection.ILlConnection;
import at.ac.ait.lablink.core.connection.impl.LlConnectionFactory;
import at.ac.ait.lablink.core.connection.impl.PortUtils;

import io.moquette.server.Server;
import io.moquette.server.config.MemoryConfig;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import java.net.BindException;
import java.util.Properties;

/**
 * Integration tests for services.
 */
public abstract class ServiceIT {

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

    labLinkConnection = LlConnectionFactory
        .getDefaultConnectionController("at.ac.ait", "IntegrationTest", 
            "group1", "Client", testConfiguration);
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

}
