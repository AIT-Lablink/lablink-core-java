//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.impl;

import at.ac.ait.lablink.core.service.datapoint.DataPointGeneric;
import at.ac.ait.lablink.core.service.datapoint.IDataPoint;
import at.ac.ait.lablink.core.service.datapoint.payloads.DoubleValue;

import java.util.List;

/**
 * A writeable double Datapoint.
 *
 * <p>This datapoint can be set from the Lablink.
 */
public class DoubleDataPoint extends DataPointGeneric<Double> implements IDataPoint<Double> {

  /**
   * Constructor.
   *
   * @param identifier Identifier of the datapoint.
   * @param name       Friendly name of the datapoint for additional information.
   * @param unit       Unit of the datapoint.
   */
  public DoubleDataPoint(List<String> identifier, String name, String unit) {
    super(identifier, name, unit, true, new DoubleValue());
  }
}
