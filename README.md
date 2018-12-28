# BACNet Binding for openHAB

This binding provides basic access to BACNet devices for openHAB. This binding is based on [bacnet4j-wrapper](https://github.com/Code-House/bacnet4j-wrapper) and limited to it's basic abilities such reading and writing properties. Keep in mind that this is work in progress and even basic features can be broken.

## Installation

Drop the .jar file from the plugins/ folder in your openHAB `addons` directory and restart your server.

## Configure items

Each BACNet endpoint is identified by a device instance ID, an object type and an object instance ID. The configuration your items file looks as follows:

`... {bacnet="key1=value1,key2=value2,..."}`

e.g.

`Switch reflectors_west_2 "Reflectors West Center" <reflector> (Reflector) {bacnet="device=701105,type=binaryValue,id=3"}`

The following keys are available:

* `device` is the instance ID (integer) of the device as configured on your local network (it is *not* the IP address of the device)
* `type` is the object type. Available types are  `analogInput`, `analogOutput`, `analogValue`, `binaryInput`, `binaryOutput`, `binaryValue`, `multiStateInput`, `multiStateOutput`, `multiStateValue`
* `id`is the instance ID (integer) of the object you want to tie to this openHAB item
* `address` (optional) if you know device ip - must be in `x.y.z.w:port` format
* `networkNumber` (optional) network number if different than default
* `refreshInterval` (optional) periods between property read requests

All these properties are coming from bacnet standard. If you don't know yet device id or available properties please contact your device manufacturer and ask for appropriate documentation.

## Configuration options
This binding have few basic options. Bold items are mandatory
* **broadcast** - broadcast address used to discover devices
* localBindAddress (default 0.0.0.0) - bind ip for local device, be aware that in some cases bacnet4j (used under the hood) doesn't work when this option is specified
* port (default 47808)
* localNetworkNumber (default 0) - bacnet network number
* localDeviceId (default 1339) - device id used to identify openhab in bacnet network
* refreshInterval (default 30000) - milliseconds between polling values for configured items
* discoveryTimeout (default 30000) - specifies how many milliseconds openhab will listen for whois responses from devices. *Note* binding start will be delayed for `discoveryTimeout`.


## How does it work?

The binding uses BACNet/IP and sends out a broadcast discovery command on startup. All devices on the local network responding to the broadcast become available to the binding. The binding will continuously update values for all objects that are configured in your item files by issuing read property commands via BACNet. Sending commands/status updates will result in write property commands.

### Development

The item types "dimmer", "number" and "switch" have been tested. As said earlier this binding is still in it's early development stage and might need many tweaks to work reliably in all cases! Don't hesitate to report issues in this github project or ask on [openhab community forum](http://community.openhab.org). See also our [contributing rules](CONTRIBUTING.md)