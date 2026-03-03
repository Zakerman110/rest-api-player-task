package utils;

public class LogCollector {

    private static final ThreadLocal<StringBuilder> LOGS = ThreadLocal.withInitial(StringBuilder::new);

    public static void append(String message) {
        LOGS.get().append(message).append("\n");
    }

    public static String getLogs() {
        return LOGS.get().toString();
    }

    public static void clear() {
        LOGS.remove();
    }
}
