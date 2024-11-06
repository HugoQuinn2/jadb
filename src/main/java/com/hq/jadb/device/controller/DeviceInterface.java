package com.hq.jadb.device.controller;

import com.hq.jadb.device.model.DeviceApp;
import com.hq.jadb.device.model.DeviceData;
import com.hq.jadb.device.model.File;

import java.util.List;

public interface DeviceInterface {
    //*************** Getter functions *******************//
    String getIp();
    String getMac();
    String getSimContract();
    String getAndroidId();
    String getAndroidVersion();
    long getStorage();
    long getStorageUsed();
    DeviceData getDeviceData();
    List<File> getFilesFrom(String remotePath);
    List<File> getFilesFrom(File file);
    List<DeviceApp> getDeviceApps();

    //*************** Action functions *******************//
    boolean ping(String ip);
    String shell(String command);
    boolean install(String pathApk);
    boolean uninstall(String packageApp);
    boolean uninstall(DeviceApp deviceApp);
    boolean pull(String remotePath, String localPath);
    boolean pull(File file, String localPath);
    boolean push(String localPath, String remotePath);
    boolean push(String localPath, File file);
    void root();
}
