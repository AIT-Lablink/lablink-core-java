//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.impl;

import at.ac.ait.lablink.core.service.datapoint.DataPointGeneric;
import at.ac.ait.lablink.core.service.datapoint.IDataPoint;

/**
 * A datapoint wrapper for a Double Datapoint that only publishs values with delta.
 *
 * <p>This datapoint will be used to send double values if the change of the value is greater than
 * a given delta.
 */
public class DoubleDeltaDataPoint extends DataPointGeneric<Double> implements IDataPoint<Double> {

  private double deltaValue = 0.0;

  /**
   * Constructor.
   *
   * @param dataPointGeneric Double Datapoint that should be used with this wrapper-
   * @param deltaValue       Delta value for calculating changes.
   */
  public DoubleDeltaDataPoint(DataPointGeneric<Double> dataPointGeneric, double deltaValue) {
    super(dataPointGeneric.getProps().getIdentifier(), dataPointGeneric.getProps().getName(),
        dataPointGeneric.getProps().getUnit(), dataPointGeneric.getProps().isWriteable(),
        dataPointGeneric.getLastValue());

    this.deltaValue = deltaValue;
  }


  @Override
  public void setValue(Double value, long timestamp) {
    double lastValue = this.getValue();

    if (lastValue - deltaValue < value && value < lastValue + deltaValue) {
      return;
    }
    super.setValue(value, timestamp);
  }

}
