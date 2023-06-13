# enphasepowermeter
A Hubitat or SmartThings device driver for an enphase Solar Array with a local envoy controller.  Implements powermeter and pushable button capabilities. The driver will query the envoy for array statistics when it recieves a push event.  The "power" attribute
records the EIM (circuit tap) output power from the array in watts and the maxpower attribute records the highest power value encountered.  Maxpower is reset when instantaneous power goes negative at night.  Best implemented as a pushbutton device with rule manager initiating
a push event every 5 minutes.
