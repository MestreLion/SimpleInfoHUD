package com.rodrigosilva;

import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Color;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class SimpleInfoHUD implements ModInitializer {
	public static final String MOD_ID = "simple-info-hud";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	public static MatrixStack MATRIX_STACK;

	@Override
	public void onInitialize() {
		// Callback is (DrawContext context, float tickDelta) in Minecraft 1.20+
		// https://fabricmc.net/2023/05/25/120.html#screen-and-rendering-changes
		HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
			MATRIX_STACK = matrices;
			mainSimpleInfoHUD();
		});
		LOGGER.info("[SimpleInfoHUD] Initialized");
	}

	public static final void mainSimpleInfoHUD() {
		float msgX = 2;
		float msgY = 2;
		Color color = Color.WHITE;
		String fmt = "Hello %s!";
		render(msgX, msgY, color, fmt, MOD_ID);
	}

	public static void render(float x, float y, Color color, String format, Object... args) {
		String msg = String.format(format, args);
		CLIENT.textRenderer.drawWithShadow(MATRIX_STACK, msg, x, y, color.getRGB());
	}
}
