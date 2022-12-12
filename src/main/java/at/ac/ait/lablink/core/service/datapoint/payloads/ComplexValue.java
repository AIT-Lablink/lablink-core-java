//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.payloads;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodable;
import at.ac.ait.lablink.core.connection.encoding.IEncodableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.service.types.Complex;

/**
 * A simple value format containing a complex number.
 */
public class ComplexValue extends BaseValue implements ISimpleValue<Complex> {

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
    return "complexValue";
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
        return new ComplexValue();
      }
    };
  }

  private double re = 0.0;
  private double im = 0.0;

  /**
   * Default constructor.
   */
  public ComplexValue() {
    super();
  }

  /**
   * Constructor.
   *
   * @param value Set the string value of the payloads.
   * @param time  Set a time in milliseconds for the payloads.
   */
  public ComplexValue(Complex value, long time) {
    super(time);
    this.re = value.re();
    this.im = value.im();
  }

  /**
   * Constructor with current time automatically set.
   *
   * @param value Set the string value of the payloads.
   */
  public ComplexValue(Complex value) {
    super();
    this.re = value.re();
    this.im = value.im();
  }


  @Override
  public void validate() {
    super.validate();
  }

  @Override
  public void encode(final IEncoder encoder) {
    super.encode(encoder);
    encoder.putDouble("re", this.re);
    encoder.putDouble("im", this.im);
  }

  @Override
  public void decode(final IDecoder decoder) {
    super.decode(decoder);
    this.re = decoder.getDouble("re");
    this.im = decoder.getDouble("im");
  }

  @Override
  public String getType() {
    return ComplexValue.getClassType();
  }

  @Override
  public void decodingCompleted() {
    super.decodingCompleted();
  }

  @Override
  public String toString() {
    return "ComplexValue{" + "re=" + this.re + ", im=" + this.im + ", " + super.toString() + '}';
  }

  @Override
  public void setValue(Complex value) {
    this.re = value.re();
    this.im = value.im();
  }

  public Complex getValue() {
    return new Complex(this.re, this.im);
  }

}
