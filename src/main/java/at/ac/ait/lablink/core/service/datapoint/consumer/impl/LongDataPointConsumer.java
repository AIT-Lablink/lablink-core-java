//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.consumer.impl;

import at.ac.ait.lablink.core.service.datapoint.consumer.DataPointConsumerGeneric;
import at.ac.ait.lablink.core.service.datapoint.consumer.IDataPointConsumer;
import at.ac.ait.lablink.core.service.datapoint.payloads.LongValue;

import java.util.List;

/**
 * A Datapoint consumer implementation for a long datapoint.
 */
public class LongDataPointConsumer extends DataPointConsumerGeneric<Long>
    implements IDataPointConsumer<Long> {

  /**
   * Constructor.
   *
   * @param remoteGroup  Group identifier of the remote datapoint
   * @param remoteClient Client identifier of the remote datapoint
   * @param identifier   Datapoint identifier
   */
  public LongDataPointConsumer(String remoteGroup, String remoteClient, List<String> identifier) {
    super(remoteGroup, remoteClient, identifier, new LongValue());
  }
}
