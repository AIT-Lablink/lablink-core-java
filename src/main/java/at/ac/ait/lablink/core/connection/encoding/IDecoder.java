//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding;

import java.util.List;

/**
 * Common IDecoder Interface.
 *
 * <p>This interface is used to get different variables fom the decoder. Every method reads a
 * decoded value and should be assign it to its internal instance variable. Some encoder aren't
 * using a dictionary to store the variables. So the decoding should be in the same order as the
 * encoding of the class.
 *
 */
public interface IDecoder {

  /**
   * Read a string value from the decoder.
   *
   * @param key for the value to be read
   * @return value from the decoder
   */
  String getString(String key);

  /**
   * Read a List off strings from the decoder.
   *
   * @param key for the value to be read
   * @return value from the decoder
   */
  List<String> getStrings(String key);

  /**
   * Read a double value from the decoder.
   *
   * @param key for the value to be read
   * @return value from the decoder
   */
  float getFloat(String key);

  /**
   * Read a double value from the decoder.
   *
   * @param key for the value to be read
   * @return value from the decoder
   */
  double getDouble(String key);

  /**
   * Read an integer value from the decoder.
   *
   * @param key for the value to be read
   * @return value from the decoder
   */
  boolean getBoolean(String key);

  /**
   * Read an integer value from the decoder.
   *
   * @param key for the value to be read
   * @return value from the decoder
   */
  int getInt(String key);

  /**
   * Read an integer value from the decoder.
   *
   * @param key for the value to be read
   * @return value from the decoder
   */
  long getLong(String key);

  /**
   * Read a byte array from the decoder.
   *
   * @param key for the value to be read
   * @return value from the decoder
   */
  byte[] getBlob(String key);

  /**
   * Read a {@link IEncodeable} from the decoder.
   *
   * @param key for the value to be read
   * @return value from the decoder
   */
  IEncodeable getEncodeable(String key);

  /**
   * Read a List off {@link IEncodeable} from the decoder.
   *
   * @param key for the value to be read
   * @return value from the decoder
   */
  List<? extends IEncodeable> getEncodeables(String key);
}
