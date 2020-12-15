//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.payloads;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.encoding.IEncodeableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;

/**
 * A simple value format containing a double.
 */
public class DoubleValue extends BaseValue implements ISimpleValue<Double> {

  /**
   * Get a type string of the class.
   *
   * <p><b>This static method must be implemented by every subclass.</b>
   *
   * <p>Every class that is encodeable and is used by a decoder must have a unique string that
   * identifies this class. This type string will be transmitted during the communication and will
   * be used by a decoder for creating an empty object of the encodeable class.
   *
   * @return an unique type string of the class
   */
  public static String getClassType() {
    return "doubleValue";
  }

  /**
   * Get the factory to create objects of the class.
   *
   * <p><b>This static method must be implemented by every subclass.</b>
   *
   * <p>Every class that is encodeable and is used by a decoder must have a unique factory object
   * to create empty objects of the class. This factory method will be used by the decoder to
   * create a fresh object that can be filled in with the decoded values.
   *
   * @return A factory object for creating encodeable classes
   */
  public static IEncodeableFactory getEncodeableFactory() {
    return new IEncodeableFactory() {
      @Override
      public IEncodeable createEncodeableObject() {
        return new DoubleValue();
      }
    };
  }


  private double value = 0.0;

  /**
   * Default constructor.
   */
  public DoubleValue() {
    super();
  }

  /**
   * Constructor.
   *
   * @param value Set the string value of the payloads.
   * @param time  Set a time in milliseconds for the payloads.
   */
  public DoubleValue(double value, long time) {
    super(time);
    this.value = value;
  }

  /**
   * Constructor with current time automatically set.
   *
   * @param value Set the string value of the payloads.
   */
  public DoubleValue(double value) {
    super();
    this.value = value;
  }


  @Override
  public void validate() {
    super.validate();
  }

  @Override
  public void encode(final IEncoder encoder) {
    super.encode(encoder);
    encoder.putDouble("value", value);
  }

  @Override
  public void decode(final IDecoder decoder) {
    super.decode(decoder);
    value = decoder.getDouble("value");
  }

  @Override
  public String getType() {
    return DoubleValue.getClassType();
  }

  @Override
  public void decodingCompleted() {
    super.decodingCompleted();
  }

  @Override
  public String toString() {
    return "DoubleValue{" + "value=" + value + ", " + super.toString() + '}';
  }

  @Override
  public void setValue(Double value) {
    this.value = value;
  }

  public Double getValue() {
    return value;
  }

}
