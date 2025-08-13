package pebblesboon.metalrender.mixins;

import net.caffeinemc.mods.sodium.client.render.chunk.region.RenderRegion;
import net.caffeinemc.mods.sodium.client.gl.device.CommandList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pebblesboon.metalrender.bridge.MetalBridge;

@Mixin(value = RenderRegion.class, remap = false)
public class SodiumChunkDeleteMixin {

    @Inject(method = "delete", at = @At("HEAD"))
    private void metarender$onDelete(CommandList cl, CallbackInfo ci) {
        MetalBridge.deleteChunkMesh(this);
    }
}

