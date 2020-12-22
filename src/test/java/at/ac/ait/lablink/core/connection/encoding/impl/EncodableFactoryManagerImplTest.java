//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.impl;

import static org.junit.Assert.assertEquals;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit Tests for EncodableFactoryManager implementation.
 */
public class EncodableFactoryManagerImplTest {

  EncodableFactoryManagerImpl encodableFactoryManager;

  @Before
  public void setUp() {

    encodableFactoryManager = new EncodableFactoryManagerImpl();


  }

  @Test
  public void registerClass_CorrectWay() {

    encodableFactoryManager.registerEncodableFactory(EncodableWithStaticMethods.class);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void registerClass_MissingGetClassType_noStaticMethod() {
    encodableFactoryManager.registerEncodableFactory(EncodableWithoutStaticMethods.class);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void registerClass_MissingGetClassType_onlyFactoryMethod() {
    encodableFactoryManager.registerEncodableFactory(EncodableOnlyWithStaticGetFactory.class);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void registerClass_MissingFactoryMethod_noStaticMethod() {
    encodableFactoryManager.registerEncodableFactory(EncodableWithoutStaticMethods.class);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void registerClass_differentClassesWithSameNames() {
    encodableFactoryManager.registerEncodableFactory("type1", EncodableWithStaticMethods.class);
    encodableFactoryManager
        .registerEncodableFactory("type1", EncodableOnlyWithStaticGetFactory.class);
  }

  @Test()
  public void registerClass_SameClassTwice() {
    encodableFactoryManager.registerEncodableFactory(EncodableWithStaticMethods.class);
    encodableFactoryManager.registerEncodableFactory(EncodableWithStaticMethods.class);

    assertEquals("Class should only be registered once.", 1,
        encodableFactoryManager.getEncodableStore().size());
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void registerClass_MissingFactoryMethod_onlyGetClassMethod() {
    encodableFactoryManager.registerEncodableFactory(EncodableOnlyWithStaticClassType.class);
  }

  @Test()
  public void registerClass_SameClassWithDifferentNames() {
    encodableFactoryManager.registerEncodableFactory(EncodableWithStaticMethods.class);
    encodableFactoryManager.registerEncodableFactory("type2", EncodableWithStaticMethods.class);

    assertEquals("Class should be registered twice.", 2,
        encodableFactoryManager.getEncodableStore().size());
  }


  @Test
  public void unregisterClass_NotAvailable() {
    encodableFactoryManager.unregisterEncodableFactory("type1");
    encodableFactoryManager.unregisterEncodableFactory(EncodableWithStaticMethods.class);
  }

  @Test
  public void unregisterClass_AvailableClass() {
    encodableFactoryManager.registerEncodableFactory(EncodableWithStaticMethods.class);
    encodableFactoryManager.unregisterEncodableFactory(EncodableWithStaticMethods.class);
  }

  @Test
  public void unregisterClass_MoreClassesAvailable() {
    encodableFactoryManager.registerEncodableFactory(EncodableWithStaticMethods.class);
    encodableFactoryManager.registerEncodableFactory("type2", EncodableWithStaticMethods.class);
    encodableFactoryManager.registerEncodableFactory("type3", EncodableWithStaticMethods.class);

    encodableFactoryManager.unregisterEncodableFactory(EncodableWithStaticMethods.class);

    assertEquals("Two factories should be registered.", 2,
        encodableFactoryManager.getEncodableStore().size());
  }

}


class EncodableWithoutStaticMethods implements IEncodable {


  @Override
  public void encode(IEncoder encoder) {

  }

  @Override
  public void decode(IDecoder decoder) {

  }

  @Override
  public String getType() {
    return "EncodableWithoutStaticMethods";
  }

  @Override
  public void decodingCompleted() {

  }

  @Override
  public void validate() {

  }

}

class EncodableWithStaticMethods implements IEncodable {

  public static String getClassType() {
    return "EncodableWithStaticMethods";
  }

  public static IEncodableFactory getEncodableFactory() {
    return new IEncodableFactory() {
      @Override
      public IEncodable createEncodableObject() {
        return new EncodableWithStaticMethods();
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
    return EncodableWithStaticMethods.getClassType();
  }

  @Override
  public void decodingCompleted() {

  }

  @Override
  public void validate() {

  }

}

class EncodableOnlyWithStaticClassType implements IEncodable {

  public static String getClassType() {
    return "EncodableOnlyWithStaticClassType";
  }


  @Override
  public void encode(IEncoder encoder) {

  }

  @Override
  public void decode(IDecoder decoder) {

  }

  @Override
  public String getType() {
    return EncodableOnlyWithStaticClassType.getClassType();
  }

  @Override
  public void decodingCompleted() {

  }

  @Override
  public void validate() {

  }

}


class EncodableOnlyWithStaticGetFactory implements IEncodable {


  public static IEncodableFactory getEncodableFactory() {
    return new IEncodableFactory() {
      @Override
      public IEncodable createEncodableObject() {
        return new EncodableOnlyWithStaticGetFactory();
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
    return "EncodableOnlyWithStaticGetFactory";
  }

  @Override
  public void decodingCompleted() {

  }

  @Override
  public void validate() {

  }

}