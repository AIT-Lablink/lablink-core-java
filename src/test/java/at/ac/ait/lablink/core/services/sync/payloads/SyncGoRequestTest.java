//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.services.sync.payloads;

import at.ac.ait.lablink.core.connection.encoding.encodables.PayloadBaseTest;
import at.ac.ait.lablink.core.service.sync.payloads.SyncGoRequest;

import org.junit.Before;

/**
 * Unit Tests for BooleanValue.
 */
public class SyncGoRequestTest extends PayloadBaseTest {

  @Before
  public void setUp() throws Exception {
    classUnderTest = new SyncGoRequest();
    expectedName = "SyncGoRequest";
  }

}