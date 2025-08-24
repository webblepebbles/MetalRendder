package com.metalrender.nativebridge;

import java.nio.ByteBuffer;
import net.minecraft.client.MinecraftClient;

public class MetalBackend {
    static {
        System.loadLibrary("metalrender");
    }

    public static long init(long windowHandle, boolean someFlag) {
        if (!MetalHardwareChecker.isCompatible()) {
            System.out.println("[MetalRender] Skipping initialization: incompatible hardware");
            MinecraftClient.getInstance().execute(() -> MetalHardwareChecker.showIncompatibleScreen());
            return 0;
        }
        return initNative(windowHandle, someFlag);
    }

    private static native long initNative(long windowHandle, boolean someFlag);
    public static native void uploadStaticMesh(long handle, ByteBuffer vertexData, int vertexCount, int stride);
    public static native void resize(long handle, int width, int height);
    public static native void setCamera(long handle, float[] viewProj4x4);
    public static native void render(long handle, float timeSeconds);
    public static native void destroy(long handle);
    public static native boolean supportsMeshShaders();
}