//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding;

/**
 * Common interface for creating encodable objects.
 *
 * <p>This interface will be used by a factory manager to create different encodable objects
 * (mainly during decoding). Therefore a factory for every convertible (encodable) object is
 * required that must be especially implemented by every class.
 */
public interface IEncodableFactory {

  /**
   * Create an empty and fresh encodable object.
   *
   * @return an empty object of the class.
   */
  IEncodable createEncodableObject();
}
