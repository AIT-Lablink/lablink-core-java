//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.payloads;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.connection.encoding.encodables.PayloadBase;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

/**
 * A simple value format containing a string.
 */
public class StringMessage extends PayloadBase {

  /**
   * Get a type string of the class.
   *
   * <p><b>This static method must be implemented by every subclass.</b>
   *
   * <p>Every class that is encodable and is used by a decoder must have a unique string that
   * identifies this class. This type string will be transmitted during the communication and will
   * be used by a decoder for creating an empty object of the encodable class.
   *
   * @return an unique type string of the class
   */
  public static String getClassType() {
    return "stringMsg";
  }

  /**
   * Get the factory to create objects of the class.
   *
   * <p><b>This static method must be implemented by every subclass.</b>
   *
   * <p>Every class that is encodable and is used by a decoder must have a unique factory object
   * to create empty objects of the class. This factory method will be used by the decoder to
   * create a fresh object that can be filled in with the decoded values.
   *
   * @return A factory object for creating encodable classes
   */
  public static IEncodableFactory getEncodableFactory() {
    return new IEncodableFactory() {
      @Override
      public IEncodable createEncodableObject() {
        return new StringMessage();
      }
    };
  }


  private String value;

  /**
   * Default constructor.
   */
  public StringMessage() {
  }


  /**
   * Constructor.
   *
   * @param value Set the string value of the payloads.
   */
  public StringMessage(String value) {
    this.value = value;

  }


  @Override
  public void validate() {

    if (this.value == null || this.value.isEmpty()) {
      throw new LlCoreRuntimeException("Value string in StringMessage is null or empty.");
    }
  }

  @Override
  public void encode(final IEncoder encoder) {
    encoder.putString("value", value);
  }

  @Override
  public void decode(final IDecoder decoder) {
    value = decoder.getString("value");
  }

  @Override
  public String getType() {
    return StringMessage.getClassType();
  }

  @Override
  public void decodingCompleted() {

  }

  @Override
  public String toString() {
    return "StringMessage{" + "value='" + value + "'}";
  }


  /**
   * Set the string value of the message.
   *
   * @param value to be set for the message.
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Get the string value of the Message.
   *
   * @return the string value of the message.
   */
  public String getValue() {
    return value;
  }


}
