//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.services.datapoint.payloads;

import at.ac.ait.lablink.core.connection.encoding.encodables.PayloadBaseTest;
import at.ac.ait.lablink.core.service.datapoint.payloads.DoubleValue;

import org.junit.Before;

/**
 * Unit Tests for DoubleValue.
 */
public class DoubleValueTest extends PayloadBaseTest {

  @Before
  public void setUp() throws Exception {
    classUnderTest = new DoubleValue();
    expectedName = "doubleValue";
  }

}