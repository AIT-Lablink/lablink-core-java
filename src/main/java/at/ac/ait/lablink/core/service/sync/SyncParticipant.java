//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.service.sync;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * Simple Class representing a synchronization participant by its group name and client
 * name.
 */
public class SyncParticipant {

  @Override
  public int hashCode() {
    return new HashCodeBuilder(11, 31).append(groupName).append(clientName).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof SyncParticipant)) {
      return false;
    }
    if (obj == this) {
      return true;
    }

    SyncParticipant rhs = (SyncParticipant) obj;
    return new EqualsBuilder().append(clientName, rhs.getClientName())
        .append(groupName, rhs.getGroupName()).isEquals();
  }

  @Override
  public String toString() {
    return "[" + groupName + "/" + clientName + "]";
  }

  private String groupName;
  private String clientName;

  public SyncParticipant(String groupName, String clientName) {
    this.groupName = groupName;
    this.clientName = clientName;
  }

  public String getGroupName() {
    return groupName;
  }

  public String getClientName() {
    return clientName;
  }
}
