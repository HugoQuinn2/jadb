# JADB

![JADB Lib 150x150](https://github.com/user-attachments/assets/f5659d31-7d8b-49d0-869b-b1fd465af03a)

JADB is a java library that uses the official Android tools (adb), with library you can establish a USB connection with an Android device and change device properties or get information about it

## Getting started

Connect an Android device(s) to the computer and enable the USB debug of the device and get a list of connected devices 

```java
AdbController adbController = new AdbController();
List<Device> device = adbController.getDevices();
```
### Result

```java
Device (
  deviceName=emulator-5554, 
  serial=emulator-5554,
  deviceLevel=ROOT, 
  deviceState=DEVICE, 
  model=Android SDK built for x86,
  manufacturer=unknown
)
```

## General Device Data

With an already established connection, various information can be obtained from it

```java
AdbController adbController = new AdbController();
Device device = adbController.getDevices().getFirst();
DeviceData deviceData = device.getDeviceData();
```

### Result

```java
DeviceData (
  ip=255.255.255.255,
  mac=FF:FF:FF:FF:FF:FF, 
  storage=122829576, // KB
  storageUsed=25256456, // KB
  androidId=49f31805fa6516d1, 
  simContract=AT&T, 
  androidVersion=14
)
```
