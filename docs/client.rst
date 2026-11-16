Client
======

Package ``at.ac.ait.lablink.core.client`` defines a Lablink client.
``LlClient`` (``at.ac.ait.lablink.core.client.impl``) is the class an application constructs.
It holds the client name, the host implementation, the registered services, and the communication interface.

A client is either a real client or a pseudo client.
A real client registers ``LlService`` instances.
A pseudo client registers ``LlServicePseudo`` instances.
Passing the wrong kind of service to ``addService`` raises ``ServiceTypeDoesNotMatchClientType``.

Constructing a client
---------------------

.. code-block:: java

   LlClient client = new LlClient(clientName,
       MqttCommInterfaceUtility.SP_ACCESS_NAME, giveShell, isPseudo);

   MqttCommInterfaceUtility.addClientProperties(client, clientDesc,
       scenarioName, groupName, clientName, propertiesUri, syncHostUri, syncConsumer);

   client.addService(service);
   client.create();
   client.init();
   client.start();

The constructor arguments are the client name, the host-implementation access name, a flag that opens the :doc:`interactive shell <shell>`, and a flag that selects a pseudo client.
An optional scope can follow those arguments.
The MQTT host uses the access name ``LL_HOST_MQTT_IMPL``, exposed as ``MqttCommInterfaceUtility.SP_ACCESS_NAME``.

``addClientProperties`` stores the fields other clients read from the yellow pages and from the connection configuration.

.. topic:: Client properties

  :*Description*: short text stored as ``PROP_YELLOW_PAGE_CLIENT_DESCRIPTION``
  :*Client name*: ``PROP_MQTT_CLIENT_NAME``
  :*Group name*: ``PROP_MQTT_CLIENT_GROUP_NAME``
  :*Scenario name*: ``PROP_MQTT_CLIENT_APP_NAME``
  :*Connection properties URI*: ``PROP_MQTT_CLIENT_APP_PROPERTIES_URI``
  :*Sync-host properties URI*: ``PROP_MQTT_CLIENT_SYNC_HOST_PROPERTIES_URI``
  :*Sync consumer*: optional ``ISyncConsumer``, stored as an advanced property

Both URIs may contain ``$ENV$`` placeholders.
``Utility.parseWithEnvironmentVariable`` replaces each placeholder with the value of that environment variable before the URI is stored.

Lifecycle
---------

``create``, ``init``, ``start``, and ``shutdown`` drive the client state machine.
``getState`` returns the current ``ELlClientStates`` value.

.. topic:: States

  :``LABLINK_CLIENT_INTERFACE_STATE_NOTINSTANTIATED``: the client exists and has not been created
  :``LABLINK_CLIENT_INTERFACE_STATE_INSTANTIATED``: ``create`` has built the communication interface
  :``LABLINK_CLIENT_INTERFACE_STATE_INITIALIZED``: ``init`` has loaded the configuration and the services
  :``LABLINK_CLIENT_INTERFACE_STATE_STARTED``: ``start`` has opened the connection
  :``LABLINK_CLIENT_INTERFACE_STATE_SHUTDOWN``: ``shutdown`` has closed the client

``stop`` is part of ``ILlClientCommInterface``, next to ``start`` and ``shutdown``.
The state machine itself moves on ``create``, ``init``, ``start``, and ``shutdown``.

After ``start``, the client can read and write service values through ``getImplementedServices``, ``setServiceValue``, and the typed getters such as ``getServiceValueString``.
A service that was never registered raises ``ServiceIsNotRegisteredWithClientException``.
Reading a service with the wrong datatype raises ``InvalidCastForServiceValueException``.
