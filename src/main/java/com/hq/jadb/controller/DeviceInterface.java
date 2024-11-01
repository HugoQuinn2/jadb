package com.hq.jadb.controller;

import com.hq.jadb.model.Device;
import com.hq.jadb.model.DeviceApp;
import com.hq.jadb.model.DeviceData;
import com.hq.jadb.model.File;

import java.util.List;

public interface DeviceInterface {
    //*************** Getter functions *******************//
    public String getIp();
    public String getMac();
    public String getSimContract();
    public String getAndroidId();
    public String getAndroidVersion();
    public long getStorage();
    public long getStorageUsed();
    public DeviceData getDeviceData();
    public List<File> getFilesFrom(String remotePath);
    public List<File> getFilesFrom(File file);
    public List<DeviceApp> getDeviceApps();

    //*************** Action functions *******************//
    public boolean ping(String ip);
    public String shell(String command);
    public boolean install(String pathApk);
    public boolean uninstall(String packageApp);
    public boolean uninstall(DeviceApp deviceApp);
    public boolean pull(String remotePath, String localPath);
    public boolean pull(File file, String localPath);
    public boolean push(String localPath, String remotePath);
    public boolean push(String localPath, File file);
    public void root();

}
