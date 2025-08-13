package pebblesboon.metalrender.bridge;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public final class MetalBridge {
    private MetalBridge() {}


    public static native void ctxCreate(int width, int height);
    public static native void ctxResize(int width, int height);
    public static native void ctxDestroy();

    public static native void frameBegin(float partialTicks);
    public static native void frameDraw();
    public static native void frameEnd();

    public static native void setViewProj(float[] view, float[] proj);
    public static native void uploadChunkMesh(
            long id,
            ByteBuffer vertexData,
            int vertexStride,
            int vertexCount,
            ByteBuffer indexData,
            int indexCount);

    public static native void deleteChunkMesh(long id);
    public static ByteBuffer directCopy(ByteBuffer src) {
        if (src == null) return null;
        if (src.isDirect()) return src;
        ByteBuffer dst = MemoryUtil.memAlloc(src.remaining());
        int pos = src.position();
        dst.put(src).flip();
        src.position(pos);
        return dst;
    }
}
