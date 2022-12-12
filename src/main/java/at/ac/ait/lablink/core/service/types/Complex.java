//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.types;

import java.util.Objects;

/**
 * Data type for complex numbers.
 *
 * <p>The data type is "immutable" so once you create and initialize
 * a Complex object, you cannot change it. The "final" keyword
 * when declaring re and im enforces this rule, making it a
 * compile-time error to change the .re or .im instance variables after
 * they've been initialized.
 *
 * <p>Original authors: Robert Sedgewick and Kevin Wayne
 * Original source code: https://introcs.cs.princeton.edu/java/32class/Complex.java.html
 **/
public class Complex {

  /// The real part.
  private final double re;

  /// The imaginary part.
  private final double im;

  /**
   *  Create a new object with the given real and imaginary parts.
   *  @param real real part
   *  @param imag imaginary part
   */
  public Complex(double real, double imag) {
    re = real;
    im = imag;
  }

  /**
   *  Return absolute value.
   *  @return absolute value
   */
  public double abs() {
    return Math.hypot(re, im);
  }

  /**
   *  Return angle/phase/argument, normalized to be between -pi and pi.
   *  @return angle/phase/argument
   */
  public double phase() {
    return Math.atan2(im, re);
  }

  /**
   *  Return a new Complex object whose value is (this + other).
   *  @param other complex number to add
   *  @return (this + other)
   */
  public Complex plus(Complex other) {
    double real = this.re + other.re;
    double imag = this.im + other.im;
    return new Complex(real, imag);
  }

  /**
   *  Return a new Complex object whose value is (this - other).
   *  @param other complex number to subtract
   *  @return (this - other)
   */
  public Complex minus(Complex other) {
    double real = this.re - other.re;
    double imag = this.im - other.im;
    return new Complex(real, imag);
  }

  /**
   *  Return a new Complex object whose value is (this * other).
   *  @param other complex number to multiply with
   *  @return (this * other)
   */
  public Complex times(Complex other) {
    double real = this.re * other.re - this.im * other.im;
    double imag = this.re * other.im + this.im * other.re;
    return new Complex(real, imag);
  }

  /**
   *  Return a new object whose value is (this * alpha).
   *  @param alpha scaling factor
   *  @return (alpha * other)
   */
  public Complex scale(double alpha) {
    return new Complex(alpha * re, alpha * im);
  }

  /**
   *  Return a new Complex object whose value is the conjugate of this.
   *  @return complex conjugate
   */
  public Complex conjugate() {
    return new Complex(re, -im);
  }

  /**
   *  Return a new Complex object whose value is the reciprocal of this.
   *  @return this / abs(this)
   */
  public Complex reciprocal() {
    double scale = re * re + im * im;
    return new Complex(re / scale, -im / scale);
  }

  /**
   *  Return the real part.
   *  @return real part
   */
  public double re() {
    return re;
  }

  /**
   *  Return the imaginary part.
   *  @return imaginary part
   */
  public double im() {
    return im;
  }

  /**
   *  Return (this / other).
   *  @param other complex number to divide with
   *  @return (this / other)
   */
  public Complex divides(Complex other) {
    return this.times(other.reciprocal());
  }

  /**
   *  Return a new Complex object whose value is the complex exponential of this.
   *  @return exp(this)
   */
  public Complex exp() {
    return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
  }

  /**
   *  Return a new Complex object whose value is the complex sine of this.
   *  @return sin(this)
   */
  public Complex sin() {
    return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
  }

  /**
   *  Return a new Complex object whose value is the complex cosine of this.
   *  @return cos(this)
   */
  public Complex cos() {
    return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
  }

  /**
   *  Return a new Complex object whose value is the complex tangent of this.
   *  @return tan(this)
   */
  public Complex tan() {
    return sin().divides(cos());
  }

  /**
   *  Compare for equality.
   *  @param other complex number
   *  @return (this == other)
   */
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }
    if (this.getClass() != other.getClass()) {
      return false;
    }
    Complex that = (Complex) other;
    return (this.re == that.re) && (this.im == that.im);
  }

  /**
   *  Return hash code.
   *  @return hash code
   */
  public int hashCode() {
    return Objects.hash(re, im);
  }

  /**
   * Return a string representation of the invoking Complex object.
   * @return string representation
   */
  public String toString() {
    if (im == 0) {
      return re + "";
    }
    if (re == 0) {
      return "j*" + im;
    }
    if (im <  0) {
      return re + " - j*" + (-im);
    }
    return re + " + j*" + im;
  }
}
