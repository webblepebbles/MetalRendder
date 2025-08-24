package com.metalrender.nativebridge;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class MetalHardwareChecker {
    public static boolean isCompatible() {
        // Replace this with your actual detection logic
        String vendor = org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_VENDOR);
        return vendor != null && vendor.contains("Apple");
    }

    public static void showIncompatibleScreen() {
        MinecraftClient.getInstance().setScreen(new IncompatibleHardwareScreen());
    }

    private static class IncompatibleHardwareScreen extends Screen {
        protected IncompatibleHardwareScreen() {
            super(Text.literal("Incompatible Hardware"));
        }

        @Override
        protected void init() {
            this.addDrawableChild(
                    ButtonWidget.builder(Text.literal("Back"), button -> {
                        MinecraftClient.getInstance().setScreen(new TitleScreen());
                    }).dimensions(this.width / 2 - 100, this.height / 2 + 20, 200, 20).build()
            );
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            this.renderBackground(context, mouseX, mouseY, delta);

            context.drawCenteredTextWithShadow(this.textRenderer,
                    "MetalRender has been disabled!", this.width / 2, this.height / 2 - 20, 0xFF5555);
            context.drawCenteredTextWithShadow(this.textRenderer,
                    "Your GPU does not support Apple Metal.", this.width / 2, this.height / 2, 0xFFFFFF);
            super.render(context, mouseX, mouseY, delta);
        }
    }
}