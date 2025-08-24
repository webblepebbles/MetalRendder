package com.metalrender;

import com.metalrender.nativebridge.MetalHardwareChecker;
import net.fabricmc.api.ClientModInitializer;

public class MetalRenderClient implements ClientModInitializer {
    public static boolean enabled = true;

    @Override
    public void onInitializeClient() {
        init();
    }

    public static void init() {
        if (!MetalHardwareChecker.isCompatible()) {
            disable();
            MetalHardwareChecker.showIncompatibleScreen();
        }
    }

    public static void disable() {
        enabled = false;
        System.out.println("[MetalRender] Disabled due to incompatible hardware");
    }

    public static void enable() {
        enabled = true;
        System.out.println("[MetalRender] Enabled");
    }

    public static boolean isEnabled() {
        return enabled;
    }
}