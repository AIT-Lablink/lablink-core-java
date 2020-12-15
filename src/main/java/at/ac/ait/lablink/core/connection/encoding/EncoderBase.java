//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding;

import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;

import at.ac.ait.lablink.core.connection.ex.LlCoreEncoderRuntimeException;

/**
 * Base class of the IEncoder object.
 */
public abstract class EncoderBase implements IEncoder {

  /**
   * Get the encoded object
   *
   * @return the encoded object, if the encoding is finished.
   * @throws LlCoreEncoderRuntimeException if the encoding hasn't finished
   */
  public abstract byte[] getEncoded();

  /**
   * Encode a whole base {@link IEncodeable} object.
   *
   * <p>The method will initialize and setup the encoder to encode a new IEncodeable element.
   * Therefore it will clear the encoder stack and reinitialize it. After that it will start to
   * encode the first element.
   *
   * @param value IEncodeable object to be encoded
   */
  protected abstract void encodeElement(IEncodeable value);

  /**
   * Process the encoding of an {@link IEncodeable} object.
   *
   * @param source object to be encoded.
   * @return the encoded object as blob for further usage. The encoded object can be read more often
   *         using the {@link #getEncoded()} method until a new encoding has been started.
   */
  public synchronized byte[] processEncoding(IEncodeable source) {
    encodeElement(source);
    return getEncoded();
  }
}