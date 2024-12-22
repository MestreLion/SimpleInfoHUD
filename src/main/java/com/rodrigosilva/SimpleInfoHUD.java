package com.rodrigosilva;

import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Color;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;


public class SimpleInfoHUD implements ModInitializer {
	public static final String MOD_ID = "simple-info-hud";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	public static MatrixStack MATRIX_STACK;

	// Constants from the F3 Debug overlay, taken from Minecraft 1.17.1 at
	// net.minecraft.client.gui.components.DebugScreenOverlay
	private static final Color GREY = new Color(0xE0E0E0);  // int COLOR_GREY = 14737632;
	private static final int LINE_HEIGHT = 9;  // int j = 9;
	private static final int MARGIN_LEFT = 2;
	private static final int MARGIN_TOP = 2;
	private static final int DEBUG_HEIGHT = 27 * LINE_HEIGHT;  // 26 lines in 1.16.4 + 1 blank

	// Helper constants
	public static String[] DIRECTIONS = {"S", "SW", " W", "NW", "N", "NE", " E", "SE"};
	public static int MONO_WIDTH  = 6;  // == CLIENT.textRenderer.getWidth​("W")
	public static int SPACE_WIDTH = 4;  // == CLIENT.textRenderer.getWidth​(" ")

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
		float msgX = MARGIN_LEFT;
		float msgY = MARGIN_TOP + (CLIENT.options.debugEnabled ? DEBUG_HEIGHT : 0);
		Color color = GREY;  // Default F3 color, almost white

		// Player Position
		Entity entity = CLIENT.getCameraEntity();
		BlockPos pos = entity.getBlockPos();
		msgX += render(msgX, msgY, color, "[%d %d %d]", pos.getX(), pos.getY(), pos.getZ());

		// Player Direction
		float yaw = entity.yaw;  // Not wrapped, full range of negative and positive angles
		float angle = MathHelper.wrapDegrees(yaw);  // Yaw wrapped to [-180, +180]
		String direction = getDirection(yaw);
		msgX += SPACE_WIDTH;
		msgX += render(msgX, msgY, color, "[%-2s]", direction);  // Simple

		// Line 2: Advanced HUD
		msgX  = MARGIN_LEFT;
		msgY += LINE_HEIGHT;
		msgX += render(msgX, msgY, color, "[%-2s%+5.1f]", direction, angle);  // Advanced
	}

	// Basic: render as-is, return string width
	public static int renderCore(float x, float y, int rgb, String msg) {
		CLIENT.textRenderer.drawWithShadow(MATRIX_STACK, msg, x, y, rgb);
		return CLIENT.textRenderer.getWidth​(msg);
	}

	// Monospace: render each character individually, full-width
	public static int renderMono(float x, float y, Color color, String format, Object... args) {
		String msg = String.format(format, args);
		int length = msg.length();
		int rgb = color.getRGB();
		for (int i = 0; i < length; i++) {
			renderCore(x + i * MONO_WIDTH, y, rgb, msg.substring(i, i+1));
		}
		return length * MONO_WIDTH;
	}

	// "Smart" spacing: handle spaces as full-width, all other characters as-is
	public static int render(float x, float y, Color color, String format, Object... args) {
		String msg = String.format(format, args);
		if (msg == "")
			return 0;  // optional short-circuit
		String[] arr = msg.split(" ", -1);
		int width = 0;
		int rgb = color.getRGB();
		int i = 0;
		for (; i < arr.length - 1; i++) {
			width += renderCore(x + width, y, rgb, arr[i]) + MONO_WIDTH;
		}
		return width + ((arr[i] == "") ? 0 : renderCore(x + width, y, rgb, arr[i]));
	}

	public static String getDirection(float yaw) {
		int zones = DIRECTIONS.length;
		int angle = (int)(yaw + 360/(2*zones) + 0.5) % 360;
		if (angle < 0)
			angle += 360;
		return DIRECTIONS[angle / (360/zones)];
	}
}
