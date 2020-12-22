//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.messaging;

import at.ac.ait.lablink.core.connection.encoding.encodables.EncodableBaseTest;

import org.junit.Before;

/**
 * Unit test for class MsgHeader.
 */
public class MsgHeaderTest extends EncodableBaseTest {

  @Before
  public void setUp() throws Exception {
    classUnderTest = new MsgHeader();
    expectedName = "msg-header";
  }
}