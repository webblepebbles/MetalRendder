package com.metalrender.sodium.mixin;
import com.metalrender.MetalRenderClient;
import com.metalrender.sodium.MetalRendererBackend;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SodiumWorldRenderer.class)
public class SodiumRendererMixin {
    private MetalRendererBackend metal;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void metalrender$init(net.minecraft.client.MinecraftClient client, CallbackInfo ci) {
        if (!MetalRenderClient.isEnabled()) return;
        metal = new MetalRendererBackend();
        metal.init();
        metal.resizeIfNeeded();
    }

    @Inject(method = "setupTerrain", at = @At("TAIL"))
    private void metalrender$afterSetupTerrain(CallbackInfo ci) {
        if (metal == null) return;
        metal.render();
    }

    @Inject(method = "reload", at = @At("TAIL"), remap = false)
    private void metalrender$afterReload(CallbackInfo ci) {
        if (metal != null) {
            metal.resizeIfNeeded();
        }
    }
}