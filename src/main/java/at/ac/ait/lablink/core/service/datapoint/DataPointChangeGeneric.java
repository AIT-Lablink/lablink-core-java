//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint;

/**
 * A datapoint wrapper for publishing changes.
 *
 * <p>This datapoint wrapper can be used to publish updates only if the value of the datapoint is
 * changed. So the calling of the setValue method will check if the value is changed.
 */
public class DataPointChangeGeneric<T> extends DataPointGeneric<T> implements IDataPoint<T> {

  /**
   * Constructor.
   *
   * @param dataPointGeneric Datapoint that will be used by the wrapper.
   */
  public DataPointChangeGeneric(DataPointGeneric<T> dataPointGeneric) {
    super(dataPointGeneric.getProps().getIdentifier(), dataPointGeneric.getProps().getName(),
        dataPointGeneric.getProps().getUnit(), dataPointGeneric.getProps().isWriteable(),
        dataPointGeneric.getLastValue());
  }


  @Override
  public void setValue(T value, long timestamp) {
    if (getValue() == null || !getValue().equals(value)) {
      super.setValue(value, timestamp);
    }
  }

  @Override
  public void setValue(T value) {
    if (getValue() == null || !getValue().equals(value)) {
      super.setValue(value);
    }
  }
}
