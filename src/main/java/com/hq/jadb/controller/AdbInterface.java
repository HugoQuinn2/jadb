package com.hq.jadb.controller;

import com.hq.jadb.model.Device;
import com.hq.jadb.model.DeviceApp;
import com.hq.jadb.model.DeviceData;
import com.hq.jadb.model.File;

import java.util.List;

public interface AdbInterface {
    public List<Device> getDevices();
}
