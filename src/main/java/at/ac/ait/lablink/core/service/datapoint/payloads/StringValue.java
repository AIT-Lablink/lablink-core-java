//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.payloads;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;

/**
 * A simple value format containing a string.
 */
public class StringValue extends BaseValue implements ISimpleValue<String> {

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
    return "stringValue";
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
        return new StringValue();
      }
    };
  }


  private String value = "";

  /**
   * Default constructor.
   */
  public StringValue() {
    super();
  }

  /**
   * Constructor.
   *
   * @param value Set the string value of the payloads.
   * @param time  Set a time in milliseconds for the payloads.
   */
  public StringValue(String value, long time) {
    super(time);
    this.value = value;
  }

  /**
   * Constructor with current time automatically set.
   *
   * @param value Set the string value of the payloads.
   */
  public StringValue(String value) {
    super();
    this.value = value;
  }


  @Override
  public void validate() {
    super.validate();
    if (this.value == null) {
      throw new LlCoreRuntimeException("Value string in StringValue is null or empty.");
    }
  }

  @Override
  public void encode(final IEncoder encoder) {
    super.encode(encoder);
    encoder.putString("value", value);
  }

  @Override
  public void decode(final IDecoder decoder) {
    super.decode(decoder);
    value = decoder.getString("value");
  }

  @Override
  public String getType() {
    return StringValue.getClassType();
  }

  @Override
  public void decodingCompleted() {

  }

  @Override
  public String toString() {
    return "StringValue{" + "value='" + value + '\'' + ", " + super.toString() + '}';
  }

  @Override
  public void setValue(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
