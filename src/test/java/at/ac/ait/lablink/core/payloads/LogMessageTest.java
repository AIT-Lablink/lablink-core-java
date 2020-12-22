//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.payloads;

import at.ac.ait.lablink.core.connection.encoding.encodables.PayloadBaseTest;

import org.junit.Before;

/**
 * Unit Tests for Logger Message payload.
 */
public class LogMessageTest extends PayloadBaseTest {

  @Before
  public void setUp() throws Exception {
    classUnderTest = new LogMessage();
    expectedName = "logMsg";
  }
}