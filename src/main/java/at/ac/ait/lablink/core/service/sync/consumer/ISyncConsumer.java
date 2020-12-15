//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync.consumer;

import at.ac.ait.lablink.core.service.sync.ISyncParameter;
import at.ac.ait.lablink.core.service.sync.impl.SyncHostServiceImpl;


public interface ISyncConsumer {


  /**
   * Initialize sync client with synchronization-parameters provided by the
   * {@link at.ac.ait.lablink.core.service.sync.consumer.impl.SyncClientServiceImpl}.
   *
   * @param scs The {@link at.ac.ait.lablink.core.service.sync.consumer.impl.SyncClientServiceImpl}
   *     acting as the counterpart to the
   *     {@link at.ac.ait.lablink.core.service.sync.impl.SyncHostServiceImpl} holding all required 
   *     parameters such as BeginTime, EndTime, SimMode, ScaleFactor, StepSize.
   * @return Initialization success reply to the
   *     {@link at.ac.ait.lablink.core.service.sync.impl.SyncHostServiceImpl}
   */
  boolean init(ISyncParameter scs);


  /**
   * Start simulation run either in simulation or emulation mode or process the next simulation
   * time step until the given simulation time.
   *
   * @param currentSimTime Current simulation time of the simulation step.
   * @param until Simulation time until which the simulation run should be performed
   * @param scs The {@link at.ac.ait.lablink.core.service.sync.consumer.impl.SyncClientServiceImpl}
   *     acting as the counterpart to the
   *     {@link at.ac.ait.lablink.core.service.sync.impl.SyncHostServiceImpl} holding all required 
   *     parameters such as BeginTime, EndTime, SimMode, ScaleFactor, StepSize.
   * @return the next simulation time (Unix-time in milliseconds), the simulator requests to
   *     simulate until (must be greater than the given simulate until time stamp).
   */
  long go(long currentSimTime, long until, ISyncParameter scs);


  /**
   * Stop the simulation run and unloads local data if required.
   *
   * @param scs The {@link at.ac.ait.lablink.core.service.sync.consumer.impl.SyncClientServiceImpl}
   *     acting as the counterpart to the
   *     {@link at.ac.ait.lablink.core.service.sync.impl.SyncHostServiceImpl} holding all required 
   *     parameters such as BeginTime, EndTime, SimMode, ScaleFactor, StepSize.
   * @return Stop success reply to the
   *     {@link at.ac.ait.lablink.core.service.sync.impl.SyncHostServiceImpl}
   */
  boolean stop(ISyncParameter scs);

}
