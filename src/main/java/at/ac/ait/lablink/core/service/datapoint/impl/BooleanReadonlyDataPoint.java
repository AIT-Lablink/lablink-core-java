//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.impl;

import at.ac.ait.lablink.core.service.datapoint.DataPointGeneric;
import at.ac.ait.lablink.core.service.datapoint.IDataPoint;
import at.ac.ait.lablink.core.service.datapoint.payloads.BooleanValue;

import java.util.List;

/**
 * A read-only Boolean Datapoint.
 *
 * <p>This datapoint can not be set from the Lablink. IT can only be set from the hosting client.
 * This class should be used for e.g. measurement values.
 */
public class BooleanReadonlyDataPoint extends DataPointGeneric<Boolean>
    implements IDataPoint<Boolean> {


  /**
   * Constructor.
   *
   * @param identifier Identifier of the datapoint.
   * @param name       Friendly name of the datapoint for additional information.
   * @param unit       Unit of the datapoint.
   */
  public BooleanReadonlyDataPoint(List<String> identifier, String name, String unit) {
    super(identifier, name, unit, false, new BooleanValue());
  }

}
