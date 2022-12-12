//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;
import at.ac.ait.lablink.core.connection.encoding.encodabletestsamples.EncodableTestSample;
import at.ac.ait.lablink.core.connection.encoding.encodabletestsamples.EncodableTestSample2;
import at.ac.ait.lablink.core.connection.encoding.encodabletestsamples.EncoderTestEncodable;
import at.ac.ait.lablink.core.connection.ex.LlCoreDecoderRuntimeException;
import at.ac.ait.lablink.core.service.types.Complex;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit Tests for JSON decoder.
 */
public class JsonDecoderTest {

  JsonDecoder decoder;

  @Before
  public void setUp() throws Exception {
    decoder = new JsonDecoder();

    EncodableFactoryManagerImpl factoryManager = new EncodableFactoryManagerImpl();
    factoryManager.registerEncodableFactory("test-sample-1", new IEncodableFactory() {
      @Override
      public IEncodable createEncodableObject() {
        return new EncodableTestSample();
      }
    });
    factoryManager.registerEncodableFactory("test-sample-2", new IEncodableFactory() {
      @Override
      public IEncodable createEncodableObject() {
        return new EncodableTestSample2();
      }
    });
    factoryManager.registerEncodableFactory("test-encodable", new IEncodableFactory() {
      @Override
      public IEncodable createEncodableObject() {
        return new EncoderTestEncodable("Hallo", 29634);
      }
    });

    decoder.setEncodableFactoryManager(factoryManager);
  }


  @Test(expected = NullPointerException.class)
  public void decoder_getNonAvailableKey() {
    String
        jsonString =
        "{\"TestString\":\"Hallo\",\"TestFloat\":12.234,\"TestDouble\":-32423.324,"
            + "\"TestBoolean\":true,\"TestInt\":15,\"TestLong\":22,\""
            + "TestBlob\":\"VGVzdFN0cmluZw==\",\"TestComplex_re\":3.1415,"
            + "\"TestComplex_im\":2.7183,\"TestString2\":\"Testing\""
            + ",\"TestDouble2\":876.234,\"TestFloat2\":-1346.8442,\"TestLong2\":22,"
            + "\"TestBoolean2\":false,\"TestInt2\":0,\"TestBlob2\":\"U2Vjb25kVGVzdA==\","
            + "\"TestComplex2_re\":7.234,\"TestComplex2_im\":-4.098}";

    decoder.initDecoder(jsonString.getBytes());

    decoder.getString("NonAvailableKey");

  }

  @Test
  public void decoder_getSimpleValues() {
    String
        jsonString =
        "{\"TestString\":\"Hallo\",\"TestFloat\":12.234,\"TestDouble\":-32423.324,"
            + "\"TestBoolean\":true,\"TestInt\":15,\"TestLong\":22,\""
            + "TestBlob\":\"VGVzdFN0cmluZw==\",\"TestComplex_re\":3.1415,"
            + "\"TestComplex_im\":2.7183,\"TestString2\":\"Testing\""
            + ",\"TestDouble2\":876.234,\"TestFloat2\":-1346.8442,\"TestLong2\":22,"
            + "\"TestBoolean2\":false,\"TestInt2\":0,\"TestBlob2\":\"U2Vjb25kVGVzdA==\","
            + "\"TestComplex2_re\":7.234,\"TestComplex2_im\":-4.098}";

    decoder.initDecoder(jsonString.getBytes());

    assertEquals("Testing", decoder.getString("TestString2"));
    assertEquals(876.234, decoder.getDouble("TestDouble2"), 0.1);
    assertEquals(-1346.8442f, decoder.getFloat("TestFloat2"), 0.1);
    assertEquals(22L, decoder.getLong("TestLong2"));
    assertEquals(false, decoder.getBoolean("TestBoolean2"));
    assertEquals(0, decoder.getInt("TestInt2"));
    assertEquals("SecondTest", new String(decoder.getBlob("TestBlob2")));
    assertEquals(true, (new Complex(7.234,-4.098)).equals(decoder.getComplex("TestComplex2")));
  }

  @Test
  public void decoder_getSimpleValues_TestNullValues_test() {
    String
        jsonString =
        "{\"TestString\":null,\"TestFloat\":null,\"TestDouble\":null,"
            + "\"TestBoolean\":null,\"TestInt\":null,\"TestLong\":null,\""
            + "TestBlob\":null,\"TestComplex_re\":null,\"TestComplex_im\":null}";

    decoder.initDecoder(jsonString.getBytes());

    assertEquals("", decoder.getString("TestString"));
    assertEquals(0.0, decoder.getDouble("TestDouble"), 0.1);
    assertEquals(0.0f, decoder.getFloat("TestFloat"), 0.1);
    assertEquals((long) 0, decoder.getLong("TestLong"));
    assertEquals(false, decoder.getBoolean("TestBoolean"));
    assertEquals(0, decoder.getInt("TestInt"));
    assertEquals("", new String(decoder.getBlob("TestBlob")));
    assertEquals(true, (new Complex(0.,0.)).equals(decoder.getComplex("TestComplex")));
  }

  @Test
  public void decoder_getEncodable() {

    String
        json =
        "{\"Encodable1\":{\"$type\":\"test-encodable\",\"testInt\":12,\"testString\":\"Hallo\","
            + "\"Inner\":{\"$type\":\"test-sample-2\",\"boolVal\":true,\"StringList\":[\"Hallo1"
            + "\",\"Hallo2\",\"Hallo3\"]," + "\"halloString\":\"TestSTRING\",\"long\":125}},"
            + "\"Encodable2\":{\"$type\":\"test-encodable\",\"testInt\":56,"
            + "\"testString\":\"Test2\",\"Inner\":{\"$type\":\"test-sample-2\",\"boolVal\":true,"
            + "\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125}}}";

    decoder.initDecoder(json.getBytes());

    assertEquals("EncoderTestEncodable{testInt=12, testString='Hallo', "
        + "innerClass=EncodableTestSample2{testString='TestSTRING', "
        + "testStringList='[Hallo1, Hallo2, Hallo3]', testBoolean=true, "
        + "testLong=125}}", decoder.getEncodable("Encodable1").toString());

    assertEquals("EncoderTestEncodable{testInt=56, testString='Test2', "
        + "innerClass=EncodableTestSample2{testString='TestSTRING', "
        + "testStringList='[Hallo1, Hallo2, Hallo3]', testBoolean=true, "
        + "testLong=125}}", decoder.getEncodable("Encodable2").toString());
  }

  @Test
  public void decoder_getListOfEncodables() {

    String
        json =
        "{\"TestList\":[{\"$type\":\"test-encodable\",\"testInt\":12,\"testString\":\"Hallo\","
            + "\"Inner\":{\"$type\":\"test-sample-2\",\"boolVal\":true,\"StringList\":[\"Hallo1"
            + "\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125}},{\"$type\":\"test-encodable\","
            + "\"testInt\":12,\"testString\":\"Hallo4\",\"Inner\":{\"$type\":\"test-sample-2\","
            + "\"boolVal\":true,\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125}},"
            + "{\"$type\":\"test-encodable\",\"testInt\":56,\"testString\":\"Test2\","
            + "\"Inner\":{\"$type\":\"test-sample-2\",\"boolVal\":true,\"StringList\":[\"Hallo1"
            + "\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125}},{\"$type\":\"test-encodable\","
            + "\"testInt\":56,\"testString\":\"Test3\",\"Inner\":{\"$type\":\"test-sample-2\","
            + "\"boolVal\":true,\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125}}]}";

    decoder.initDecoder(json.getBytes());

    List<EncoderTestEncodable> testList = new ArrayList<EncoderTestEncodable>();

    List<? extends IEncodable> encodables = decoder.getEncodables("TestList");
    for (IEncodable encodable : encodables) {
      if (encodable instanceof EncoderTestEncodable) {
        testList.add((EncoderTestEncodable) encodable);
      } else {
        throw new LlCoreDecoderRuntimeException("Decoded element (" + encodable.getClass()
            + ") isn't a EncoderTestEncodable element.");
      }
    }

    assertEquals("Length of read Encodables doesn't match.", 4, testList.size());
    assertEquals("EncoderTestEncodable{testInt=12, testString='Hallo', "
        + "innerClass=EncodableTestSample2{testString='TestSTRING', testStringList='[Hallo1, "
        + "Hallo2, Hallo3]', testBoolean=true, "
        + "testLong=125}}", testList.get(0).toString());
    assertEquals("EncoderTestEncodable{testInt=12, testString='Hallo4', "
        + "innerClass=EncodableTestSample2{testString='TestSTRING', "
        + "testStringList='[Hallo1, Hallo2, Hallo3]', testBoolean=true, "
        + "testLong=125}}", testList.get(1).toString());
    assertEquals("EncoderTestEncodable{testInt=56, testString='Test2', "
        + "innerClass=EncodableTestSample2{testString='TestSTRING', "
        + "testStringList='[Hallo1, Hallo2, Hallo3]', testBoolean=true, "
        + "testLong=125}}", testList.get(2).toString());
    assertEquals("EncoderTestEncodable{testInt=56, testString='Test3', "
        + "innerClass=EncodableTestSample2{testString='TestSTRING', "
        + "testStringList='[Hallo1, Hallo2, Hallo3]', testBoolean=true, "
        + "testLong=125}}", testList.get(3).toString());
  }


  @Test
  public void encoder_setConfig() {

    assertEquals("Default max stack size for encoder is not 200", 200, decoder.getMaxStackSize());

    Configuration config = new BaseConfiguration();
    config.addProperty("encoding.maxStackSize", 10);
    decoder = new JsonDecoder(config);

    assertEquals("Stack size can't be set", 10, decoder.getMaxStackSize());

    config = new BaseConfiguration();
    decoder = new JsonDecoder(config);

    assertEquals("Stack size doesn't uses default values", 200, decoder.getMaxStackSize());

  }

  @Test
  public void decoder_decodeElement() {

    String
        jsonString =
        "{\"$type\":\"test-sample-1\",\"Inner\":{\"$type\":\"test-sample-2\",\"boolVal\":true,"
            + "\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125},"
            + "\"testList\":[{\"$type\":\"test-sample-2\",\"boolVal\":true,\"StringList"
            + "\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125},{\"$type\":\"test-sample-2\","
            + "\"boolVal\":true,\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING2\",\"long\":128}]}";

    IEncodable encodable = decoder.processDecoding(jsonString.getBytes());

    assertEquals("EncodableTestSample{innerClass=EncodableTestSample2{testString='TestSTRING', "
        + "testStringList='[Hallo1, Hallo2, Hallo3]', " + "testBoolean=true, testLong=125}, "
        + "testList=[EncodableTestSample2{testString='TestSTRING', "
        + "testStringList='[Hallo1, Hallo2, Hallo3]', testBoolean=true, "
        + "testLong=125}, EncodableTestSample2{testString='TestSTRING2', "
        + "testStringList='[Hallo1, Hallo2, Hallo3]', testBoolean=true, "
        + "testLong=128}]}", encodable.toString());
  }

  @Test
  public void processDecoding_lazyDecodingOneElement_callOneTime_test() {

    String
        jsonString =
        "{\"$type\":\"test-sample-1\",\"Inner\":{\"$type\":\"test-sample-2\",\"boolVal\":true,"
            + "\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125},"
            + "\"testList\":[{\"$type\":\"test-sample-2\",\"boolVal\":true,\"StringList"
            + "\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125},{\"$type\":\"test-sample-2\","
            + "\"boolVal\":true,\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING2\",\"long\":128}]}";

    JsonDecoder spyDecoder = spy(decoder);

    spyDecoder.processDecoding(jsonString.getBytes());

    verify(spyDecoder, times(1)).decodeElement(any(byte[].class));
  }

  @Test
  public void processDecoding_lazyDecodingOneElementTwice_callOneTime_test() {

    String
        jsonString =
        "{\"$type\":\"test-sample-1\",\"Inner\":{\"$type\":\"test-sample-2\",\"boolVal\":true,"
            + "\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125},"
            + "\"testList\":[{\"$type\":\"test-sample-2\",\"boolVal\":true,\"StringList"
            + "\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125},{\"$type\":\"test-sample-2\","
            + "\"boolVal\":true,\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING2\",\"long\":128}]}";

    JsonDecoder spyDecoder = spy(decoder);

    spyDecoder.processDecoding(jsonString.getBytes());
    spyDecoder.processDecoding(jsonString.getBytes());

    verify(spyDecoder, times(1)).decodeElement(any(byte[].class));
  }

  @Test
  public void processDecoding_lazyDecodingTwoElementTwice_CallTwoTimes_test() {

    String
        jsonString =
        "{\"$type\":\"test-sample-1\",\"Inner\":{\"$type\":\"test-sample-2\",\"boolVal\":true,"
            + "\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125},"
            + "\"testList\":[{\"$type\":\"test-sample-2\",\"boolVal\":true,\"StringList"
            + "\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125},{\"$type\":\"test-sample-2\","
            + "\"boolVal\":true,\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING2\",\"long\":128}]}";

    JsonDecoder spyDecoder = spy(decoder);

    spyDecoder.processDecoding(jsonString.getBytes());

    jsonString =
        "{\"$type\":\"test-sample-1\",\"Inner\":{\"$type\":\"test-sample-2\",\"boolVal\":true,"
            + "\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRINGNew\",\"long\":176},"
            + "\"testList\":[{\"$type\":\"test-sample-2\",\"boolVal\":true,\"StringList"
            + "\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125},{\"$type\":\"test-sample-2\","
            + "\"boolVal\":true,\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING2\",\"long\":128}]}";

    spyDecoder.processDecoding(jsonString.getBytes());
    spyDecoder.processDecoding(jsonString.getBytes());

    verify(spyDecoder, times(2)).decodeElement(any(byte[].class));
  }
}