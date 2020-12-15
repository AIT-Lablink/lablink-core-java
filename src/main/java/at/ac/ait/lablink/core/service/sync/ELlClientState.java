//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync;

public enum ELlClientState {
  NOT_CONNECTED, REGISTERED, SIMULATING, WAITING_FOR_INIT_REPLY, WAITING_FOR_GO_REPLY,
  WAITING_FOR_STOP_REPLY
}
