package com.rodrigosilva;

import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class SimpleInfoHUD implements ModInitializer {
	public static final String MOD_ID = "simple-info-hud";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	@Override
	public void onInitialize() {
		HudRenderCallback.EVENT.register(SimpleInfoHUD::renderSimpleInfoHUD);
		LOGGER.info("[SimpleInfoHUD] Initialized");
	}

	// Called as (DrawContext context, float tickDelta) in Minecraft 1.20+
	// https://fabricmc.net/2023/05/25/120.html#screen-and-rendering-changes
	public static final void renderSimpleInfoHUD(MatrixStack matrices, float tickDelta) {
		float msgX = 2;
		float msgY = 2;
		int color = 0xFFFFFFFF;  // White
		String msg = "Hello SimpleInfoHUD!";
		CLIENT.textRenderer.drawWithShadow(matrices, msg, msgX, msgY, color);
	}
}
