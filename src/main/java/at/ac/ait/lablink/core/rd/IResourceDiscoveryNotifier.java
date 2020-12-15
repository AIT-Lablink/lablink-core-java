//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core.rd;

/**
 * Interface for Lablink resource discovery notifiers.
 */
public interface IResourceDiscoveryNotifier {

  /**
   * Notifier to be executed whenever a reply.
   *
   * @param rdpacket the rdpacket
   */
  public void onReply(ResourceMessage rdpacket);

}
