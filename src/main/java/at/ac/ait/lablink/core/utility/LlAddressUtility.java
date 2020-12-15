//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.utility;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

public class LlAddressUtility {

  /**
   * Gets the random client id.
   *
   * @return the random client id
   */
  public static long getRandomClientId() {
    long val = -1;
    do {
      final UUID uid = UUID.randomUUID();
      final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
      buffer.putLong(uid.getLeastSignificantBits());
      buffer.putLong(uid.getMostSignificantBits());
      final BigInteger bi = new BigInteger(buffer.array());
      val = bi.longValue();
    } while (val < 0);

    return val;
  }

}
