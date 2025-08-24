package com.metalrender.nativebridge;
import java.nio.ByteBuffer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class MetalBackend {
    static {
        try {
            String libName = "libmetalrender.dylib";
            String resourcePath = "/natives/" + libName;
            File temp = File.createTempFile("metalrender", ".dylib");
            temp.deleteOnExit();
            try (InputStream in = MetalBackend.class.getResourceAsStream(resourcePath)) {
                if (in == null) {
                    throw new UnsatisfiedLinkError("Native library not found in JAR: " + resourcePath);
                }
                Files.copy(in, temp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            System.load(temp.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load MetalRender native library", e);
        }
    }


    public static native long init(long nsWindow, boolean srgb);
    public static native void resize(long handle, int width, int height);
    public static native void render(long handle, float timeSeconds);
    public static native void destroy(long handle);
    public static native boolean supportsMeshShaders();
    public static native void uploadStaticMesh(long handle, ByteBuffer vertexData, int vertexCount, int stride);
    public static native void setCamera(long handle, float[] viewProj4x4);
}