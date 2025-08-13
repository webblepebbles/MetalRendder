package pebblesboon.metalrender.mixins;

import net.caffeinemc.mods.sodium.client.render.chunk.region.RenderRegionManager;
import net.caffeinemc.mods.sodium.client.gl.device.CommandList;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.BuilderTaskOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pebblesboon.metalrender.bridge.MetalBridge;

import java.util.Collection;

@Mixin(value = RenderRegionManager.class, remap = false)
public class SodiumChunkUploadMixin {

    @Inject(method = "uploadResults", at = @At("TAIL"))
    private void metarender$afterUpload(CommandList cl, Collection<BuilderTaskOutput> outputs, CallbackInfo ci) {
        for (BuilderTaskOutput output : outputs) {
            MetalBridge.uploadChunkMesh(output);
        }
    }
}

