package com.rodrigosilva;

import net.fabricmc.api.ClientModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Color;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
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
	private static final int BACKGROUND = 0x90505050;  // −1873784752
	private static final int LINE_HEIGHT = 9;  // int j = 9;
	private static final int MARGIN_LEFT = 2;
	private static final int MARGIN_TOP = 2;

	// Helper constants
	public static String[] DIRECTIONS = {"S", "SW", " W", "NW", "N", "NE", " E", "SE"};
	public static int MONO_WIDTH  = 6;  // == CLIENT.textRenderer.getWidth​("W")
	public static int SPACE_WIDTH = 4;  // == CLIENT.textRenderer.getWidth​(" ")
	public static int DAY_TICKS = 24000;  // https://minecraft.wiki/w/Daylight_cycle
	public static float SCALE = 0.75f;  // Text scale
	public static float DEBUG_HEIGHT;  // F3 Debug info height

	@Override
	public void onInitializeClient() {
		// Callback is (DrawContext context, float tickDelta) in Minecraft 1.20+
		// https://fabricmc.net/2023/05/25/120.html#screen-and-rendering-changes
		HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
			MATRIX_STACK = matrices;
			WORLD_TICKS = getWorldTicks();
			DEBUG_HEIGHT = getDebugHeight();
			mainSimpleInfoHUD();
		});
		LOGGER.info("[SimpleInfoHUD] Initialized");
	}

	public static final void mainSimpleInfoHUD() {
		int msgX = 0;

		// Player Position
		Entity entity = CLIENT.getCameraEntity();
		BlockPos pos = entity.getBlockPos();
		msgX += render(1, msgX, "[%d %3d %d]", pos.getX(), pos.getY(), pos.getZ());

		// Player Direction
		float yaw = entity.yaw;  // Not wrapped, full range of negative and positive angles
		float angle = MathHelper.wrapDegrees(yaw);  // Yaw wrapped to [-180, +180]
		String direction = getDirection(yaw);
		msgX += render(1, msgX, "[%-2s]", direction);  // Simple

		// Line 2: Advanced HUD
		msgX = 0;
		msgX += render(msgX, "[%d %3d %d]", pos.getX(), pos.getY(), pos.getZ());
		msgX += render(msgX, "[%-2s%+6.1f]", direction, angle);

		long ticks = WORLD_TICKS % DAY_TICKS;
		msgX += render(msgX, getWorldTimeColor(), "%s T%5d", getWorldTime(), ticks);

		// Real Time
		msgX += render(msgX, getRealTime());

		// Biome
		msgX += render(msgX, getBiome(pos));

		// FPS
		msgX += render(msgX, "%d FPS", getFPS());
	}

	/**********************************************************************
	 * Rendering / drawing methods
	 **********************************************************************/

	public static int fill_background(float x, float y, int width) {
		MATRIX_STACK.push();
		MATRIX_STACK.scale(SCALE, SCALE, SCALE);
		DrawableHelper.fill(
			MATRIX_STACK,
			(int)x,
			(int)y      - 1,
			(int)x      + width,
			(int)y      + LINE_HEIGHT,
			BACKGROUND
		);
		MATRIX_STACK.pop();
		return width;
	}

	/* Basic API: render string as-is at (x, y) with color.
	 * Automatically scale font size and add semi-transparent background.
	 * Return string width
	 */
	public static int renderCore(float x, float y, int rgb, String msg) {
		int width = CLIENT.textRenderer.getWidth​(msg);
		fill_background(x, y, width);
		MATRIX_STACK.push();
		MATRIX_STACK.scale(SCALE, SCALE, SCALE);
		int x1 = CLIENT.textRenderer.drawWithShadow(MATRIX_STACK, msg, x, y, rgb);
		if (x1 != (int)x + width + 1)
			LOGGER.warn(String.format(
				"[%s] renderCore() mismatch: %d != %d + %d + 1",
				MOD_ID, x1, (int)x, width
			));
		MATRIX_STACK.pop();
		return width;
	}

	/* Higher level render API:
	 * - Line number instead of raw y
	 * - Color object instead of raw ARGB
	 * - Built-in String formatting using optional args
	 * - Use left margin if rendering at x=0
	 * - Monospace: render each character individually, full-width
	 * Return rendered text width, including added margin, if any.
	 */
	public static int renderMono(int line, float x, Color color, String format, Object... args) {
		String msg = String.format(format, args);
		int length = msg.length();
		int rgb = color.getRGB();
		float y = MARGIN_TOP + DEBUG_HEIGHT + line * LINE_HEIGHT;
		int margin = 0;
		if (x == 0)
			margin = MARGIN_LEFT;
		for (int i = 0; i < length; i++) {
			float posX = margin + x + i * MONO_WIDTH;
			int width = renderCore(posX, y, rgb, msg.substring(i, i+1));
			if (width != MONO_WIDTH)
				fill_background(posX + width, y, MONO_WIDTH - width);
		}
		return margin + length * MONO_WIDTH;
	}

	/* Highest level render API:
	 * - Smart spacing: handle spaces as full-width, all other characters as-is
	 * - Add a leading regular-width space if at left margin and formatted string is not empty
	 * - Add a trailing regular-width space, unless formatted string is empty or
	 *   already contains a trailing space (which will be rendered as full-width)
	 * Return rendered text width, including added margin, leading and trailing spaces, if any.
	 */
	public static int render(int line, float x, Color color, String format, Object... args) {
		String msg = String.format(format, args);
		if (msg == "")
			return 0;  // short-circuit
		String[] arr = msg.split(" ", -1);
		int rgb = color.getRGB();
		float y = MARGIN_TOP + DEBUG_HEIGHT + line * LINE_HEIGHT;
		int width = 0;
		if (x == 0 || x == MARGIN_LEFT) {
			width += MARGIN_LEFT;  // Add margin (no fill)
			width += fill_background(width, y, SPACE_WIDTH);  // leading space
		}
		int i = 0;
		for (; i < arr.length - 1; i++) {
			width += renderCore(x + width, y, rgb, arr[i]);
			width += fill_background(x + width, y, MONO_WIDTH);
		}
		if (arr[i] != "") {
			width += renderCore(x + width, y, rgb, arr[i]);
			width += fill_background(x + width, y, SPACE_WIDTH);  // trailing space
		}
		return width;
	}
	// No line, draw on first (line 0)
	public static int render(float x, Color color, String format, Object... args) {
		return render(0, x, color, format, args);
	}
	// No color, use default (GREY)
	public static int render(int line, float x, String format, Object... args) {
		return render(line, x, GREY, format, args);
	}
	// No line nor color
	public static int render(float x, String format, Object... args) {
		return render(0, x, GREY, format, args);
	}

	/**********************************************************************
	 * Minecraft data fetching methods
	 **********************************************************************/

	/* F3 Debug Info text height, considering scale, enabled and "Reduced Debug Info"
	 * In Minecraft 1.16.4: 26 lines (13 if reduced) + 1 blank line
	 */
	public static float getDebugHeight() {
		if (!CLIENT.options.debugEnabled)
			return 0;
		return (CLIENT.options.reducedDebugInfo ? 14 : 27) * LINE_HEIGHT / SCALE;
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

	/* World Time
	 * Reference: https://minecraft.wiki/w/Daylight_cycle
	 * Day:  10:00 rl = 12000 ticks. Start 06:00 / 0
	 * Dusk:  0:50 rl =  1000 ticks. Start 18:00 / 12000 (beds from  12542)
	 * Night: 8:20 rl = 10000 ticks. Start 19:00 / 13000 (mobs 13188-22812‌)
	 * Dawn:  0:50 rl =  1000 ticks. Start 05:00 / 23000 (beds until 23460)
	 */
	public static String getWorldTime() {
		int hours = 60;  // just a helper to make the expression below easier to read
		long minutes = (6*hours + 24*hours * WORLD_TICKS / DAY_TICKS) % (24*hours);
		return String.format("%02d:%02d", minutes / 60, minutes % 60);
	}

	public static Color getWorldTimeColor() {
		long ticks = WORLD_TICKS % DAY_TICKS;
		if      (ticks >= 23460) return Color.YELLOW;  // 05:27 Bed end, mob burn start
		else if (ticks >= 22812) return Color.ORANGE;  // 04:48 Mob spawn end (clear weather)
		else if (ticks >= 13188) return Color.RED;     // 19:11 Mob spawn start (clear weather)
		else if (ticks >= 12542) return Color.ORANGE;  // 18:32 Bed start, mob burn end
		else if (ticks >= 12000) return Color.YELLOW;  // 18:00 Dusk start
		return GREY;
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
