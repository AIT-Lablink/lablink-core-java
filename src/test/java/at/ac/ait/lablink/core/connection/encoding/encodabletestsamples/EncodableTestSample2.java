//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.encodabletestsamples;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;

import java.util.Arrays;
import java.util.List;

/**
 * More test samples for encodable classes.
 */
public class EncodableTestSample2 implements IEncodable {

  String testString = "TestSTRING";
  List<String> testStringList = Arrays.asList("Hallo1", "Hallo2", "Hallo3");
  boolean testBoolean = true;
  long testLong = (long) 125;


  public EncodableTestSample2() {

  }

  public EncodableTestSample2(String testString, long testLong) {
    this.testString = testString;
    this.testLong = testLong;
  }

  @Override
  public void encode(IEncoder encoder) {

    encoder.putBoolean("boolVal", testBoolean);
    encoder.putStringList("StringList",testStringList);
    encoder.putString("halloString", testString);
    encoder.putLong("long", testLong);
  }

  @Override
  public void decode(IDecoder decoder) {

    testBoolean = decoder.getBoolean("boolVal");
    testStringList = decoder.getStrings("StringList");
    testString = decoder.getString("halloString");
    testLong = decoder.getLong("long");
  }

  @Override
  public String getType() {
    return "test-sample-2";
  }

  @Override
  public void decodingCompleted() {

  }

  @Override
  public void validate() {

  }

  @Override
  public String toString() {
    return "EncodableTestSample2{"
        + "testString='" + testString + '\''
        + ", testStringList='" + testStringList + '\''
        + ", testBoolean=" + testBoolean
        + ", testLong=" + testLong
        + '}';
  }
}
