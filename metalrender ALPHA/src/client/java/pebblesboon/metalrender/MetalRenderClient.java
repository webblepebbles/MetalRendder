package pebblesboon.metalrender;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class MetalRenderClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MetalRenderer.LOG.info("MetalRender client init");
        MetalRuntime.get().bootstrap();

        if (FabricLoader.getInstance().isModLoaded("sodium")) {
            MetalRenderer.LOG.info("Sodium detected — Metal backend swap ready");
        } else {
            MetalRenderer.LOG.warn("Sodium not detected — Metal backend will be inactive");
        }
    }
}
