package io.github.hugoquinn2.jadb.device.constant;

public enum Command {
    PING("ping -c 1 %s"),
    PUSH("-s %s push %s %s"),
    ROOT_VER("-s %s shell whoami"),
    ROOT("-s %s root"),
    DEVICE          ("devices"),
    WLAN0           ("ip addr show wlan0"),
    MODEL           ("getprop ro.product.model"),
    SERIAL          ("-s %s get-serialno"),
    ANDROID_VERSION ("getprop ro.build.version.release"),
    SIM_CONTRACT    ("getprop gsm.sim.operator.alpha"),
    MANUFACTURER    ("getprop ro.product.manufacturer"),
    STORAGE         ("df"),
    ANDROID_ID      ("settings get secure android_id"),
    DISPLAY         ("wm size"),
    PROCESSOR       ("cat /proc/cpuinfo"),
    RAM             ("cat /proc/meminfo"),
    CONTACTS        ("content query --uri content://contacts/phones/"),
    CALL_PHONE      ("am start -a android.intent.action.CALL -d tel:%s"),
    USER_PACKAGES   ("-pm list packages"),
    PACKAGES_INFO   ("dumpsys package %s"),
    PS              ("ps"),
    TOP             ("top -n 1"),
    UNINSTALL       ("uninstall %s"),
    INSTALL         ("install %s"),
    PACK_PATH       ("pm path %s"),
    PULL            ("-s %s pull %s %s"),
    LS              ("cd %s && ls -l"),
    LS_M            ("ls -m %s"),
    STAT            ("stat -c"),
    MKDIR           ("mkdir %s");

    private final String command;

    Command(String command) {
        this.command = command;
    }

    public String command(String... data) {
        return String.format(this.command, (Object[]) data);
    }
}
