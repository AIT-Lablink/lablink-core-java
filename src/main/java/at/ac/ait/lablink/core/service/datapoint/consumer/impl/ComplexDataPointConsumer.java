//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.consumer.impl;

import at.ac.ait.lablink.core.service.datapoint.consumer.DataPointConsumerGeneric;
import at.ac.ait.lablink.core.service.datapoint.consumer.IDataPointConsumer;
import at.ac.ait.lablink.core.service.datapoint.payloads.ComplexValue;
import at.ac.ait.lablink.core.service.types.Complex;

import java.util.List;

/**
 * A Datapoint consumer implementation for a complex number datapoint.
 */
public class ComplexDataPointConsumer extends DataPointConsumerGeneric<Complex>
    implements IDataPointConsumer<Complex> {

  /**
   * Constructor.
   *
   * @param remoteGroup  Group identifier of the remote datapoint
   * @param remoteClient Client identifier of the remote datapoint
   * @param identifier   Datapoint identifier
   */
  public ComplexDataPointConsumer(String remoteGroup, String remoteClient, 
      List<String> identifier) {
    super(remoteGroup, remoteClient, identifier, new ComplexValue());
  }
}
