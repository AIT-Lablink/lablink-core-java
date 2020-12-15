//
// Copyright (c) AIT Austrian Institute of Technology GmbH.
// Distributed under the terms of the Modified BSD License.
//
/**
 * Specification of dispatching classes used to build a 
 * dispatching tree.
 *
 * <p>The dispatcher is used for subscribing to and
 * unsubscribing from MQTT topics at the MQTT broker. It is
 * also used to store callback methods. An incoming message
 * will be dispatched and the appropriate callback methods
 * will be called to inform the application.
 */

package at.ac.ait.lablink.core.connection.dispatching;