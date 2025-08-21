package com.metalrender.sodium.mixin;
import com.metalrender.MetalRenderMod;
import com.metalrender.sodium.MetalRendererBackend;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(SodiumWorldRenderer.class)
public class SodiumRendererMixin {
    @Shadow private int viewportWidth;
    @Shadow private int viewportHeight;
    private MetalRendererBackend metal;
    @Inject(method = "onInitialize", at = @At("TAIL"))
    private void metalrender$init(CallbackInfo ci) {
        if (!MetalRenderMod.enabled) return;
        metal = new MetalRendererBackend();
        metal.init();
        metal.resizeIfNeeded();
    }
    @Inject(method = "onRender", at = @At("HEAD"), cancellable = true)
    private void metalrender$render(CallbackInfo ci) {
        if (metal == null) return;
        metal.render();
        ci.cancel();
    }
    @Inject(method = "onResize", at = @At("TAIL"))
    private void metalrender$resize(CallbackInfo ci) {
        if (metal != null) metal.resizeIfNeeded();
    }
}