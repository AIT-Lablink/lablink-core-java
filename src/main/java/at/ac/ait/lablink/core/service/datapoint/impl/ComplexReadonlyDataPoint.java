//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.impl;

import at.ac.ait.lablink.core.service.datapoint.DataPointGeneric;
import at.ac.ait.lablink.core.service.datapoint.IDataPoint;
import at.ac.ait.lablink.core.service.datapoint.payloads.ComplexValue;
import at.ac.ait.lablink.core.service.types.Complex;

import java.util.List;

/**
 * A read-only complex number datapoint.
 *
 * <p>This datapoint can not be set from the Lablink. IT can only be set from the hosting client.
 * This class should be used for e.g. measurement values.
 */
public class ComplexReadonlyDataPoint extends DataPointGeneric<Complex> 
    implements IDataPoint<Complex> {
  /**
   * Constructor.
   *
   * @param identifier Identifier of the datapoint.
   * @param name       Friendly name of the datapoint for additional information.
   * @param unit       Unit of the datapoint.
   */
  public ComplexReadonlyDataPoint(List<String> identifier, String name, String unit) {
    super(identifier, name, unit, false, new ComplexValue());
  }

}
