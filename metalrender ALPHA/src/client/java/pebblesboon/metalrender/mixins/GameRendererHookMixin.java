package pebblesboon.metalrender.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pebblesboon.metalrender.MetalRuntime;
import pebblesboon.metalrender.bridge.MetalBridge;

@Mixin(GameRenderer.class)
public class GameRendererHookMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onResized", at = @At("RETURN"))
    private void metarender$onResized(int width, int height, CallbackInfo ci) {
        if (!MetalRuntime.get().isAvailable()) return;
        MetalRuntime.get().createOrResize(width, height);
        MetalBridge.ctxResize(width, height);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void metarender$begin(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (!MetalRuntime.get().isAvailable()) return;
        MetalBridge.frameBegin(tickDelta);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void metarender$end(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (!MetalRuntime.get().isAvailable()) return;
        // Draw uploaded chunks each frame
        MetalBridge.frameDraw();
        MetalBridge.frameEnd();
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void metarender$shutdown(CallbackInfo ci) {
        MetalBridge.ctxDestroy();
        MetalRuntime.get().destroy();
    }

    @Inject(method = "onResized", at = @At("HEAD"))
    private void metarender$ctxInit(int width, int height, CallbackInfo ci) {
        if (!MetalRuntime.get().isAvailable()) return;
        // Ensure native ctx is created; use current window size
        Window w = client.getWindow();
        MetalBridge.ctxCreate(w.getWidth(), w.getHeight());
    }
}
