package pebblesboon.metalrender;

final class Platform {
    private static final String OS = System.getProperty("os.name", "").toLowerCase();
    static boolean isMac() { return OS.contains("mac"); }
    private Platform() {}
}
