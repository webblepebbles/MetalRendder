package pebblesboon.metalrender.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

import pebblesboon.metalrender.bridge.MetalBridge;
import pebblesboon.metalrender.bridge.ChunkFormats;

@Mixin(targets = {
        "me.jellysquid.mods.sodium.client.render.chunk.graphics.ChunkMesh",
}, remap = false)
public class SodiumChunkUploadMixin {
    @Inject(method = "upload", at = @At("RETURN"))
    private void metarender$onUpload(long sectionId,
                                     ByteBuffer vertexData,
                                     int vertexCount,
                                     ByteBuffer indexData,
                                     int indexCount,
                                     CallbackInfo ci) {
        if (vertexData == null || indexData == null) return;
        ByteBuffer v = MetalBridge.directCopy(vertexData);
        ByteBuffer i = MetalBridge.directCopy(indexData);
        MetalBridge.uploadChunkMesh(sectionId, v, ChunkFormats.POSITION_COLOR_F32, vertexCount, i, indexCount);
    }
}
