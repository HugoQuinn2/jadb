package io.github.hugoquinn2.jadb.adb.controller;

import io.github.hugoquinn2.jadb.device.model.Device;

import java.util.List;

public interface AdbInterface {
    public List<Device> getDevices();
}
