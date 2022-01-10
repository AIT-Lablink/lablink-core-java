//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.services.datapoint.payloads;

import at.ac.ait.lablink.core.connection.encoding.encodables.PayloadBaseTest;
import at.ac.ait.lablink.core.service.datapoint.payloads.DataPointProperties;

import org.junit.Before;

/**
 * Unit Tests for DataPointProperties.
 */
public class DataPointPropertiesTest extends PayloadBaseTest {

  @Before
  public void setUp() throws Exception {
    classUnderTest = new DataPointProperties();
    expectedName = "datapointProperties";
  }

}