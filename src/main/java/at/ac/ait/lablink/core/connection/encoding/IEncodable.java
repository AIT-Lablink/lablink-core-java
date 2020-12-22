//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding;

/**
 * Interface that is used to encode and decode an object.
 *
 * <p>Every class, which should be sent or received by the Lablink connection must implement this
 * interface. The interface is used by the en-/decoder to serialize the class.
 */
public interface IEncodable {

  /**
   * Implementation of the encoding of the class.
   *
   * <p>This method is called to serialize/encode the class. Within this method all available
   * instance variables must be transmitted to the encoder using the {@link IEncoder} methods. For
   * some encoder implementations the order of the serialization is important. The should match
   * with the decode method of this interface
   *
   * @param encoder that should be used, for encoding the method.
   */
  void encode(IEncoder encoder);

  /**
   * Implementation of the decoding of the class.
   *
   * <p>This method is called to deserialize/decode the class. Within this method all available
   * instance variables must be read from the decoder using the {@link IDecoder} methods. For
   * some decoder implementations the order of the serialization is important. The should match
   * with the encode method of this interface.
   *
   * @param decoder that should be used for decoding the object.
   */
  void decode(IDecoder decoder);

  /**
   * Returns a unique string that represents the class.
   *
   * <p>A name for the class is needed to map the class into an encoded dictionary.
   *
   * @return a unique (within the system) string of the class.
   */
  String getType();

  /**
   * Callback method to inform the object that the decoding is completed.
   *
   * <p>The method will be called by the decoder, if it completes the decoding of the object. It
   * can be used to implement calculation that depends on the read values.
   */
  void decodingCompleted();

  /**
   * Validation method for an IEncodable object.
   *
   * <p>Every encodable object should have such a method to validate all of its containing
   * elements. The method will be used before publishing a packet for allowing feedback of
   * correct objects to the user.
   *
   * @throws at.ac.ait.lablink.core.ex.LlCoreRuntimeException if a validation error occurs
   */
  void validate();
}
