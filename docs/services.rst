Services
========

Package ``at.ac.ait.lablink.core.service`` is the application-facing value API.
A service is a named value of one datatype.
The client that owns the service subclasses one of the typed classes and implements ``get`` and ``set``.
Other clients see that value through the MQTT host.

``LlService<T>`` stores the current value, a read-only flag, a property map, and a list of state-change notifiers.
``setCurState`` replaces the value and then calls every notifier.
``getCurState`` returns the value last stored.

Datatypes
---------

``ELlServiceDataTypes`` names the six datatypes.
Each datatype has a service class and a pseudo-service class.

.. topic:: Typed services

  :``DOUBLE``: ``LlServiceDouble``, ``LlServicePseudoDouble``
  :``LONG``: ``LlServiceLong``, ``LlServicePseudoLong``
  :``STRING``: ``LlServiceString``, ``LlServicePseudoString``
  :``BOOLEAN``: ``LlServiceBoolean``, ``LlServicePseudoBoolean``
  :``COMPLEX``: ``LlServiceComplex``, ``LlServicePseudoComplex``
  :``OBJECT``: ``LlServiceObject``, ``LlServicePseudoObject``

A complex value is an ``at.ac.ait.lablink.core.service.types.Complex`` with a real part and an imaginary part.
A pseudo service (``LlServicePseudo``) is the service a pseudo client registers.
It keeps the same datatype, properties, and notifiers as ``LlService``.

A service constructed with ``readonly`` set to ``true`` is published as a read-only value.
The MQTT host maps that flag onto a read-only datapoint, so remote clients can read the value and the hosting client is the one that updates it.
The default constructors create a read/write service.
The name can be set with ``setName`` or passed to the constructor.
A constructor that omits the name generates one.

MQTT properties
---------------

``MqttCommInterfaceUtility.addDataPointProperties`` attaches the four properties the yellow pages and the datapoint layer use.

.. topic:: Service properties

  :``PROP_MQTT_DP_NAME``: datapoint name
  :``PROP_MQTT_DP_DESCRIPTION``: description
  :``PROP_MQTT_DP_IDENTIFIER``: datapoint identifier
  :``PROP_MQTT_DP_UNIT``: unit string

State changes
-------------

``addStateChangeNotifier`` registers an ``IServiceStateChangeNotifier``.
``stateChanged(service, oldVal, newVal)`` runs after every ``setCurState``, including a set that arrives from another client.

A minimal string service looks like this:

.. code-block:: java

   class HelloWorldData extends LlServiceString {
     @Override
     public String get() {
       return this.getCurState();
     }

     @Override
     public boolean set(String newval) {
       this.setCurState(newval);
       return true;
     }
   }
