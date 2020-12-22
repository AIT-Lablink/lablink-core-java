//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.impl;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Unit tests for the EncoderFactory class.
 */

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest(value = EncoderFactory.class)
public class EncoderFactoryTest {

  @Test
  public void createDefaultEncoder_test() throws Exception {
    JsonEncoder encoderMock = mock(JsonEncoder.class);
    whenNew(JsonEncoder.class).withAnyArguments().thenReturn(encoderMock);

    EncoderFactory encoderFactory = new EncoderFactory(EncoderFactory.EEncoderType.JSON, null);

    encoderFactory.getDefaultEncoderObject();

    verifyNew(JsonEncoder.class).withArguments(null);
  }

  @Test
  public void createJsonEncoder_test() throws Exception {
    JsonEncoder encoderMock = mock(JsonEncoder.class);
    whenNew(JsonEncoder.class).withAnyArguments().thenReturn(encoderMock);

    EncoderFactory encoderFactory = new EncoderFactory(EncoderFactory.EEncoderType.JSON, null);

    encoderFactory.getEncoderObject(EncoderFactory.EEncoderType.JSON);

    verifyNew(JsonEncoder.class).withArguments(null);
  }

  //TODO test pool implementation
}