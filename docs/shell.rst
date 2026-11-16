Interactive shell
=================

Package ``at.ac.ait.lablink.core.is`` is a console on a running ``LlClient``.
The client opens it when the constructor is called with ``giveShell`` set to ``true``.
The prompt is ``llclient``.
Commands are implemented on ``LlClientIShell`` and dispatched by the cliche shell.

``set-srvc-name`` (``ssn``) stores a service name for the commands that do not take one.
Pass ``null`` to clear the stored name.
``get-srvc-val`` (``fgsv``) reads the value of that stored service.

Commands
--------

Each command below prints its result on the console.
A name that is not registered produces ``ERROR: A service with this name does not exists.``

.. topic:: Reading the client

  :``get-name`` (``gn``): client name
  :``get-desc`` (``gd``): yellow-page description
  :``get-prot`` (``gp``): host-implementation access name, ``LL_HOST_MQTT_IMPL`` for the MQTT host
  :``get-ypage`` (``gyp``): yellow pages as JSON

.. topic:: Reading a service

  :``list-srvc`` (``ls``): registered services, with datatype and current value
  :``list-srvc-prop`` (``lsp``): properties of the registered services
  :``get-srvc-val`` (``gsv``): current value of the named service
  :``get-srvc-type`` (``gst``): datatype of the named service
  :``list-srvc-psudo`` (``lps``): services registered on a pseudo client
  :``list-psudo-srvc-prop`` (``lpsp``): properties of those pseudo services

.. topic:: Writing a service

  :``set-srvc-val-dbl`` (``svd``): set a double
  :``new-set-srvc-val-dbl`` (``sd``): set a double through the implemented service
  :``set-srvc-val-lng`` (``svl``): set a long
  :``set-srvc-val-bol`` (``svb``): set a boolean
  :``set-srvc-val-str`` (``svs``): set a string
  :``set-srvc-val-cpx`` (``svc``): set a complex value from a real part and an imaginary part

A set command prints ``Success`` when the service accepts the value, and ``Failed`` when it rejects it.
The complex command takes the service name, the real part, and the imaginary part, in that order.
