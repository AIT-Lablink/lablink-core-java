//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.impl;

import at.ac.ait.lablink.core.service.datapoint.DataPointGeneric;
import at.ac.ait.lablink.core.service.datapoint.IDataPoint;
import at.ac.ait.lablink.core.service.types.Complex;

/**
 * A datapoint wrapper for a complex number datapoint that only publishs values with delta.
 *
 * <p>This datapoint will be used to send complex number values if the change of the value is 
 * greater than a given delta.
 */
public class ComplexDeltaDataPoint extends DataPointGeneric<Complex> 
    implements IDataPoint<Complex> {

  private double deltaValue = 0.;

  /**
   * Constructor.
   *
   * @param dataPointGeneric Complex Datapoint that should be used with this wrapper-
   * @param deltaValue       Delta value for calculating changes.
   */
  public ComplexDeltaDataPoint(DataPointGeneric<Complex> dataPointGeneric, Double deltaValue) {
    super(dataPointGeneric.getProps().getIdentifier(), dataPointGeneric.getProps().getName(),
        dataPointGeneric.getProps().getUnit(), dataPointGeneric.getProps().isWriteable(),
        dataPointGeneric.getLastValue());

    this.deltaValue = deltaValue;
  }


  @Override
  public void setValue(Complex value, long timestamp) {
    Complex lastValue = this.getValue();

    double delta = lastValue.minus(value).abs();

    if (delta < deltaValue) {
      return;
    }
    super.setValue(value, timestamp);
  }

}
