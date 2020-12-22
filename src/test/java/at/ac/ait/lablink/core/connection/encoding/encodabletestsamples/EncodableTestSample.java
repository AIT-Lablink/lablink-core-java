//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding.encodabletestsamples;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.connection.ex.LlCoreDecoderRuntimeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Test samples for encodable classes.
 */
public class EncodableTestSample implements IEncodable {

  EncodableTestSample2 innerClass = new EncodableTestSample2();

  final List<EncodableTestSample2> testList = new ArrayList<EncodableTestSample2>();

  public EncodableTestSample() {
  }

  public EncodableTestSample(boolean init) {
    if (init) {
      testList.add(new EncodableTestSample2());
      testList.add(new EncodableTestSample2("TestSTRING2", 128));
    }
  }

  @Override
  public void encode(IEncoder encoder) {

    encoder.putEncodable("Inner", innerClass);
    encoder.putEncodableList("testList", testList);
  }

  @Override
  public void decode(IDecoder decoder) {

    IEncodable innerClass = decoder.getEncodable("Inner");
    if (innerClass instanceof EncodableTestSample2) {
      this.innerClass = (EncodableTestSample2) innerClass;
    } else {
      throw new LlCoreDecoderRuntimeException(
          "Decoded element (" + innerClass.getClass() + ") isn't a EncodableTestSample2 element.");
    }

    List<? extends IEncodable> encodables = decoder.getEncodables("testList");

    for (IEncodable encodable : encodables) {
      if (encodable instanceof EncodableTestSample2) {
        testList.add((EncodableTestSample2) encodable);
      } else {
        throw new LlCoreDecoderRuntimeException(
            "Decoded element (" + encodable.getClass()
                + ") isn't a EncodableTestSample2 element.");
      }

    }
  }

  @Override
  public String getType() {
    return "test-sample-1";
  }

  @Override
  public void decodingCompleted() {

  }

  @Override
  public void validate() {

  }

  @Override
  public String toString() {
    return "EncodableTestSample{" 
        + "innerClass=" + innerClass
        + ", testList=" + testList
        + '}';
  }
}
