//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.payloads;

import at.ac.ait.lablink.core.connection.encoding.encodables.PayloadBaseTest;

import org.junit.Before;

/**
 * Unit Tests for StringValue.
 */
public class StringValueTest extends PayloadBaseTest {

  @Before
  public void setUp() throws Exception {
    classUnderTest = new StringValue();
    expectedName = "stringValue";
  }

}