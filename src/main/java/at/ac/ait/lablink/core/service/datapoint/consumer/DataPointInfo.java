//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.consumer;

import at.ac.ait.lablink.core.service.datapoint.payloads.DataPointProperties;

import java.util.List;

/**
 * Information template of a datapoint consumer.
 *
 * <p>This class provides the read properties of a remote datapoint.
 */
public class DataPointInfo {


  private String srcGroupId;
  private String srcClientId;

  private DataPointProperties dataPointProperties;

  /**
   * Default constructor of the IDataPoint info.
   *
   * @param srcGroupId          Group identifier of the remote datapoint.
   * @param srcClientId         Client identifier of the remote datapoint.
   * @param dataPointProperties Properties of the remote datapoint.
   */
  DataPointInfo(String srcGroupId, String srcClientId, DataPointProperties dataPointProperties) {
    this.srcGroupId = srcGroupId;
    this.srcClientId = srcClientId;
    this.dataPointProperties = dataPointProperties;
  }

  public String getSrcGroupId() {
    return srcGroupId;
  }

  public String getSrcClientId() {
    return srcClientId;
  }

  /**
   * Get the identifier of the datapoint.
   *
   * @return identifier
   */
  public List<String> getIdentifier() {
    return dataPointProperties.getIdentifier();
  }

  /**
   * Get the friendly name of the datapoint.
   *
   * @return name of the datapoint
   */
  public String getFriendlyName() {
    return dataPointProperties.getName();
  }

  /**
   * Get the unit of the datapoint.
   *
   * @return unit.
   */
  public String getUnit() {
    return dataPointProperties.getUnit();
  }

  /**
   * Check if the writeable flag is set.
   *
   * @return the writeable flag
   */
  public boolean isWriteable() {
    return dataPointProperties.isWriteable();
  }

  /**
   * Get the data type of the value.
   *
   * @return value's datatype
   */
  public String getDatapointType() {
    return dataPointProperties.getDatapointType().getSimpleName();
  }

  @Override
  public String toString() {
    return "DataPointInfo{" + "srcGroupId='" + srcGroupId + '\'' + ", srcClientId='" + srcClientId
        + '\'' + ", dataPointProperties=" + dataPointProperties + '}';
  }
}
