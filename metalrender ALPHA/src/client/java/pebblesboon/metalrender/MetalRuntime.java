package pebblesboon.metalrender;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

public final class MetalRuntime {
    private static final MetalRuntime INSTANCE = new MetalRuntime();
    public static MetalRuntime get() { return INSTANCE; }

    private boolean available;
    private long handle; // native MRRenderer*

    public void bootstrap() {
        if (!Platform.isMac()) {
            MetalRenderer.LOG.info("Non-macOS platform — Metal disabled");
            available = false;
            return;
        }
        try {
            String libName = "libmetarender_native.dylib";
            InputStream in = MetalRuntime.class.getClassLoader().getResourceAsStream("natives/" + libName);
            if (in == null) {
                MetalRenderer.LOG.error("Native library missing — build :buildNative first");
                available = false; return;
            }
            File tmp = new File(System.getProperty("java.io.tmpdir"), "metarender-" + UUID.randomUUID() + ".dylib");
            Files.copy(in, tmp.toPath());
            System.load(tmp.getAbsolutePath());
            available = nativeIsSupported();
            MetalRenderer.LOG.info("Metal native available = {}", available);
        } catch (IOException e) {
            MetalRenderer.LOG.error("Failed to load Metal native", e);
            available = false;
        }
    }

    public boolean isAvailable() { return available; }

    public void createOrResize(int w, int h) {
        if (!available) return;
        if (handle == 0) handle = nativeCreate(w, h);
        else nativeResize(handle, w, h);
    }

    public void beginFrame() { if (available && handle != 0) nativeBeginFrame(handle); }
    public void endFrame()   { if (available && handle != 0) nativeEndFrame(handle); }
    public void destroy()    { if (available && handle != 0) { nativeDestroy(handle); handle = 0; } }

    /** When implemented native-side, returns an IOSurfaceID to be bound to a GL texture. */
    public long acquireIOSurfaceId() { return (available && handle != 0) ? nativeGetIOSurfaceId(handle) : 0; }

    // JNI
    private static native boolean nativeIsSupported();
    private static native long    nativeCreate(int width, int height);
    private static native void    nativeResize(long handle, int width, int height);
    private static native void    nativeBeginFrame(long handle);
    private static native void    nativeEndFrame(long handle);
    private static native void    nativeDestroy(long handle);
    private static native long    nativeGetIOSurfaceId(long handle);
}
