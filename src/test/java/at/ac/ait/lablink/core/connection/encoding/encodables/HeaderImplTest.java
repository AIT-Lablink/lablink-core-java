//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.encodables;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertEquals;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;

/**
 * Unit test for abstract header class implementation.
 */
public class HeaderImplTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void getClassType_WithStaticMethods() throws Exception {

    Class<? extends IEncodable> encodableClass = HeaderImplementationWithStaticMethods.class;

    Method method = encodableClass.getMethod("getClassType");
    String classType = (String) method.invoke(null);

    assertEquals("HeaderImplementationWithStaticMethods", classType);
  }

  @Test
  public void getClassType_WithoutStaticMethods() throws Exception {

    Class<? extends IEncodable> encodableClass = HeaderImplementationWithoutStaticMethods.class;

    thrown.expectCause(isA(IllegalStateException.class));

    Method method = encodableClass.getMethod("getClassType");
    String classType = (String) method.invoke(null);

    assertEquals("HeaderImplementationWithoutStaticMethods", classType);
  }

  @Test
  public void getEncodableFactory_WithStaticMethods() throws Exception {
    Class<? extends IEncodable> encodableClass = HeaderImplementationWithStaticMethods.class;

    Method method = encodableClass.getMethod("getEncodableFactory");
    IEncodableFactory factory = (IEncodableFactory) method.invoke(null);

    assertEquals("HeaderImplementationWithStaticMethods",
        factory.createEncodableObject().getType());
  }

  @Test
  public void getEncodableFactory_WithoutStaticMethods() throws Exception {

    Class<? extends IEncodable> encodableClass = HeaderImplementationWithoutStaticMethods.class;

    thrown.expectCause(isA(IllegalStateException.class));

    Method method = encodableClass.getMethod("getEncodableFactory");
    IEncodableFactory factory = (IEncodableFactory) method.invoke(null);

    assertEquals("HeaderImplementationWithoutStaticMethods",
        factory.createEncodableObject().getType());
  }
}

@SuppressWarnings("EmptyMethod")
class HeaderImplementationWithoutStaticMethods extends Header {

  @Override
  public void encode(IEncoder encoder) {
    super.encode(encoder);
  }

  @Override
  public void decode(IDecoder decoder) {
    super.decode(decoder);
  }

  @Override
  public String getType() {
    return "HeaderImplementationWithoutStaticMethods";
  }

  @Override
  public void decodingCompleted() {
    super.decodingCompleted();
  }

  @Override
  public void validate() {
  }
}

@SuppressWarnings("EmptyMethod")
class HeaderImplementationWithStaticMethods extends Header {

  public static String getClassType() {
    return "HeaderImplementationWithStaticMethods";
  }

  public static IEncodableFactory getEncodableFactory() {
    return new IEncodableFactory() {
      @Override
      public IEncodable createEncodableObject() {
        return new HeaderImplementationWithStaticMethods();
      }
    };
  }

  @Override
  public void encode(IEncoder encoder) {
    super.encode(encoder);
  }

  @Override
  public void decode(IDecoder decoder) {
    super.decode(decoder);
  }

  @Override
  public String getType() {
    return HeaderImplementationWithStaticMethods.getClassType();
  }

  @Override
  public void decodingCompleted() {
    super.decodingCompleted();
  }

  @Override
  public void validate() {
  }
}