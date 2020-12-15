//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.connection.encoding;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodeable;

import java.util.Arrays;

/**
 * Abstract base class of a decoder that is used by Lablink communication.
 *
 * <p>It will be used by the incoming handlers to provide an abstract representation for holding
 * specific decoder.
 */
public abstract class DecoderBase implements IDecoder {

  /* temporary storage for last decoded payloads */
  private int lastSourceHash = 0;

  /**
   * Factory Manager for IEncodeable object creation.
   */
  protected IEncodeableFactoryManager encodeableFactoryManager;

  /**
   * Set the factory manager for generating encodeable during decoding.
   *
   * @param encodeableFactoryManager to be used by the decoder
   */
  public void setEncodeableFactoryManager(IEncodeableFactoryManager encodeableFactoryManager) {
    this.encodeableFactoryManager = encodeableFactoryManager;
  }

  /**
   * Returns the decoded representation of the element.
   *
   * @return encodeable object
   */
  protected abstract IEncodeable getDecodedElement();

  /**
   * Decode the source element.
   *
   * @param source element to be decoded
   */
  protected abstract void decodeElement(byte[] source);

  /**
   * Process decoding.
   *
   * @param source element to be decoded.
   * @return decoded element.
   */
  public synchronized IEncodeable processDecoding(byte[] source) {

    /* Decode only if source array has been changed */
    int sourceHash = Arrays.hashCode(source);
    if (lastSourceHash != sourceHash) {
      decodeElement(source);
      lastSourceHash = sourceHash;
    }
    return getDecodedElement();
  }
}
