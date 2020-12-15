//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync.consumer.impl;

import at.ac.ait.lablink.core.service.sync.ELlSimulationMode;
import at.ac.ait.lablink.core.service.sync.ISyncParameter;
import at.ac.ait.lablink.core.service.sync.payloads.SyncClientConfigMessage;
import at.ac.ait.lablink.core.service.sync.payloads.SyncParamMessage;

import com.eclipsesource.json.JsonObject;

public class SyncClientConfig implements ISyncParameter {

  private SyncParamMessage syncParameter;
  private SyncClientConfigMessage clientConfig;

  public SyncClientConfig(SyncParamMessage syncParameter, SyncClientConfigMessage clientConfig) {
    this.syncParameter = syncParameter;
    this.clientConfig = clientConfig;
  }

  public void setSyncParameter(SyncParamMessage syncParameter) {
    this.syncParameter = syncParameter;
  }

  public void setClientConfig(SyncClientConfigMessage clientConfig) {
    this.clientConfig = clientConfig;
  }

  @Override
  public String getScenarioIdentifier() {
    return syncParameter.getScenarioIdentifier();
  }

  @Override
  public JsonObject getClientConfig() {
    return clientConfig.getClientConfig();
  }

  @Override
  public ELlSimulationMode getSimMode() {
    return syncParameter.getSimMode();
  }

  @Override
  public long getSimBeginTime() {
    return syncParameter.getSimBeginTime();
  }

  @Override
  public long getSimEndTime() {
    return syncParameter.getSimEndTime();
  }

  @Override
  public long getStepSize() {
    return syncParameter.getStepSize();
  }

  @Override
  public long getScaleFactor() {
    return syncParameter.getScaleFactor();
  }
}
