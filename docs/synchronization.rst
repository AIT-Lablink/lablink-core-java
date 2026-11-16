Synchronization
===============

Package ``at.ac.ait.lablink.core.service.sync`` steps several clients through one scenario.
One process runs the sync host.
Every other process that takes part implements the sync consumer and receives ``init``, ``go``, and ``stop``.

The host passes an ``ISyncConsumer`` into ``MqttCommInterfaceUtility.addClientProperties`` when the client is created.
``IDataPointService.getSyncConsumer`` returns the consumer attached to a datapoint service, so datapoint updates stay on the same clock as the scenario.

Sync host
---------

``ISyncHostService`` (implemented by ``SyncHostServiceImpl``) is the host.

``init`` reads the simulation configuration.
``start`` runs the scenario.
``shutdown`` stops the host.
``getRegisteredClients`` returns the ``SyncParticipant`` values that have joined.
A participant is a group name plus a client name.

``SyncConfig`` is the JSON document the host reads.
It holds the scenario times and, for each participant, a client-specific JSON configuration.
Required clients and additional clients are stored separately.
``getClientCfgsJson`` returns the configuration of one participant.

``ISyncHostNotifier.stateChanged`` reports the host state from ``ELlSyncHostState``: ``STOPPED``, ``INIT``, ``SIMULATING``, or ``ERROR``.

Sync consumer
-------------

A client implements ``ISyncConsumer``.

``init`` receives the scenario parameters and returns whether the client is ready.
``go`` advances the client from ``currentSimTime`` up to ``until``.
The return value is the next simulation time, in milliseconds, that this client wants to run until.
That time is greater than ``until``.
``stop`` ends the run and lets the client drop local data.
Each method receives the same ``ISyncParameter`` the host is using.

``ISyncParameter`` exposes the values every participant shares.

.. topic:: Scenario parameters

  :``getScenarioIdentifier``: identifier of the running scenario
  :``getClientConfig``: client-specific configuration as a JSON object
  :``getSimMode``: ``SIMULATION`` or ``EMULATION``
  :``getSimBeginTime``: virtual start time, in milliseconds
  :``getSimEndTime``: virtual end time, in milliseconds
  :``getStepSize``: step by which the scenario advances, in milliseconds
  :``getScaleFactor``: scale applied while the scenario is running

``ELlSimulationMode`` has two values.
``SIMULATION`` is an offline run: the host advances as soon as the participants return from ``go``.
``EMULATION`` follows wall-clock time, scaled by ``getScaleFactor``.
``fromValue`` reads the mode from a string.
A value that starts with ``sim`` is ``SIMULATION``.
Any other value is ``EMULATION``.
