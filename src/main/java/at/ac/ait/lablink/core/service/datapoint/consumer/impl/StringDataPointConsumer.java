//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.consumer.impl;

import at.ac.ait.lablink.core.service.datapoint.consumer.DataPointConsumerGeneric;
import at.ac.ait.lablink.core.service.datapoint.consumer.IDataPointConsumer;
import at.ac.ait.lablink.core.service.datapoint.payloads.StringValue;

import java.util.List;

/**
 * A Datapoint consumer implementation for a string datapoint.
 */
public class StringDataPointConsumer extends DataPointConsumerGeneric<String>
    implements IDataPointConsumer<String> {

  /**
   * Constructor.
   *
   * @param remoteGroup  Group identifier of the remote datapoint
   * @param remoteClient Client identifier of the remote datapoint
   * @param identifier   Datapoint identifier
   */
  public StringDataPointConsumer(String remoteGroup, String remoteClient, List<String> identifier) {
    super(remoteGroup, remoteClient, identifier, new StringValue());
  }
}
