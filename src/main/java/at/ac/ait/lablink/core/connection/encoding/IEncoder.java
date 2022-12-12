//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding;

import at.ac.ait.lablink.core.service.types.Complex;

import java.util.List;

/**
 * Common IEncoder Interface.
 *
 * <p>This interface is used to add different variables to the encoder. Every method call
 * adds the variable to a encoder'S internal dictionary. The {@link IEncodable} can be 
 * nested into the encoder. Keys for specific values must be unique and are only allowed 
 * to add once to the encoder.
 *
 */
public interface IEncoder {

  /**
   * Put a string value to the encoder.
   *
   * @param key   for the given value
   * @param value to be added
   */
  void putString(String key, String value);

  /**
   * Put a list of strings to the encoder.
   *
   * <p>Must be renamed because overloading a method with generic list element doesn't work.
   *
   * @param key    for the given value
   * @param values to be added
   */
  void putStringList(String key, List<String> values);

  /**
   * Put a double value to the encoder.
   *
   * @param key   for the given value
   * @param value to be added
   */
  void putFloat(String key, float value);

  /**
   * Put a double value to the encoder.
   *
   * @param key   for the given value
   * @param value to be added
   */
  void putDouble(String key, double value);

  /**
   * Put an int value to the encoder.
   *
   * @param key   for the given value
   * @param value to be added
   */
  void putBoolean(String key, boolean value);

  /**
   * Put an int value to the encoder.
   *
   * @param key   for the given value
   * @param value to be added
   */
  void putInt(String key, int value);

  /**
   * Put an int value to the encoder.
   *
   * @param key   for the given value
   * @param value to be added
   */
  void putLong(String key, long value);

  /**
   * Put a byte array to the encoder.
   *
   * @param key   for the given value
   * @param value to be added
   */
  void putBlob(String key, byte[] value);

  /**
   * Put a complex number to the encoder.
   *
   * @param key   for the given value
   * @param value to be added
   */
  void putComplex(String key, Complex value);

  /**
   * Put an {@link IEncodable} object to the encoder.
   *
   * @param key   for the given value
   * @param value to be added
   */
  void putEncodable(String key, IEncodable value);

  /**
   * Put a list of {@link IEncodable} to the encoder.
   *
   * @param key    for the given value
   * @param values to be added
   */
  void putEncodableList(String key, List<? extends IEncodable> values);
}
