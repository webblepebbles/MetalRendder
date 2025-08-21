package com.metalrender.nativebridge;
import java.nio.ByteBuffer;
public final class MetalBackend {
    static {
        try { System.loadLibrary("metalrender"); } catch (Throwable t) {}
    }
    public static native long init(long nsWindow, boolean srgb);
    public static native void resize(long handle, int width, int height);
    public static native void render(long handle, float timeSeconds);
    public static native void destroy(long handle);
    public static native boolean supportsMeshShaders();
    public static native void uploadStaticMesh(long handle, ByteBuffer vertexData, int vertexCount, int stride);
    public static native void setCamera(long handle, float[] viewProj4x4);
}