//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.payloads;

import at.ac.ait.lablink.core.connection.encoding.encodables.PayloadBaseTest;

import org.junit.Before;

/**
 * Unit Tests for Error Message payload.
 */
public class StatusMessageTest extends PayloadBaseTest {


  @Before
  public void setUp() throws Exception {
    classUnderTest = new StatusMessage();
    expectedName = "statusMsg";
  }

}