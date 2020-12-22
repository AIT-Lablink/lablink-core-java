//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import at.ac.ait.lablink.core.connection.ILlConnection;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.Arrays;

/**
 * Unit tests for the ILlConnection module.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest(LlConnectionController.class)
public class LablinkConnectionControllerTest {


  private final String tempConfigFileName = "LLConfig.properties";

  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  @Before
  public void setUp() throws Exception {
    final File tempConfigFile = tempFolder.newFile(tempConfigFileName);
  }


  @Test
  public void construct_AvailableConfigFileGiven_CreateObject_test() throws Exception {

    LlConnectionController messagingMock = mock(LlConnectionController.class);
    whenNew(LlConnectionController.class).withAnyArguments().thenReturn(messagingMock);
    String configFile = new File(tempFolder.getRoot().toString(), tempConfigFileName).toString();

    ILlConnection connection = new LlConnectionController(
        Arrays.asList("at.ac.ait", "lablink"), "appid1", "group1", "client1",
            configFile);

    verifyNew(LlConnectionController.class).withArguments(anyList(), eq("appid1"), eq( "group1"),
        eq( "client1"), any());
  }


  @Test(expected = LlCoreRuntimeException.class)
  public void construct_NonAvailableConfigFileGiven_ThrowException_test() throws Exception {
    String
        configFile =
        new File(tempFolder.getRoot().toString(), "LLFalseConfig.properties").toString();

    ILlConnection connection = new LlConnectionController(
        Arrays.asList("at.ac.ait", "lablink"), "appid1", "group1", "client1",
        configFile);
  }
}