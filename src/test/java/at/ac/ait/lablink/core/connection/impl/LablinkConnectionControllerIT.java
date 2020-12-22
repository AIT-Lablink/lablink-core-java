//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.impl;

import org.junit.Before;

/**
 * Integration tests for the LablinkController using MQTT low level core.
 */
public class LablinkConnectionControllerIT extends LablinkConnectionBaseIT {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    labLinkConnection = LlConnectionFactory
        .getDefaultConnectionController("at.ac.ait", "IntegrationTest", 
            "group1", "Client", testConfiguration);
  }
}
