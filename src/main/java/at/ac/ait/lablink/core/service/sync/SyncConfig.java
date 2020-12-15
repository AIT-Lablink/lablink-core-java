//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync;

import at.ac.ait.lablink.core.service.sync.ex.SyncServiceRuntimeException;
import at.ac.ait.lablink.core.utility.Utility;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Configuration of the simulation within sync host component.
 *
 * <p>The configuration object will be read from a Json file and is stored within this class.
 */
public class SyncConfig {

  private static Logger logger = LoggerFactory.getLogger(SyncConfig.class);

  private long simBeginTime;
  private long simEndTime;
  private ELlSimulationMode simMode;
  private long simScaleFactor;
  private long simStepSize;

  private HashMap<SyncParticipant, String>
      requiredClientConfigsJson =
      new HashMap<SyncParticipant, String>();
  private HashMap<SyncParticipant, String>
      additionalClientConfigsJson =
      new HashMap<SyncParticipant, String>();

  public long getSimBeginTime() {
    return simBeginTime;
  }

  public long getSimEndTime() {
    return simEndTime;
  }

  public ELlSimulationMode getSimMode() {
    return simMode;
  }

  public long getSimScaleFactor() {
    return simScaleFactor;
  }

  public long getSimStepSize() {
    return simStepSize;
  }

  /**
   * Get the specific configuration of a simulation client.
   *
   * @param client Identifier of the client
   * @return the client's specific configuration.
   */
  public String getClientCfgsJson(SyncParticipant client) {
    String foundClientConfig = requiredClientConfigsJson.get(client);

    if (foundClientConfig == null) {
      foundClientConfig = additionalClientConfigsJson.get(client);
    }

    if (foundClientConfig == null) {
      foundClientConfig = Json.object().toString();
    }

    return foundClientConfig;
  }

  public Set<SyncParticipant> getNeededClients() {
    return requiredClientConfigsJson.keySet();
  }


  private HashMap<SyncParticipant, String> getRequiredClientConfigsJson() {
    return requiredClientConfigsJson;
  }

  private HashMap<SyncParticipant, String> getAdditionalClientConfigsJson() {
    return additionalClientConfigsJson;
  }

  /**
   * Update the sync parameter.
   *
   * <p>The sync host will call this method after every simulation step. This method can be used
   * to change parameter of the configuration during runtime.
   *
   * @param currentTime Current simulation time in milliseconds since epoch.
   */
  public void updateSyncParameter(long currentTime) {
    //TODO implement changing of sync params during simulation

    // There is the idea to change the simulation parameters during runtime. E.g., The simulation
    // starts in simulation mode until a specific simulation time was reached. Then the sync host
    // switches to emulation mode (to test real components within the lab). After the successful
    // execution the sync host can switch back to simulation mode to finish the simulation fast.

    // Another example is the dynamically changing of the simulation step size to analyze a short
    // time interval with a higher accuracy.
  }

  /**
   * Read the configuration object from a configuration file.
   *
   * @param syncCfgSrc Filename of the JSON configuration file.
   * @return the read configuration.
   */
  public static SyncConfig readSyncConfigFromFile(String syncCfgSrc) {

    if (syncCfgSrc == null || syncCfgSrc.isEmpty()) {
      throw new SyncServiceRuntimeException(
          "No sync configuration for '" + syncCfgSrc + "' provided.");
    }

    JsonValue syncConfigJson;

    try {
      File syncCfgFile;
      try {
        ClassLoader classLoader = SyncConfig.class.getClassLoader();
        syncCfgFile = new File(classLoader.getResource(syncCfgSrc).getFile());
      } catch (NullPointerException ex) {
        syncCfgFile = new File(syncCfgSrc);
      }

      syncConfigJson = Json.parse(new FileReader(syncCfgFile));

    } catch (Exception ex) {
      throw new SyncServiceRuntimeException(
          "Unreadable configuration source file '" + syncCfgSrc + "'", ex);
    }

    SyncConfig config = new SyncConfig();

    // reading sync host parameters
    JsonObject syncParamsJson = (JsonObject) syncConfigJson.asObject().get("syncParams");

    String simModeString = syncParamsJson.getString("simMode", "SIM");
    config.simMode = ELlSimulationMode.fromValue(simModeString);

    config.simScaleFactor = syncParamsJson.getLong("simScaleFactor", 1);
    config.simStepSize = syncParamsJson.getLong("simStepSize_ms", 10000);
    config.simBeginTime =
        Utility.dateStrToUnix(syncParamsJson.getString("simBeginTime", "01.01.2017 12:00:00"));
    config.simEndTime =
        Utility.dateStrToUnix(syncParamsJson.getString("simEndTime", "01.01.2017 12:10:00"));

    if (logger.isDebugEnabled()) {
      logger.debug("Read ISyncParameter: {} {} - {}, {}ms, Scale {}", config.simMode,
          Utility.unixToDateStr(config.simBeginTime),
          Utility.unixToDateStr(config.simEndTime), config.simStepSize, config.simScaleFactor);
    }

    readClientConfigs("requiredClients", config.getRequiredClientConfigsJson(),
        syncConfigJson.asObject());
    if (syncConfigJson.asObject().get("additionalClients") != null) {
      readClientConfigs("additionalClients", config.getAdditionalClientConfigsJson(),
          syncConfigJson.asObject());
    }

    return config;
  }


  private static void readClientConfigs(String clientKey, Map<SyncParticipant, String> participants,
                                        JsonObject syncConfigJson) {
    JsonArray clientCfgsArr = syncConfigJson.get(clientKey).asArray();
    for (JsonValue clv : clientCfgsArr.values()) {
      JsonObject clo = clv.asObject();
      String clientId = clo.get("clientId").asString();
      String clientGroup = clo.get("clientGroup").asString();
      JsonValue parameterJson = clo.get("Parameter");

      logger.debug("Read ClientConfig: {} {}, {}", clientGroup, clientId, parameterJson);

      if (clientId == null || clientId.isEmpty()) {
        throw new SyncServiceRuntimeException("ClientId in JSON config is null or empty.");
      }

      if (clientGroup == null || clientGroup.isEmpty()) {
        throw new SyncServiceRuntimeException("clientGroup in JSON config is null or empty.");
      }

      if (parameterJson == null) {
        //No parameter element available - create empty object
        parameterJson = Json.object();
      }

      if (!parameterJson.isObject()) {
        throw new SyncServiceRuntimeException("Parameters in Json config isn't an object.");
      }

      SyncParticipant newClient = new SyncParticipant(clientGroup, clientId);
      participants.put(newClient, parameterJson.asObject().toString(WriterConfig.MINIMAL));
    }
  }
}
