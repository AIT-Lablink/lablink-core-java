//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

/**
 * UnitTests for client Identifier.
 */
public class ClientIdentifierTest {

  private ClientIdentifier classUnderTest;

  @Before
  public void setUp() throws Exception {
    classUnderTest =
        new ClientIdentifier(Collections.singletonList("at.ac.ait"), 
            "lablink", "group1", "client1");
  }

  @Test
  public void getPrefix_test() throws Exception {
    String expected = classUnderTest.getPrefix().get(0);
    assertEquals("at.ac.ait", expected);
  }

  @Test
  public void getAppId_test() throws Exception {
    assertEquals("lablink", classUnderTest.getAppId());
  }

  @Test
  public void getGroupId_test() throws Exception {
    assertEquals("group1", classUnderTest.getGroupId());
  }

  @Test
  public void getClientId_test() throws Exception {
    assertEquals("client1", classUnderTest.getClientId());
  }
}