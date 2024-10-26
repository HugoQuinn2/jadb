package com.hq.jadb.controller;

import com.hq.jadb.model.Device;
import com.hq.jadb.model.DeviceApp;
import com.hq.jadb.model.DeviceData;
import com.hq.jadb.model.File;

import java.util.List;

public interface DeviceInterface {
    public DeviceData getDeviceData();
    public boolean ping(String ip);
    public boolean install(String pathApk);
    public boolean uninstall(String packageApp);
    public boolean uninstall(DeviceApp deviceApp);
    public boolean pull(String remotePath, String localPath);
    public boolean push(String localPath, String remotePath);
    public List<File> getFilesFrom(String remotePath);
    public List<File> getFilesFrom(File file);
    public void root();
    public List<DeviceApp> getDeviceApps();
}
