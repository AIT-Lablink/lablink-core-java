//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//
/**
 * LowLevel communication interface of the Lablink connection core.
 *
 * <p>This package contains the implementation of the MQTT low-level communication.
 * The package provides different interfaces for the bidirectional communication of the low level
 * MQTT client with the higher communication levels.
 *
 * <p>So it is possible to substitute the low-level MQTT implementations. The first use case uses
 * MQTT as best-effort system. Therefore an already implemented MQTT client with a synchronous
 * behavior is used. If it is necessary the synchronous client can be simply replaced by an
 * asynchronous one.
 */

package at.ac.ait.lablink.core.connection.mqtt;