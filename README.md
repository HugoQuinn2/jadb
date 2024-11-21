<p align="center">
  <a href="https://github.com/HugoQuinn2/jadb">
    <img src="https://github.com/user-attachments/assets/f5659d31-7d8b-49d0-869b-b1fd465af03a" alt="Logo">
  </a>
</p>

<p align="center">
JADB is a java library that uses the official <a href="https://developer.android.com/tools/adb?hl=es-419"> Android tools (adb) </a>, with library you can establish a USB connection with an Android device and change device properties or get information about it.
</p>

## Maven

```xml
<dependency>
  <groupId>com.hq</groupId>
  <artifactId>jadb</artifactId>
</dependency>
```

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
## Special Functions

| Function        | Required                                | Retorned           | Description                                                             | 
| --------------- | --------------------------------------- | ------------------ | ----------------------------------------------------------------------- |
| ping()          | String (IP)                             | Boolean            | Make a ping from device and verify connection                           |
| install()       | String (pathApk)                        | Boolean            | Install an Andrid app (.apk)                                            |
| uninstall()     | String (packageApp) or DeviceApp        | Boolean            | Uninstall an Android app with package app or DeviceApp object           |
| pull()          | String (remotePath), String (localPath) | Boolean            | Pull a remote file to a local path                                      |
| push()          | String (localPath), String (remotePath) | Boolean            | Push a local file to a remote path                                      |
| getFilesFrom()  | String (remotePath) or File (FOLDER)    | List of File       | Get a list of files from remote path or from a object File type FOLDER  |
| getDeviceApps() | Null                                    | List of DeviceApp  | Get a list of apps (appName, appPackage & baseApk)                      |
