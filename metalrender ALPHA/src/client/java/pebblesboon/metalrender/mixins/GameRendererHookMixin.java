package pebblesboon.metalrender.mixins;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pebblesboon.metalrender.MetalRuntime;

@Mixin(GameRenderer.class)
public class GameRendererHookMixin {

    @Inject(method = "onResized", at = @At("RETURN"))
    private void metarender$onResized(int width, int height, CallbackInfo ci) {
        MetalRuntime.get().createOrResize(width, height);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void metarender$begin(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        MetalRuntime.get().beginFrame();
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void metarender$end(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        MetalRuntime.get().endFrame();
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void metarender$shutdown(CallbackInfo ci) {
        MetalRuntime.get().destroy();
    }
}

