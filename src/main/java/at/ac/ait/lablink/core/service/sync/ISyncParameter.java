//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync;

import com.eclipsesource.json.JsonObject;


/**
 * Interface for simulation parameter access.
 */
public interface ISyncParameter {

  /**
   * Returns an unique identifier of the running scenario.
   *
   * @return An identifier for the scenario.
   */
  String getScenarioIdentifier();

  /**
   * Returns the client-specific configuration (as {@link JsonObject}) provided by the sync host
   * or any other configuration entity.
   *
   * @return client-specific configuration (as {@link JsonObject})
   */
  JsonObject getClientConfig();

  /**
   * Returns the mode of the simulation.
   *
   * @return either EMU for emulation mode, which is (scaled) wall-clock time, or SIM for pure
   *         (offline) simulation mode.
   */
  ELlSimulationMode getSimMode();

  /**
   * Returns the start time of the simulation.
   *
   * @return the timestamp in ms of the (virtual) simulation start time.
   */
  long getSimBeginTime();

  /**
   * Returns the end time of the simulation.
   *
   * @return the timestamp in ms of the (virtual) simulation end time.
   */
  long getSimEndTime();

  /**
   * Returns the step size of the simulation steps.
   *
   * @return the typical step size (in ms) in which the simulation progresses from start till end
   *         time.
   */
  long getStepSize();

  /**
   * Returns the scale factor of the simulation.
   *
   * @return the scale factor with which the emulation progresses (typically 1-n)
   */
  long getScaleFactor();

}