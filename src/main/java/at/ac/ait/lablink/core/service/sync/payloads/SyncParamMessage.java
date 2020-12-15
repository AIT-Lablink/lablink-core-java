//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync.payloads;

import at.ac.ait.lablink.core.connection.encoding.IDecoder;
import at.ac.ait.lablink.core.connection.encoding.IEncodeable;
import at.ac.ait.lablink.core.connection.encoding.IEncodeableFactory;
import at.ac.ait.lablink.core.connection.encoding.IEncoder;
import at.ac.ait.lablink.core.connection.encoding.encodeables.PayloadBase;
import at.ac.ait.lablink.core.ex.LlCoreRuntimeException;
import at.ac.ait.lablink.core.service.sync.ELlSimulationMode;

/**
 * IPayload with simulation parameters.
 */
public class SyncParamMessage extends PayloadBase {

  /**
   * Get a type string of the class.
   *
   * <p><b>This static method must be implemented by every subclass.</b>
   *
   * <p>Every class that is encodeable and is used by a decoder must have a unique string that
   * identifies this class. This type string will be transmitted during the communication and will
   * be used by a decoder for creating an empty object of the encodeable class.
   *
   * @return an unique type string of the class
   */
  public static String getClassType() {
    return "SyncParamMessage";
  }

  /**
   * Get the factory to create objects of the class.
   *
   * <p><b>This static method must be implemented by every subclass.</b>
   *
   * <p>Every class that is encodeable and is used by a decoder must have a unique factory object to
   * create empty objects of the class. This factory method will be used by the decoder to create a
   * fresh object that can be filled in with the decoded values.
   *
   * @return A factory object for creating encodeable classes
   */
  public static IEncodeableFactory getEncodeableFactory() {
    return new IEncodeableFactory() {
      @Override
      public IEncodeable createEncodeableObject() {
        return new SyncParamMessage();
      }
    };
  }

  private String scenarioIdentifier;

  private ELlSimulationMode mode = ELlSimulationMode.SIMULATION;
  private long simBeginTime;
  private long simEndTime;
  private long scaleFactor;
  private long stepSize;

  /**
   * Default constructor.
   */
  public SyncParamMessage() {
  }

  /**
   * Constructor.
   *
   * @param scenarioIdentifier Identifier of the scenario.
   * @param mode Mode of the simulation.
   * @param simBeginTime Start time of the simulation in Milliseconds since epoch.
   * @param simEndTime End time of the simulation in Milliseconds since epoch.
   * @param scaleFactor Scale factor for emulation mode.
   * @param stepSize Duration between simulation steps in Milliseconds.
   */
  public SyncParamMessage(String scenarioIdentifier, ELlSimulationMode mode, long simBeginTime,
                          long simEndTime, long scaleFactor, long stepSize) {
    this.scenarioIdentifier = scenarioIdentifier;
    this.mode = mode;
    this.simBeginTime = simBeginTime;
    this.simEndTime = simEndTime;
    this.scaleFactor = scaleFactor;
    this.stepSize = stepSize;
  }

  @Override
  public void encode(IEncoder encoder) {
    encoder.putString("scenario", scenarioIdentifier);
    encoder.putString("simMode", mode.toString());
    encoder.putLong("beginTime", simBeginTime);
    encoder.putLong("endTime", simEndTime);
    encoder.putLong("scaleFactor", scaleFactor);
    encoder.putLong("stepSize", stepSize);
  }

  @Override
  public void decode(IDecoder decoder) {
    scenarioIdentifier = decoder.getString("scenario");
    mode = ELlSimulationMode.fromValue(decoder.getString("simMode"));
    simBeginTime = decoder.getLong("beginTime");
    simEndTime = decoder.getLong("endTime");
    scaleFactor = decoder.getLong("scaleFactor");
    stepSize = decoder.getLong("stepSize");
  }

  @Override
  public String getType() {
    return SyncParamMessage.getClassType();
  }

  @Override
  public void decodingCompleted() {
    // expected
  }

  @Override
  public void validate() {

    if (this.mode == null) {
      throw new LlCoreRuntimeException("Mode in SyncParamMessage is null.");
    }

    if (this.scenarioIdentifier == null || this.scenarioIdentifier.isEmpty()) {
      throw new LlCoreRuntimeException(
          "ScenarioIdentifier in SyncParamMessage is null or empty.");
    }


  }


  @Override
  public String toString() {
    return "SyncParamMessage{" + "scenario='" + scenarioIdentifier + '\'' + ", mode='" + mode + '\''
        + ", simBeginTime=" + simBeginTime + ", simEndTime=" + simEndTime + ", scaleFactor="
        + scaleFactor + ", stepSize=" + stepSize + '}';
  }


  public String getScenarioIdentifier() {
    return scenarioIdentifier;
  }


  public ELlSimulationMode getSimMode() {
    return mode;
  }

  public long getSimBeginTime() {
    return simBeginTime;
  }

  public long getSimEndTime() {
    return simEndTime;
  }

  public long getScaleFactor() {
    return scaleFactor;
  }

  public long getStepSize() {
    return stepSize;
  }
}
