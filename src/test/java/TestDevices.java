import io.github.hugoquinn2.jadb.adb.controller.AdbController;
import io.github.hugoquinn2.jadb.device.model.Device;
import io.github.hugoquinn2.jadb.device.model.DeviceApp;
import io.github.hugoquinn2.jadb.device.model.File;
import org.junit.Test;

import java.util.List;

public class TestDevices {
    AdbController adbController;
    @Test
    public void devices() {
        adbController = new AdbController();
        List<Device> device = adbController.getDevices();
        System.out.println(device.toString());
    }

    @Test
    public void deviceData() {
        adbController = new AdbController();
        Device device = adbController.getDevices().getFirst();
        System.out.println(device.getDeviceData());
    }

    @Test
    public void install() {
        adbController = new AdbController();
        String pathApk = "C:\\Users\\hugoq\\Downloads\\aptoide.apk";
        Device device = adbController.getDevices().getFirst();
        System.out.println(device.install(pathApk));
    }

    @Test
    public void uninstall() {
        adbController = new AdbController();
        String packageApk = "cm.aptoide.pt";
        Device device = adbController.getDevices().getFirst();
        System.out.println(device.uninstall(packageApk));
    }

    @Test
    public void uninstallByDeviceApp() {
        adbController = new AdbController();
        String packageApk = "cm.aptoide.pt";
        Device device = adbController.getDevices().getFirst();

        List<DeviceApp> deviceApps = device.getDeviceApps();
        for (DeviceApp deviceApp : deviceApps) {
            if (deviceApp.getAppPackage().contains(packageApk))
                System.out.println(device.uninstall(deviceApp));
        }
    }

    @Test
    public void getApps() {
        adbController = new AdbController();
        Device device = adbController.getDevices().getFirst();
        System.out.println(device.getDeviceApps());
    }

    @Test
    public void push() {
        adbController = new AdbController();
        Device device = adbController.getDevices().getFirst();
        String localPath = "C:\\Users\\hugoq\\Downloads\\aptoide.apk";
        String remotePath = "/storage/emulated/0/Download";

        System.out.println(device.push(localPath, remotePath));
    }

    @Test
    public void pull() {
        adbController = new AdbController();
        Device device = adbController.getDevices().getFirst();
        String localPath = "C:\\Users\\hugoq\\Documents";
        String remotePath = "/storage/emulated/0/Download/aptoide.apk";

        System.out.println(device.pull(remotePath, localPath));
    }

    @Test
    public void ping() {
        adbController = new AdbController();
        Device device = adbController.getDevices().getFirst();
        String ip = "google.com";

        System.out.println(device.ping(ip));
    }

    @Test
    public void getFilesFrom() {
        adbController = new AdbController();
        Device device = adbController.getDevices().getFirst();
        List<File> files = device.getFilesFrom("/");

        System.out.println(files);
    }

    @Test
    public void getFilesFromFileFolder() {
        adbController = new AdbController();
        Device device = adbController.getDevices().getFirst();
        List<File> files = device.getFilesFrom("/");

        System.out.println(device.getFilesFrom(files.getFirst()));
    }

    @Test
    public void shell() {
        adbController = new AdbController();
        Device device = adbController.getDevices().getFirst();

        String command = "toybox";
        System.out.println(device.shell(command));
    }
}
