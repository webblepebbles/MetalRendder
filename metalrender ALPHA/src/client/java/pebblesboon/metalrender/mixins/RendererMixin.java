package pebblesboon.metalrender.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pebblesboon.metalrender.MetalRenderer;

@Mixin(GameRenderer.class)
public class RendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        MetalRenderer.renderFrame();
        ci.cancel();
    }
}
