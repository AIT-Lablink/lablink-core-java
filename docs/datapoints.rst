Datapoints
==========

Package ``at.ac.ait.lablink.core.service.datapoint`` hosts values on the connection.
A datapoint has an identifier, a friendly name, a unit, a value, and a timestamp in milliseconds since 1 January 1970.
``setValue(value)`` stamps the value with the current time.
``setValue(value, timestamp)`` keeps the timestamp the caller supplies.
``getValue`` and ``getTimestamp`` return the last stored pair.
``getProps`` returns the ``DataPointProperties``.

The hosting side and the consuming side are separate services.
``IDataPointService`` (implemented by ``DataPointServiceImpl``) registers datapoints that this client owns.
``IDataPointConsumerService`` registers consumers that follow a datapoint on another client.

Hosted datapoints
-----------------

``registerDatapoint`` adds a ``DataPointGeneric`` to the service.
``unregisterDatapoint`` removes it.
``start`` begins publishing, and ``shutdown`` stops the service.
The service prefix on the connection is ``DP``.

Each datatype has a writable class and a read-only class.
A writable datapoint accepts a set request from a remote consumer.
A read-only datapoint is updated by the client that hosts it.
Measurement values are the usual case for a read-only datapoint.

.. topic:: Host classes

  :Boolean: ``BooleanDataPoint``, ``BooleanReadonlyDataPoint``
  :Double: ``DoubleDataPoint``, ``DoubleReadonlyDataPoint``
  :Long: ``LongDataPoint``, ``LongReadonlyDataPoint``
  :String: ``StringDataPoint``, ``StringReadonlyDataPoint``
  :Complex: ``ComplexDataPoint``, ``ComplexReadonlyDataPoint``

The constructor takes the identifier as a list of strings, the friendly name, and the unit.

``DoubleDeltaDataPoint`` and ``ComplexDeltaDataPoint`` wrap a datapoint of the same type.
They publish a new value when the change is greater than the configured delta.
A change inside that band is kept local.

``setNotifier`` installs an ``IDataPointNotifier``.
``valueSetNotifier`` runs when a consumer sends a new value.
During that call the host updates its state with ``setValue``.
``requestValueUpdate`` runs when a consumer asks for a fresh value, and the host answers by calling ``setValue`` again.

Consumers
---------

Package ``at.ac.ait.lablink.core.service.datapoint.consumer`` is the remote side.
A consumer is constructed with the remote group, the remote client name, and the datapoint identifier.

.. topic:: Consumer classes

  :Boolean: ``BooleanDataPointConsumer``
  :Double: ``DoubleDataPointConsumer``
  :Long: ``LongDataPointConsumer``
  :String: ``StringDataPointConsumer``
  :Complex: ``ComplexDataPointConsumer``

``registerDatapointConsumer`` attaches the consumer to ``IDataPointConsumerService``.
``waitForAllDatapointConnections`` blocks until every registered consumer has reached its remote datapoint, and raises ``DatapointServiceRuntimeException`` when the wait times out.
``getAvailableDataPoints`` lists the datapoints the service can see.
``isConnected`` reports whether the consumer service is connected.

``getValue`` and ``getTimestamp`` read the local copy.
They do not send a request.
``setValue`` sends a set request to the remote datapoint.
``requestValueUpdate`` asks the remote datapoint for a new value.
The reply arrives later through the notifier.

``EDataPointConsumerState`` is the consumer's connection state: ``NOT_REGISTERED``, ``NOT_CONNECTED``, ``INITIALIZING``, ``CONNECTED``, or ``ERROR``.
``ERROR`` is the state used when the consumer datatype and the remote datatype differ.

``IDataPointConsumerNotifier`` has two callbacks.
``valueUpdate`` runs when a new value arrives.
``stateChanged`` runs when the consumer changes state.
In a synchronized run, ``getEmulationTimestamp`` returns the emulated time of the last value.
That call requires the datapoint to be running with the sync client.
