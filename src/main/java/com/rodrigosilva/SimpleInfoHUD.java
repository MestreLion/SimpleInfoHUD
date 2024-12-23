package com.rodrigosilva;

import net.fabricmc.api.ClientModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Color;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Util;


public class SimpleInfoHUD implements ClientModInitializer {
	public static final String MOD_ID = "simple-info-hud";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	public static MatrixStack MATRIX_STACK;
	public static long WORLD_TICKS;

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
	public static int DAY_TICKS = 24000;  // https://minecraft.wiki/w/Daylight_cycle

	@Override
	public void onInitializeClient() {
		// Callback is (DrawContext context, float tickDelta) in Minecraft 1.20+
		// https://fabricmc.net/2023/05/25/120.html#screen-and-rendering-changes
		HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
			MATRIX_STACK = matrices;
			WORLD_TICKS = getWorldTicks();
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

		// Line 2: Advanced HUD
		msgX  = MARGIN_LEFT;
		msgY += LINE_HEIGHT;
		msgX += render(msgX, msgY, color, "[%d %d %d]", pos.getX(), pos.getY(), pos.getZ());
		msgX += render(msgX, msgY, color, "[%-2s%+6.1f]", direction, angle);

		/* World Time
		 * Reference: https://minecraft.wiki/w/Daylight_cycle
		 * Day:  10:00 rl = 12000 ticks. Start 06:00 / 0
		 * Dusk:  0:50 rl =  1000 ticks. Start 18:00 / 12000 (beds from  12542)
		 * Night: 8:20 rl = 10000 ticks. Start 19:00 / 13000 (mobs 13188-22812‌)
		 * Dawn:  0:50 rl =  1000 ticks. Start 05:00 / 23000 (beds until 23460)
		 */
		long ticks = WORLD_TICKS % DAY_TICKS;
		Color timeColor = color;  // Day, default color
		if      (ticks >= 23460) timeColor = Color.YELLOW;  // 05:27 Bed end, mob burn start
		else if (ticks >= 22812) timeColor = Color.ORANGE;  // 04:48 Mob spawn end (clear weather)
		else if (ticks >= 13188) timeColor = Color.RED;     // 19:11 Mob spawn start (clear weather)
		else if (ticks >= 12542) timeColor = Color.ORANGE;  // 18:32 Bed start, mob burn end
		else if (ticks >= 12000) timeColor = Color.YELLOW;  // 18:00 Dusk start
		msgX += render(msgX, msgY, timeColor, "%s T%5d", getWorldTime(), ticks);

		// Real Time
		msgX += render(msgX, msgY, color, getRealTime());

		// Biome
		msgX += render(msgX, msgY, color, getBiome(pos));

		// FPS
		msgX += render(msgX, msgY, color, "%d FPS", getFPS());
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
		if (arr[i] != "")
			width += renderCore(x + width, y, rgb, arr[i]) + SPACE_WIDTH;
		return width;
	}

	public static String getDirection(float yaw) {
		int zones = DIRECTIONS.length;
		int angle = (int)(yaw + 360/(2*zones) + 0.5) % 360;
		if (angle < 0)
			angle += 360;
		return DIRECTIONS[angle / (360/zones)];
	}

	public static long getWorldTicks() {
		/* Both getTimeOfDay() and getTime() are World "Age" in Ticks, neither wrapped to a day.
		 * getTime() does not advance when you sleep in beds, so not in sync with day/night cycle.
		 * Hence, getTimeOfDay() - getTime() == time "wasted" sleeping.
		 */
		return CLIENT.world.getTimeOfDay();  // CLIENT.level.getDayTime() in Minecraft 1.17+
	}

	public static String getWorldTime() {
		int hours = 60;  // just a helper to make the expression below easier to read
		long minutes = (6*hours + 24*hours * WORLD_TICKS / DAY_TICKS) % (24*hours);
		return String.format("%02d:%02d", minutes / 60, minutes % 60);
	}

	public static String getRealTime() {
		// By design it should show seconds, to easily tell apart from Minecraft World time
		// locale-aware alternative: DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
		return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}

	public static String getBiome(BlockPos pos) {
		// I'm sure there are better ways to get the friendly biome name...
		return I18n.translate(Util.createTranslationKey("biome",
			CLIENT.world.getRegistryManager().get(Registry.BIOME_KEY).getId(
				CLIENT.world.getBiome(pos)
			)
		));
	}

	public static int getFPS() {
		return Integer.parseInt(CLIENT.fpsDebugString.split(" ", 2)[0]);  // Lame!
	}
}
