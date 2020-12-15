//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding;

/**
 * Common interface for creating encodeable objects.
 *
 * <p>This interface will be used by a factory manager to create different encodeable objects
 * (mainly during decoding). Therefore a factory for every convertible (encodeable) object is
 * required that must be especially implemented by every class.
 */
public interface IEncodeableFactory {

  /**
   * Create an empty and fresh encodeable object.
   *
   * @return an empty object of the class.
   */
  IEncodeable createEncodeableObject();
}
