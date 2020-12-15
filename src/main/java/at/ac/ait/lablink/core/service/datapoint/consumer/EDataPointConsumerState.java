//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.datapoint.consumer;

/**
 * State of the datapoint consumer.
 */
public enum EDataPointConsumerState {

  /**
   * The datapoint consumer isn't registered to a datapoint service.
   */
  NOT_REGISTERED,

  /**
   * The datapoint consumer is registered to a service but not connected to its remote datapoint.
   */
  NOT_CONNECTED,

  /**
   * The datapoint consumer is initializing its state received from the remote datapoint.
   */
  INITIALIZING,

  /**
   * The datapoint consumer is connected to its remote datapoint and will receive updates.
   */
  CONNECTED,

  /**
   * The datapoint consumer is in an error mode and won't receive updates from the remote datapoint.
   *
   * <p>Usually the datapoint consumer will be in this state if the datatypes of the consumer and
   * its remote datapoint won't match.
   */
  ERROR

}
