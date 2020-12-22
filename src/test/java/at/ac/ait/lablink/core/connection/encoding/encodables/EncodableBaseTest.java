//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.encodables;


import static org.junit.Assert.assertEquals;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for encodables.
 */
public abstract class EncodableBaseTest {

  protected IEncodable classUnderTest;
  protected String expectedName = "Header";

  @Test
  public void checkAvailableStaticMethod_GetClassType() throws Exception {
    Class<? extends IEncodable> encodableClass = classUnderTest.getClass();

    Method method = encodableClass.getMethod("getClassType");
    String classType = (String) method.invoke(null);

    assertEquals(expectedName, classType);
  }

  @Test
  public void checkAvailableStaticMethod_getEncodableFactory() throws Exception {
    Class<? extends IEncodable> encodableClass = classUnderTest.getClass();

    Method method = encodableClass.getMethod("getEncodableFactory");
    IEncodableFactory factory = (IEncodableFactory) method.invoke(null);

    assertEquals(expectedName, factory.createEncodableObject().getType());
  }

  @Test
  public void encodeDecode_checkOrderOfEncodingDecoding_ShouldBeTheSame_test() throws Exception {
    CodecMock mock = new CodecMock();

    classUnderTest.encode(mock);
    classUnderTest.decode(mock);

    assertEquals("Elements should be in the same order.", mock.encodingList, mock.decodingList);
  }

  private class CodecMock implements IEncoder, IDecoder {

    List<String> encodingList = new ArrayList<String>();
    List<String> decodingList = new ArrayList<String>();

    Map<String,Object> elements = new HashMap<String, Object>();

    @Override
    public void putString(String key, String value) {
      encodingList.add(key);
      elements.put(key,value);
    }

    @Override
    public void putStringList(String key, List<String> values) {
      encodingList.add(key);
      elements.put(key,values);
    }

    @Override
    public void putFloat(String key, float value) {
      encodingList.add(key);
      elements.put(key,value);
    }

    @Override
    public void putDouble(String key, double value) {
      encodingList.add(key);
      elements.put(key,value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
      encodingList.add(key);
      elements.put(key,value);
    }

    @Override
    public void putInt(String key, int value) {
      encodingList.add(key);
      elements.put(key,value);
    }

    @Override
    public void putLong(String key, long value) {
      encodingList.add(key);
      elements.put(key,value);
    }

    @Override
    public void putBlob(String key, byte[] value) {
      encodingList.add(key);
      elements.put(key,value);
    }

    @Override
    public void putEncodable(String key, IEncodable value) {
      encodingList.add(key);
      elements.put(key,value);
    }

    @Override
    public void putEncodableList(String key, List<? extends IEncodable> values) {
      encodingList.add(key);
      elements.put(key,values);
    }

    @Override
    public String getString(String key) {
      decodingList.add(key);
      return (String) elements.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getStrings(String key) {
      decodingList.add(key);
      return (List<String>) elements.get(key);
    }

    @Override
    public float getFloat(String key) {
      decodingList.add(key);
      return 0;
    }

    @Override
    public double getDouble(String key) {
      decodingList.add(key);
      return 0;
    }

    @Override
    public boolean getBoolean(String key) {
      decodingList.add(key);
      return false;
    }

    @Override
    public int getInt(String key) {
      decodingList.add(key);
      return 0;
    }

    @Override
    public long getLong(String key) {
      decodingList.add(key);
      return 0;
    }

    @Override
    public byte[] getBlob(String key) {
      decodingList.add(key);
      return new byte[0];
    }

    @Override
    public IEncodable getEncodable(String key) {
      decodingList.add(key);
      return (IEncodable) elements.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<? extends IEncodable> getEncodables(String key) {
      decodingList.add(key);
      return (List<? extends IEncodable>) elements.get(key);
    }
  }
}
