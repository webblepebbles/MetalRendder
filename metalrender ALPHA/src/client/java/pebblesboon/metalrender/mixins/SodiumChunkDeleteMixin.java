package pebblesboon.metalrender.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pebblesboon.metalrender.bridge.MetalBridge;

@Mixin(targets = "me.jellysquid.mods.sodium.client.render.chunk.graphics.ChunkMesh", remap = false)
public class SodiumChunkDeleteMixin {

    @Inject(method = "delete", at = @At("HEAD"))
    private void metarender$onDelete(long sectionId, CallbackInfo ci) {
        MetalBridge.deleteChunkMesh(sectionId);
    }
}
