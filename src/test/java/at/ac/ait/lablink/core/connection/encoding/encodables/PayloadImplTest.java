//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.encodables;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Unit Tests for abstract Payload class.
 */
public class PayloadImplTest {

  @Test
  public void getClassType_WithStaticMethods() throws Exception {

    Class<? extends IEncodable> encodableClass = PayloadImplementationWithStaticMethods.class;

    Method method = encodableClass.getMethod("getClassType");
    String classType = (String) method.invoke(null);

    assertEquals("PayloadImplementationWithStaticMethods", classType);
  }

  @Test
  public void getClassType_WithoutStaticMethods() throws Exception {
    Class<? extends IEncodable> encodableClass = PayloadImplementationWithoutStaticMethods.class;

    Method method = encodableClass.getMethod("getClassType");
    Exception exception =
        assertThrows(InvocationTargetException.class, () -> method.invoke(null));

    assertEquals(IllegalStateException.class, exception.getCause().getClass());
  }

  @Test
  public void getEncodableFactory_WithStaticMethods() throws Exception {
    Class<? extends IEncodable> encodableClass = PayloadImplementationWithStaticMethods.class;

    Method method = encodableClass.getMethod("getEncodableFactory");
    IEncodableFactory factory = (IEncodableFactory) method.invoke(null);

    assertEquals("PayloadImplementationWithStaticMethods",
        factory.createEncodableObject().getType());
  }

  @Test
  public void getEncodableFactory_WithoutStaticMethods() throws Exception {

    Class<? extends IEncodable> encodableClass = PayloadImplementationWithoutStaticMethods.class;

    Method method = encodableClass.getMethod("getEncodableFactory");
    Exception exception = 
        assertThrows(InvocationTargetException.class, () -> method.invoke(null));

    assertEquals(IllegalStateException.class, exception.getCause().getClass());
  }
}


class PayloadImplementationWithoutStaticMethods extends PayloadBase {

  @Override
  public void encode(IEncoder encoder) {
  }

  @Override
  public void decode(IDecoder decoder) {
  }

  @Override
  public String getType() {
    return "PayloadImplementationWithoutStaticMethods";
  }

  @Override
  public void decodingCompleted() {
  }

  @Override
  public void validate() {
  }
}

class PayloadImplementationWithStaticMethods extends PayloadBase {

  public static String getClassType() {
    return "PayloadImplementationWithStaticMethods";
  }

  public static IEncodableFactory getEncodableFactory() {
    return new IEncodableFactory() {
      @Override
      public IEncodable createEncodableObject() {
        return new PayloadImplementationWithStaticMethods();
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
    return PayloadImplementationWithStaticMethods.getClassType();
  }

  @Override
  public void decodingCompleted() {
  }
}
