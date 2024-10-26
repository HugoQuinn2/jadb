package com.hq.jadb.controller;

import com.hq.jadb.config.AdbConfig;
import com.hq.jadb.constant.Command;
import com.hq.jadb.constant.DeviceLevel;
import com.hq.jadb.constant.DeviceState;
import com.hq.jadb.model.Device;
import com.hq.jadb.model.DeviceApp;
import com.hq.jadb.model.DeviceData;
import com.hq.jadb.model.File;
import com.hq.jadb.service.AdbService;
import com.hq.jadb.util.Parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class DeviceController implements DeviceInterface{
    private final AdbService adbService;
    protected Device device;

    public DeviceController(Device device) {
        adbService = new AdbService();
        this.device = device;
    }

    @Override
    public DeviceData getDeviceData() {
        String responseStorage = adbService.executeCommand(Parsing.buildCommand(Command.STORAGE.command(device.getDeviceName())));
        String responseWLAN0 = adbService.executeCommand(Parsing.buildCommand(Command.WLAN0.command(device.getDeviceName())));

        return DeviceData
                .builder()
                .ip(Parsing.extractIp(responseWLAN0))
                .mac(Parsing.extractMAC(responseWLAN0))
                .androidVersion(adbService.executeCommand(
                        Parsing.buildCommand(Command.ANDROID_VERSION.command(device.getDeviceName()))))
                .simContract(adbService.executeCommand(
                        Parsing.buildCommand(Command.SIM_CONTRACT.command(device.getDeviceName()))))
                .androidId(adbService.executeCommand(
                        Parsing.buildCommand(Command.ANDROID_ID.command(device.getDeviceName()))))
                .storage(Parsing.extractStorage(responseStorage))
                .storageUsed(Parsing.extractStorageUsed(responseStorage))
                .build();
    }

    @Override
    public boolean ping(String ip) {
        List<String> command = Parsing.buildCommand(Command.PING.command(device.getDeviceName(), ip));
        String response = adbService.executeCommand(command);
        return !response.contains("100% packet loss");
    }

    @Override
    public boolean install(String pathApk) {
        List<String> command = Parsing.buildCommand(Command.INSTALL.command(device.getDeviceName(), pathApk));
        String response = adbService.executeCommand(command);
        return response.contains("Performing Streamed Install\nSuccess\n");
    }

    @Override
    public boolean uninstall(String packageApp) {
        List<String> command = Parsing.buildCommand(Command.UNINSTALL.command(device.getDeviceName(), packageApp));
        String response = adbService.executeCommand(command);
        return response.contains("Success");
    }

    @Override
    public List<DeviceApp> getDeviceApps() {
        List<String> commandPackage = Parsing.buildCommand(Command.USER_PACKAGES.command(device.getDeviceName()));
        String responsePackages = adbService.executeCommand(commandPackage);
        List<DeviceApp> deviceApps = new ArrayList<>();

        List<String> packages = List.of(responsePackages.split("\n"));

        List<Callable<DeviceApp>> allTasks = new ArrayList<>();

        for (String pack : packages) {
            allTasks.add(() -> createDeviceApp(device, pack));
        }

        ExecutorService executor = Executors.newFixedThreadPool(allTasks.size());
        try {


            for (int i = 0 ; i < allTasks.size(); i += AdbConfig.maxThread) {
                List<Callable<DeviceApp>> batchTasks = allTasks.subList(i, Math.min(i + AdbConfig.maxThread, allTasks.size()));
                List<Future<DeviceApp>> futures = executor.invokeAll(batchTasks);

                for (Future<DeviceApp> future : futures) {
                    deviceApps.add(future.get());
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            executor.shutdown();
        }

        return deviceApps;
    }

//    @Override
//    public void getApps(Device device) {
//        List<String> commandPackage = Parsing.buildCommand(Command.USER_PACKAGES.command(device.getDeviceName()));
//        String responsePackages = adbService.executeCommand(commandPackage);
//        List<DeviceApp> deviceApps = new ArrayList<>();
//
//        List<String> packages = List.of(responsePackages.split("\n"));
//        List<Callable<DeviceApp>> ioTasks = new ArrayList<>();
//
//        for (String pack : packages) {
//            ioTasks.add(() -> createDeviceApp(device, pack));
//        }
//
//        try {
//            ExecutorService executor = Executors.newFixedThreadPool(ioTasks.size());
//            List<Future<DeviceApp>> futures = executor.invokeAll(ioTasks);
//
//            for (Future<DeviceApp> future : futures) {
//                deviceApps.add(future.get());
//            }
//        } catch (Exception e) {
//            System.err.println(e);
//        }
//
//        device.setDeviceApps(deviceApps);
//    }

    @Override
    public boolean pull(String remotePath, String localPath) {
        List<String> command = Parsing.buildCommand(Command.PULL.command(device.getDeviceName(), remotePath, localPath));
        String response = adbService.executeCommand(command);
        return response.contains("1 file pulled");
    }

    @Override
    public boolean push(String localPath, String remotePath) {
        List<String> command = Parsing.buildCommand(Command.PUSH.command(device.getDeviceName(), localPath, remotePath));
        String response = adbService.executeCommand(command);
        return response.contains("1 file pushed");
    }

    @Override
    public List<File> getFiles(String path) {
        return List.of();
    }

    @Override
    public void root() {
        if (device.getDeviceState() != DeviceState.DEVICE)
            return;

        List<String> command = Parsing.buildCommand(Command.ROOT.command(device.getDeviceName()));
        adbService.executeCommand(command);

        command = Parsing.buildCommand(Command.ROOT_VER.command(device.getDeviceName()));
        String response = adbService.executeCommand(command);

        if (response.contains("root"))
            device.setDeviceLevel(DeviceLevel.ROOT);
    }

    @Override
    public boolean uninstall(DeviceApp deviceApp) {
        return uninstall(deviceApp.getAppPackage());
    }

    private DeviceApp createDeviceApp(Device device ,String pack) {
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

    private String getBaseApk(Device device,String pack) {
        List<String> command = Parsing.buildCommand(Command.PACK_PATH.command(device.getDeviceName(), pack));
        String response = adbService.executeCommand(command);

        String[] baseApk = response.split(":");
        if (baseApk.length == 2)
            return baseApk[1];

        return null;
    }

}
