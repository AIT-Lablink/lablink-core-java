//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync;

/**
 * Simulation mode of the Sync.
 */
public enum ELlSimulationMode {
  SIMULATION, EMULATION;

  /**
   * Read the simulation mode from string.
   * @param value String representation of the simulation mode.
   * @return the simulation mode.
   */
  public static ELlSimulationMode fromValue(String value) {

    value = value.toLowerCase();
    if (value.startsWith("sim")) {
      return SIMULATION;
    } else {
      return EMULATION;
    }
  }
}
