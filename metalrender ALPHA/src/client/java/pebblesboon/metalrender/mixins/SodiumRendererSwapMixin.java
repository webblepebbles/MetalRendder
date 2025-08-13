package pebblesboon.metalrender.mixins;

import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pebblesboon.metalrender.MetalRenderer;
import pebblesboon.metalrender.MetalRuntime;

@Mixin(value = SodiumWorldRenderer.class, remap = false)
public abstract class SodiumRendererSwapMixin {

    @Inject(method = "instance", at = @At("HEAD"), cancellable = true)
    private static void metarender$replaceRenderer(CallbackInfoReturnable<SodiumWorldRenderer> cir) {
        if (MetalRuntime.get().isAvailable()) {
            MinecraftClient client = MinecraftClient.getInstance();
            WorldRenderer renderer = new MetalRenderer(client);
            cir.setReturnValue((SodiumWorldRenderer) (Object) renderer);
        }
    }
}
