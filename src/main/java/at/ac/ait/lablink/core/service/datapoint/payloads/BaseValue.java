//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.payloads;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.connection.encoding.encodeables.PayloadBase;

/**
 * A simple value format containing a boolean.
 */
public abstract class BaseValue extends PayloadBase {


  private long time;
  private long emulationTime = -1;

  /**
   * Default constructor.
   */
  public BaseValue() {
    this.time = System.currentTimeMillis();
  }

  /**
   * Constructor.
   *
   * @param time Set a time in milliseconds for the payloads.
   */
  public BaseValue(long time) {
    this.time = time;
  }


  @Override
  public void validate() {

  }

  @Override
  public void encode(final IEncoder encoder) {
    encoder.putLong("time", time);
    encoder.putLong("emuTime", emulationTime);
  }

  @Override
  public void decode(final IDecoder decoder) {
    time = decoder.getLong("time");
    emulationTime = decoder.getLong("emuTime");
  }


  @Override
  public void decodingCompleted() {

  }


  public void setTime(long time) {
    this.time = time;
  }

  public long getTime() {
    return time;
  }


  public void setEmulationTime(long emulationTime) {
    this.emulationTime = emulationTime;
  }

  public long getEmulationTime() {
    return emulationTime;
  }
}
