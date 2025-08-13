package pebblesboon.metalrender.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pebblesboon.metalrender.MetalRenderer;
import pebblesboon.metalrender.MetalRuntime;

@Mixin(targets = "me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer", remap = false)
public abstract class SodiumRendererSwapMixin {

    @Inject(method = "create", at = @At("HEAD"), cancellable = true, remap = false)
    private static void metarender$create(MinecraftClient client, CallbackInfoReturnable<WorldRenderer> cir) {
        if (MetalRuntime.get().isAvailable()) {
            cir.setReturnValue(new MetalRenderer(client));
        }
    }
}
