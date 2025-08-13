package pebblesboon.metalrender;

public class MetalRenderer {

    static {
        MetalNativeLoader.load();
    }

    public static void init() {
        MetalLogger.info("Initializing Metal renderer via JNI...");
        initMetal();
    }

    public static void renderFrame() {
        renderMetalFrame();
    }

    public static void shutdown() {
        shutdownMetal();
    }

    // JNI method declarations
    private static native void initMetal();
    private static native void renderMetalFrame();
    private static native void shutdownMetal();
}
