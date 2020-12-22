//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.impl;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.apache.commons.configuration.Configuration;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;


/**
 * Unit tests for the LlConnectionFactory class.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = LlConnectionFactory.class)
public class LablinkConnectionFactoryTest {

  @Test
  public void createDefaultMessagingCore_test() throws Exception {
    LlConnectionController messagingMock = mock(LlConnectionController.class);
    whenNew(LlConnectionController.class).withAnyArguments().thenReturn(messagingMock);

    LlConnectionFactory
        .getDefaultConnectionController("at.ac.ait.lablink", "appid1", "group1", "client1");

    verifyNew(LlConnectionController.class)
        .withArguments(Collections.singletonList("at.ac.ait.lablink"), "appid1", "group1",
            "client1", null);
  }

  @Test
  public void createDefaultMessagingCoreUsingFileName_test() throws Exception {
    String tmpConfigFile = "LLconfig.properties";
    LlConnectionController messagingMock = mock(LlConnectionController.class);
    whenNew(LlConnectionController.class).withAnyArguments().thenReturn(messagingMock);

    LlConnectionFactory.getDefaultConnectionController("at.ac.ait.lablink", "appid1", 
        "group1", "client1",tmpConfigFile);

    verifyNew(LlConnectionController.class).withArguments(
        Collections.singletonList("at.ac.ait.lablink"), 
        "appid1", "group1", "client1", tmpConfigFile);
  }

  @Test
  public void createMessagingCoreMqtt_test() throws Exception {

    LlConnectionController messagingMock = mock(LlConnectionController.class);
    whenNew(LlConnectionController.class).withAnyArguments().thenReturn(messagingMock);

    LlConnectionFactory.getConnectionController("MQTT", "at.ac.ait.lablink", "appid1", 
        "group1", "client1", (Configuration) null);

    verifyNew(LlConnectionController.class).withArguments(
        Collections.singletonList("at.ac.ait.lablink"), 
        "appid1", "group1", "client1", null);
  }
}