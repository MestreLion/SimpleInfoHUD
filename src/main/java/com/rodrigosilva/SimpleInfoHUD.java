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
		msgX += render(msgX, msgY, color, "[%-2s]", direction);  // Simple
		msgX += render(msgX, msgY, color, "[%-2s%+5.1f]", direction, angle);  // Advanced
	}

	public static int render(float x, float y, Color color, String format, Object... args) {
		String msg = String.format(format, args);
		CLIENT.textRenderer.drawWithShadow(MATRIX_STACK, msg, x, y, color.getRGB());
		return CLIENT.textRenderer.getWidth​(msg);
	}

	public static String getDirection(float yaw) {
		int zones = DIRECTIONS.length;
		int angle = (int)(yaw + 360/(2*zones) + 0.5) % 360;
		if (angle < 0)
			angle += 360;
		return DIRECTIONS[angle / (360/zones)];
	}
}
