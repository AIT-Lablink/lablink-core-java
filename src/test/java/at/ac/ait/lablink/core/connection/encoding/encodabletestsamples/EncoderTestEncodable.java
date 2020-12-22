//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.encodabletestsamples;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.connection.ex.LlCoreDecoderRuntimeException;

public class EncoderTestEncodable implements IEncodable {

  private int testInt;
  private String testString;
  EncodableTestSample2 innerClass = new EncodableTestSample2();

  public EncoderTestEncodable(String testString, int testInt) {
    this.testString = testString;
    this.testInt = testInt;
  }

  @Override
  public void encode(IEncoder encoder) {
    encoder.putInt("testInt", testInt);
    encoder.putString("testString", testString);
    encoder.putEncodable("Inner", innerClass);
  }

  @Override
  public void decode(IDecoder decoder) {
    testInt = decoder.getInt("testInt");
    testString = decoder.getString("testString");

    IEncodable innerClass = decoder.getEncodable("Inner");
    if (innerClass instanceof EncodableTestSample2) {
      this.innerClass = (EncodableTestSample2) innerClass;
    } else {
      throw new LlCoreDecoderRuntimeException(
          "Decoded element (" + innerClass.getClass() + ") isn't a EncodableTestSample2 element.");
    }

  }

  @Override
  public String getType() {
    return "test-encodable";
  }

  @Override
  public void decodingCompleted() {

  }

  @Override
  public void validate() {

  }

  @Override
  public String toString() {
    return "EncoderTestEncodable{"
        + "testInt=" + testInt
        + ", testString='" + testString + '\''
        + ", innerClass=" + innerClass
        + '}';
  }
}
