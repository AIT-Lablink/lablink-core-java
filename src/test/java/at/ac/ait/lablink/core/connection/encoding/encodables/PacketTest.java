//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.encodables;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit Tests for Packet.
 */
@SuppressWarnings("ALL")
public class PacketTest {

  protected Header headerUnderTest;
  protected List<IPayload> payloadsUnderTest;
  protected Packet packetUnderTest;
  protected final String expectedName = "packet";

  @Before
  public void setUp() throws Exception {
    headerUnderTest = mock(Header.class);
    payloadsUnderTest = new ArrayList<>();
    packetUnderTest = new Packet(headerUnderTest, payloadsUnderTest);
  }

  @Test
  public void checkAvailableStaticMethod_GetClassType() throws Exception {
    Class<? extends IEncodable> encodableClass = packetUnderTest.getClass();

    Method method = encodableClass.getMethod("getClassType");
    String classType = (String) method.invoke(null);

    assertEquals(expectedName, classType);
  }

  @Test
  public void checkAvailableStaticMethod_getEncodableFactory() throws Exception {
    Class<? extends IEncodable> encodableClass = packetUnderTest.getClass();

    Method method = encodableClass.getMethod("getEncodableFactory");
    IEncodableFactory factory = (IEncodableFactory) method.invoke(null);

    assertEquals(expectedName,
        factory.createEncodableObject().getType());
  }

  @Test
  public void checkEncodableGetType() {

    assertEquals("GetType method's return must match with static getClassType method's return.",
        Packet.getClassType(), packetUnderTest.getType());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void checkEncoding() {
    IEncoder encoderMock = mock(IEncoder.class);

    InOrder inOrder = inOrder(encoderMock);

    packetUnderTest.encode(encoderMock);

    inOrder.verify(encoderMock).putEncodable(eq("header"),any(Header.class));
    inOrder.verify(encoderMock).putEncodableList(eq("payload"),anyList());
  }

  @Test
  public void checkDecodingWithMockedObjects() {
    IDecoder decoderMock = mock(IDecoder.class);
    List<IEncodable> payloadsMock = new ArrayList<IEncodable>();
    payloadsMock.add(new TestPayload());
    payloadsMock.add(new TestPayload());

    doReturn(payloadsMock).when(decoderMock).getEncodables(eq("payload"));
    when(decoderMock.getEncodable(eq("header"))).thenReturn(new TestHeader());

    InOrder inOrder = inOrder(decoderMock);

    packetUnderTest.decode(decoderMock);

    inOrder.verify(decoderMock).getEncodable(eq("header"));
    inOrder.verify(decoderMock).getEncodables(eq("payload"));
  }
}

class TestHeader extends Header {

  public static String getClassType() {
    return "test-header";
  }

  public static IEncodableFactory getEncodableFactory() {
    return new IEncodableFactory() {
      @Override
      public IEncodable createEncodableObject() {
        return new TestHeader();
      }
    };
  }

  @Override
  public void encode(IEncoder encoder) {
  }

  @Override
  public void decode(IDecoder decoder) {
  }

  @Override
  public String getType() {
    return TestHeader.getClassType();
  }

  @Override
  public void decodingCompleted() {
  }

  @Override
  public void validate() {
  }
}

class TestPayload extends PayloadBase {

  public static String getClassType() {
    return "test-payloads";
  }

  public static IEncodableFactory getEncodableFactory() {
    return new IEncodableFactory() {
      @Override
      public IEncodable createEncodableObject() {
        return new TestPayload();
      }
    };
  }

  @Override
  public void validate() {
  }

  @Override
  public void encode(IEncoder encoder) {
  }

  @Override
  public void decode(IDecoder decoder) {
  }

  @Override
  public String getType() {
    return TestPayload.getClassType();
  }

  @Override
  public void decodingCompleted() {
  }
}