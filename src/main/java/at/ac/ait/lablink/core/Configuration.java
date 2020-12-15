//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//

package at.ac.ait.lablink.core;

import at.ac.ait.lablink.core.meta.Version;
import at.ac.ait.lablink.core.utility.Utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Lablink Configuration.
 */
public final class Configuration {

  /** The Constant LABLINK_AIT. */
  public static final String LABLINK_AIT = "at.ac.ait";

  /** The Constant VERSION. */
  public static final String VERSION = Version.getVersion();

  /** The Constant LABLINK_RD_SEPERATOR. */
  public static final String LABLINK_RD_SEPERATOR = ".";

  /** The logger. */
  private static Logger logger = LogManager.getLogger(Utility.class.getCanonicalName());

  /**
   * This constant specifies the multicast group that will be used by all RD services. The value for
   * this constant is assumed to a valid multicast group IPv4 address. The valid IPv4 full range of
   * multicast addresses is from <code>224.0.0.0</code> to <code>239.255.255.255</code>. In any
   * case, range <code>224.0.0.0</code> through <code>224.0.0.255</code> is reserved for local
   * purposes (as administrative and maintenance tasks) and datagrams destined to them are never
   * forwarded by multicast routers. Similarly, the range <code>239.0.0.0</code> to
   * <code>239.255.255.255</code> has been reserved for "administrative scoping". All this special
   * multicast groups are regularly published in the "Assigned Numbers" RFC. To show the joined
   * groups us <code>netsh interface ip show joins</code> (in Windows).
   */
  public static final String RESOURCE_DISCOVERY_GROUP_IPV4 = "224.3.224.224";

  /** This constant specifies the multicast port that will be used by all RD services. */
  public static final int RESOURCE_DISCOVERY_GROUP_PORT = 65432;

  /** The Constant TRANSPORT_GROUP_MANAGEMENT_DEMON_PORT. */
  public static final int TRANSPORT_GROUP_MANAGEMENT_DEMON_PORT = 64432;

  /** The Constant TRANSPORT_DE_UPDATE_DEMON_PORT. */
  public static final int TRANSPORT_DE_UPDATE_DEMON_PORT = 63432;

  /** The Constant TRANSPORT_SYNC_DEMON_PORT. */
  public static final int TRANSPORT_SYNC_DEMON_PORT = 62432;

  /** The Constant TRANSPORT_SYNC_DE_DEMON_PORT. */
  public static final int TRANSPORT_SYNC_DE_DEMON_PORT = 61432;

  /**
   * The Constant specifies the Time-To-Live (TTL), which determines the number of multicast-enabled
   * router hops a sent packet will be able to traverse before it is discarded. The valid range of
   * this constant is 0 to 255. 0 Are restricted to the same host, 1 - Are restricted to the same
   * subnet, 32 - Are restricted to the same site, 64 - Are restricted to the same region, 128 - Are
   * restricted to the same continent, and 255 - Are unrestricted in scope
   */
  public static final int RESOURCE_DISCOVERY_TTL_SCOPE = 10;

  /** The Constant RESOURCE_DISCOVERY_ENCODING_TYPE_JSON. */
  public static final String RESOURCE_DISCOVERY_ENCODING_TYPE_JSON = "JSON";

  /** The Constant RESOURCE_DISCOVERY_ENCODING_TYPE_BSON. */
  public static final String RESOURCE_DISCOVERY_ENCODING_TYPE_BSON = "BSON";

  /** The Constant RESOURCE_DISCOVERY_ENCODING_USE. */
  public static final String RESOURCE_DISCOVERY_ENCODING_USE =
      RESOURCE_DISCOVERY_ENCODING_TYPE_JSON;

  /**
   * Applies to verb-base resource discovery. This constant specifies the verb that will be used as
   * the command for asking for the advertisement to the server.
   */
  public static final String RESOURCE_DISCOVERY_ADVERTISE_VERB = "ADVERTISE";

  /**
   * Applies to verb-base resource discovery. This constant specifies the verb that will be used as
   * the command for stopping advertisement by the server.
   */
  public static final String RESOURCE_DISCOVERY_END_VERB = "END";

  /**
   * How long the resource discover should wait for a single reply, before having a timeout. In
   * fact, this value is used a the timeout for the socket. The value for this constant is assumed
   * to be in milliseconds.
   */
  public static final int RESOURCE_DISCOVERY_SOCKET_TIMEOUT_MS = 50000; // milliseconds

  /**
   * The pause between the advertisements by discovery server, running in clients. The value for
   * this constant is assumed to be in milliseconds.
   */
  public static final int RESOURCE_DISCOVERY_PERIODIC_ADVERTISE_PAUSE_MS = 50000; // milliseconds

  /**
   * The time that the resource discoverer runs in one session.The value for this constant is
   * assumed to be in milliseconds (1 minute = 60000ms).
   */
  public static final int RESOURCE_DISCOVERY_ADVERTISE_WAIT_MS = 500000; // milliseconds

  /**
   * The size of the buffer that holds the JSON replies from the clients. The value for this
   * constant is assumed to be in bytes.
   */
  public static final int RESOURCE_DISCOVERY_BUFFER_SIZE = 10240; // milliseconds

  /** The Constant RESOURCE_DISCOVERY_VERSION. */
  public static final String RESOURCE_DISCOVERY_VERSION = "1.0.1";

}
