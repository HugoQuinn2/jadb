package io.github.jadb.device.utils;

import io.github.jadb.device.constant.Command;
import io.github.jadb.device.model.Device;
import io.github.jadb.device.model.DeviceApp;
import io.github.jadb.adb.service.AdbService;

import java.util.List;

public class DeviceUtils {
    private final AdbService adbService;

    public DeviceUtils() {
        this.adbService = new AdbService();
    }

    public DeviceApp createDeviceApp(Device device , String pack) {
        String packageName = Parsing.extractPackage(pack);
        if (packageName != null) {
            String baseApk = getBaseApk(device, packageName);
            return DeviceApp
                    .builder()
                    .appPackage(packageName)
                    .baseApk(baseApk)
                    .appName(Parsing.extractNameFromBaseApk(baseApk))
                    .build();
        }

        return null;
    }

    public String getBaseApk(Device device,String pack) {
        List<String> command = Parsing.buildCommand(Command.PACK_PATH.command(device.getDeviceName(), pack));
        String response = adbService.executeCommand(command);

        String[] baseApk = response.split(":");
        if (baseApk.length == 2)
            return baseApk[1];

        return null;
    }

    public List<String> lsM(Device device,String from) {
        List<String> command = Parsing.buildCommand(Command.LS_M.command(device.getDeviceName(), from));
        return List.of(adbService.executeCommand(command)
                .replaceAll("\n", "")
                .replaceAll("\r", "")
                .replace(" ", "")
                .split(","));
    }
}
