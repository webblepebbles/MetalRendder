package com.metalrender.sodium;
import com.metalrender.nativebridge.MetalBackend;
import com.metalrender.nativebridge.NativeMemory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeCocoa;
import java.nio.ByteBuffer;
public final class MetalRendererBackend {
    private long handle;
    private boolean initialized;
    private long startNanos;
    public void init() {
        if (initialized) return;
        long ctx = GLFW.glfwGetCurrentContext();
        long nsWindow = GLFWNativeCocoa.glfwGetCocoaWindow(ctx);
        boolean srgb = true;
        handle = MetalBackend.init(nsWindow, srgb);
        startNanos = System.nanoTime();
        uploadDemoMesh();
        initialized = true;
    }
    private void uploadDemoMesh() {
        float s = 1.0f;
        float[] tris = new float[] {
                -s, -s, 0f, 1f, 0f, 0f, 1f,
                s, -s, 0f, 0f, 1f, 0f, 1f,
                0f,  s, 0f, 0f, 0f, 1f, 1f
        };
        int stride = 7 * 4;
        ByteBuffer buf = NativeMemory.alloc(tris.length * 4);
        for (float v : tris) buf.putFloat(v);
        buf.rewind();
        MetalBackend.uploadStaticMesh(handle, buf, tris.length / 7, stride);
    }
    public void resizeIfNeeded() {
        MinecraftClient mc = MinecraftClient.getInstance();
        Window w = mc.getWindow();
        MetalBackend.resize(handle, w.getFramebufferWidth(), w.getFramebufferHeight());
    }
    public void sendCamera(float fovDegrees) {
        MinecraftClient mc = MinecraftClient.getInstance();
        float w = mc.getWindow().getFramebufferWidth();
        float h = mc.getWindow().getFramebufferHeight();
        float aspect = h == 0 ? 1f : w / h;
        float f = (float)(1.0 / Math.tan(Math.toRadians(fovDegrees) * 0.5));
        float zNear = 0.05f;
        float zFar = 1000f;
        float[] proj = new float[] {
                f/aspect,0,0,0,
                0,f,0,0,
                0,0,(zFar+zNear)/(zNear-zFar),-1,
                0,0,(2*zFar*zNear)/(zNear-zFar),0
        };
        float[] view = new float[] {
                1,0,0,0,
                0,1,0,0,
                0,0,1,0,
                0,0,-3,1
        };
        float[] m = new float[16];
        for (int r=0;r<4;r++) for (int c=0;c<4;c++) {
            m[r*4+c]=view[r*4+0]*proj[0*4+c]+view[r*4+1]*proj[1*4+c]+view[r*4+2]*proj[2*4+c]+view[r*4+3]*proj[3*4+c];
        }
        MetalBackend.setCamera(handle, m);
    }
    public void render() {
        if (!initialized) return;
        resizeIfNeeded();
        sendCamera(70f);
        float t = (System.nanoTime()-startNanos)/1_000_000_000f;
        MetalBackend.render(handle, t);
    }
    public void destroy() {
        if (!initialized) return;
        MetalBackend.destroy(handle);
        handle = 0;
        initialized = false;
    }
}