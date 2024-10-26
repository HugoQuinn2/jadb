package com.hq.jadb.constant;

public enum Command {
    PING("-s %s shell ping -c 1 %s"),
    PUSH("-s %s push %s %s"),
    ROOT_VER("-s %s shell whoami"),
    ROOT("-s %s root"),
    DEVICE          ("devices"),
    WLAN0           ("-s %s shell ip addr show wlan0"),
    MODEL           ("-s %s shell getprop ro.product.model"),
    SERIAL          ("-s %s get-serialno"),
    ANDROID_VERSION ("-s %s shell getprop ro.build.version.release"),
    SIM_CONTRACT    ("-s %s shell getprop gsm.sim.operator.alpha"),
    MANUFACTURER    ("-s %s shell getprop ro.product.manufacturer"),
    STORAGE         ("-s %s shell df"),
    ANDROID_ID      ("-s %s shell settings get secure android_id"),
    DISPLAY         ("-s %s shell wm size"),
    PROCESSOR       ("-s %s shell cat /proc/cpuinfo"),
    RAM             ("-s %s shell cat /proc/meminfo"),
    CONTACTS        ("-s %s shell content query --uri content://contacts/phones/"),
    CALL_PHONE      ("-s %s shell am start -a android.intent.action.CALL -d tel:%s"),
    USER_PACKAGES        ("-s %s shell pm list packages"),
    PACKAGES_INFO   ("-s %s shell dumpsys package %s"),
    PS              ("-s %s shell ps"),
    TOP             ("-s %s shell top -n 1"),
    UNINSTALL       ("-s %s uninstall %s"),
    INSTALL         ("-s %s install %s"),
    PACK_PATH       ("-s %s shell pm path %s"),
    PULL            ("-s %s pull %s %s"),
    LS              ("-s %s shell cd %s && ls -l"),
    LS_M            ("-s %s shell ls -m %s"),
    STAT            ("-s %s shell stat -c"),
    MKDIR           ("-s %s shell mkdir %s");

    private final String command;

    Command(String command) {
        this.command = command;
    }

    public String command(String... data) {
        return String.format(this.command, (Object[]) data);
    }
}
