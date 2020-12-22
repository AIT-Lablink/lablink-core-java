//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.impl;

import static org.junit.Assert.assertSame;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Unit tests for the DecoderFactory class.
 */

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest(value = DecoderFactory.class)
public class DecoderFactoryTest {

  @Test
  public void getDefaultDecoder_test() throws Exception {
    JsonDecoder decoderMock = mock(JsonDecoder.class);
    whenNew(JsonDecoder.class).withAnyArguments().thenReturn(decoderMock);

    DecoderFactory decoderFactory = new DecoderFactory(DecoderFactory.EDecoderType.JSON, null);

    decoderFactory.getDefaultDecoderObject();

    verifyNew(JsonDecoder.class).withArguments(null);
  }

  @Test
  public void getJsonDecoder_test() throws Exception {
    JsonDecoder decoderMock = mock(JsonDecoder.class);
    whenNew(JsonDecoder.class).withAnyArguments().thenReturn(decoderMock);

    DecoderFactory decoderFactory = new DecoderFactory(DecoderFactory.EDecoderType.JSON, null);

    decoderFactory.getDecoderObject(DecoderFactory.EDecoderType.JSON);

    verifyNew(JsonDecoder.class).withArguments(null);
  }

  @Test
  public void getDefaultDecoder_getTwoTimes_ShouldBeTheSameInstance() throws Exception {
    JsonDecoder decoderMock = mock(JsonDecoder.class);
    whenNew(JsonDecoder.class).withAnyArguments().thenReturn(decoderMock);

    DecoderFactory decoderFactory = new DecoderFactory(DecoderFactory.EDecoderType.JSON, null);

    IDecoder decoder1 = decoderFactory.getDefaultDecoderObject();
    IDecoder decoder2 = decoderFactory.getDefaultDecoderObject();

    assertSame(
        "The two decoder should be the same instance. This can only guarantee the lazy encoding.",
        decoder1, decoder2);

  }
}