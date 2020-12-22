//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.impl;

import static org.junit.Assert.assertEquals;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.connection.encoding.encodabletestsamples.EncodableTestSample;
import at.ac.ait.lablink.core.connection.encoding.encodabletestsamples.EncoderTestEncodable;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for JSON encoder.
 */
public class JsonEncoderTest {

  JsonEncoder encoder;


  @Before
  public void setUp() throws Exception {
    encoder = new JsonEncoder();

  }

  @Test(expected = LlCoreRuntimeException.class)
  public void encoder_putInvalidKey() {

    encoder.initEncoder();
    encoder.putInt("$Test", 15);
  }

  @Test
  public void encoder_putValidKeys() {

    encoder.initEncoder();
    encoder.putInt("Test", 15);
    encoder.putInt("Test$", 16);
    assertEquals("{\"Test\":15,\"Test$\":16}", encoder.getEncodedString());
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void encoder_putKeysTwice() {

    encoder.initEncoder();
    encoder.putInt("Test", 15);
    encoder.putInt("Test", 16);

  }

  @Test(expected = LlCoreRuntimeException.class)
  public void encoder_putString_putNullValue_exception_test() {
    encoder.initEncoder();
    encoder.putString("Test", null);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void encoder_putStringList_putNullValue_exception_test() {
    encoder.initEncoder();
    encoder.putStringList("Test", null);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void encoder_putBlob_putNullValue_exception_test() {
    encoder.initEncoder();
    encoder.putBlob("Test",null);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void encoder_putEncodable_putNullValue_exception_test() {
    encoder.initEncoder();
    encoder.putEncodable("Test", null);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void encoder_putEncodableList_putNullValue_exception_test() {
    encoder.initEncoder();
    encoder.putEncodableList("Test", null);
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void encoder_putEncodableList_putNullValueInParts_exception_test() {
    encoder.initEncoder();
    List<IEncodable> encodables = new ArrayList<IEncodable>();
    encodables.add(new EncodableTestSample());
    encodables.add(null);
    encoder.putEncodableList("Test", encodables);
  }


  @Test
  public void encoder_putCorrectSimpleValues() {

    encoder.initEncoder();
    encoder.putString("TestString", "Hallo");
    encoder.putFloat("TestFloat", 12.234f);
    encoder.putDouble("TestDouble", -32423.324);
    encoder.putBoolean("TestBoolean", true);
    encoder.putInt("TestInt", 15);
    encoder.putLong("TestLong", 22L);
    encoder.putBlob("TestBlob", "TestString".getBytes());

    encoder.putString("TestString2", "Testing");
    encoder.putDouble("TestDouble2", 876.234);
    encoder.putFloat("TestFloat2", -1346.8442f);
    encoder.putLong("TestLong2", 22L);
    encoder.putBoolean("TestBoolean2", false);
    encoder.putInt("TestInt2", 0);
    encoder.putBlob("TestBlob2", "SecondTest".getBytes());

    assertEquals("{\"TestString\":\"Hallo\",\"TestFloat\":12.234,\"TestDouble\":-32423.324,"
            + "\"TestBoolean\":true,\"TestInt\":15,\"TestLong\":22,\""
            + "TestBlob\":\"VGVzdFN0cmluZw==\",\"TestString2\":\"Testing\""
            + ",\"TestDouble2\":876.234,\"TestFloat2\":-1346.8442,\"TestLong2\":22,"
            + "\"TestBoolean2\":false,\"TestInt2\":0,\"TestBlob2\":\"U2Vjb25kVGVzdA==\"}",
        encoder.getEncodedString());
  }

  @Test
  public void encoder_putEncodable() {
    encoder.initEncoder();
    encoder.putEncodable("Encodable1", new EncoderTestEncodable("Hallo", 12));
    encoder.putEncodable("Encodable2", new EncoderTestEncodable("Test2", 56));

    assertEquals(
        "{\"Encodable1\":{\"$type\":\"test-encodable\",\"testInt\":12,\"testString\":\"Hallo\","
            + "\"Inner\":{\"$type\":\"test-sample-2\",\"boolVal\":true,\"StringList\":[\"Hallo1"
            + "\",\"Hallo2\",\"Hallo3\"]," + "\"halloString\":\"TestSTRING\",\"long\":125}},"
            + "\"Encodable2\":{\"$type\":\"test-encodable\",\"testInt\":56,"
            + "\"testString\":\"Test2\",\"Inner\":{\"$type\":\"test-sample-2\",\"boolVal\":true,"
            + "\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125}}}", encoder.getEncodedString());
  }

  @Test
  public void encoder_putListOfEncodables() {

    List<IEncodable> testList = new ArrayList<IEncodable>();
    testList.add(new EncoderTestEncodable("Hallo", 12));
    testList.add(new EncoderTestEncodable("Hallo4", 12));
    testList.add(new EncoderTestEncodable("Test2", 56));
    testList.add(new EncoderTestEncodable("Test3", 56));
    encoder.initEncoder();
    encoder.putEncodableList("TestList", testList);

    assertEquals(
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
            + "\"halloString\":\"TestSTRING\",\"long\":125}}]}", encoder.getEncodedString());
  }

  @Test(expected = LlCoreRuntimeException.class)
  public void encoder_putEncodableRecursion() {

    encoder.initEncoder();
    encoder.putEncodable("TestEnc", new EncoderTestEncodableRecursion());
  }

  @Test
  public void encoder_setConfig() {

    assertEquals("Default max stack size for encoder is not 200", 200, encoder.getMaxStackSize());

    Configuration config = new BaseConfiguration();
    config.addProperty("encoding.maxStackSize", 10);
    encoder = new JsonEncoder(config);

    assertEquals("Stack size can't be set", 10, encoder.getMaxStackSize());

    config = new BaseConfiguration();
    encoder = new JsonEncoder(config);

    assertEquals("Stack size doesn't uses default values", 200, encoder.getMaxStackSize());

  }

  @Test
  public void encoder_encodeElement() {

    String jsonString = new String(encoder.processEncoding(new EncodableTestSample(true)));

    assertEquals(
        "{\"$type\":\"test-sample-1\",\"Inner\":{\"$type\":\"test-sample-2\",\"boolVal\":true,"
            + "\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125},"
            + "\"testList\":[{\"$type\":\"test-sample-2\",\"boolVal\":true,\"StringList"
            + "\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING\",\"long\":125},{\"$type\":\"test-sample-2\","
            + "\"boolVal\":true,\"StringList\":[\"Hallo1\",\"Hallo2\",\"Hallo3\"],"
            + "\"halloString\":\"TestSTRING2\",\"long\":128}]}",
        jsonString);
  }


  class EncoderTestEncodableRecursion implements IEncodable {

    @Override
    public void encode(IEncoder encoder) {
      encoder.putEncodable("Test", new EncoderTestEncodableRecursion());
    }

    @Override
    public void decode(IDecoder decoder) {

    }

    @Override
    public String getType() {
      return "test-encodable-reg";
    }

    @Override
    public void decodingCompleted() {

    }

    @Override
    public void validate() {

    }

  }
}





