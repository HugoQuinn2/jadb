package io.github.hugoquinn2.jadb.device.constant;

public enum ExceptionMessage {
    PERMISSION_DENIED ("Permission denied for %s: %s."),
    DEVICE_NOT_AVAILABLE ("Device not available, required state %s, <%s>."),
    FILE_NOT_FOLDER("Object File most be FOLDER <%s>."),
    NULL_POINTER("Null pointer: %s is required.");

    private final String message;

    ExceptionMessage(String command) {
        this.message = command;
    }

    public String message(String... data) {
        return String.format(this.message, (Object[]) data);
    }
}
