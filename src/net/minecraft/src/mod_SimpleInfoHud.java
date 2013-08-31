/*
    SimpleInfoHUD - Minecraft mod that displays coordinates, direction, time

    Copyright (C) 2013 Rodrigo Silva (MestreLion) <linux@rodrigosilva.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. See <http://www.gnu.org/licenses/gpl.html>
*/

package net.minecraft.src;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;

// Remove these imports if using the non-Forge MCP+ModLoader environment
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;

public class mod_SimpleInfoHud extends BaseMod
{
	public String[] directions = {"S", "SW", " W", "NW", "N", "NE", " E", "SE"};
	public KeyBinding activateKey = new KeyBinding("Simple Info HUD", Keyboard.KEY_F4);
	public boolean showHud = true;

	public String getVersion()
	{
			return "Version 2.0";
	}

	public void load()
	{
		ModLoader.registerKey(this, activateKey,  false);
		ModLoader.addLocalization("Simple Info HUD", "Simple Info HUD");
		ModLoader.setInGameHook(this, true, false);
	}

	public void keyboardEvent(KeyBinding event)
	{
		if (event == activateKey)
			showHud = !showHud;
	}

	public boolean onTickInGame(float f, Minecraft minecraft)
	{
		if (!showHud)
			return true;

		// For reference: F3 debug info is generated at GuiIngame.renderGameOverlay() @ 440
		// Screen "margin" is 2,2, Line height is 10 or 8,
		// color is 0xFFFFFF (white) or 0xE0E0E0 (gray)

		int msgX = 2;
		int msgY = 2;
		Color color = Color.WHITE;

		int x = MathHelper.floor_double(minecraft.thePlayer.posX);
		int z = MathHelper.floor_double(minecraft.thePlayer.posZ);

		// Feet Y, the one that matters. (also == posY - yOffset)
		// For head Y: minecraft.thePlayer.posY
		int fy = MathHelper.floor_double(minecraft.thePlayer.boundingBox.minY);

		float yaw = minecraft.thePlayer.rotationYaw;
		float angle = MathHelper.wrapAngleTo180_float(yaw);
		String direction = directions[wrapAngleToDirection(yaw, directions.length)];

		String realTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		long time = minecraft.theWorld.getWorldTime() % 24000;
		long minutes = (6*60 + 24*60 * time / 24000) % (24*60);
		/*
		 * Reference: http://www.minecraftwiki.net/wiki/Day-night_cycle
		 * Day:  10.0 rl minutes = 12000 ticks = 720 mc minutes;     0
		 * Dusk:  1.5 rl minutes =  1800 ticks = 108 mc minutes; 12000 (mobs: 13187)
		 * Night: 7.0 rl minutes =  8400 ticks = 504 mc minutes; 13800
		 * Dawn:  1.5 rl minutes =  1800 ticks = 108 mc minutes; 22200 (mobs: 22812)
		 */
		Color timeColor = color;  // Day, default color
		if       (time > 22812) timeColor = Color.YELLOW;
		else if (time > 22200) timeColor = Color.ORANGE;
		else if (time > 13800) timeColor = Color.RED;
		else if (time > 13187) timeColor = Color.ORANGE;
		else if (time > 12000) timeColor = Color.YELLOW;

		int light = minecraft.theWorld.getChunkFromBlockCoords(x, z).getSavedLightValue(EnumSkyBlock.Block, x & 15, fy, z & 15);
		boolean lowLight = light <= 7;
		Color lightColor = lowLight ? Color.RED : color;

		String biome = minecraft.theWorld.getBiomeGenForCoords(x, z).biomeName;
		String fps = minecraft.debug.split(",", 2)[0].replaceFirst(" ", "");

		boolean advanced = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);

		msgX += displayHud(minecraft, msgX, msgY, color, "[%d %d %3d]", x, z, fy);

		if (advanced) {
			msgX += displayHud(minecraft, msgX, msgY, color, "[%-2s%+4.0f]", direction, angle);
			msgX += displayHud(minecraft, msgX, msgY, timeColor, "T%5d", time);
			msgX += displayHud(minecraft, msgX, msgY, color, "%s", realTime);
			msgX += displayHud(minecraft, msgX, msgY, lightColor, "L%2d", light);
			msgX += displayHud(minecraft, msgX, msgY, color, "%s %s", biome, fps);
		}
		else {
			msgX += displayHud(minecraft, msgX, msgY, color, "[%-2s]", direction);
			msgX += displayHud(minecraft, msgX, msgY, timeColor,
					"%02d:%02d", minutes / 60, minutes % 60);
			if (lowLight)
				msgX += displayHud(minecraft, msgX, msgY, lightColor, "UNSAFE");
		}
		return true;
	}

	public int wrapAngleToDirection(float yaw, int zones)
	{
		int angle = (int)(yaw + 360/(2*zones) + 0.5) % 360;
		if (angle < 0)
			angle += 360;
		return angle / (360/zones);
	}

	// Pseudo-Monospaced display: spaces are printed with the same width as "W"
	public int displayHud(Minecraft minecraft, int x, int y, Color color, String format, Object... args)
	{
		String[] msg = String.format(format, args).split(" ", -1);
		int monoWidth = minecraft.fontRenderer.getCharWidth('W');
		int rgb = color.getRGB();
		int startX = x;
		int i = 0;
		for (; i < msg.length-1; i++) {
			minecraft.fontRenderer.drawStringWithShadow(msg[i], x, y, rgb);
			x += minecraft.fontRenderer.getStringWidth(msg[i]);
			minecraft.fontRenderer.drawStringWithShadow(" ", x, y, rgb);
			x += monoWidth;
		}
		if (msg[i] != "") {
			minecraft.fontRenderer.drawStringWithShadow(msg[i], x, y, rgb);
			x += minecraft.fontRenderer.getStringWidth(msg[i]);
		}
		return x - startX + minecraft.fontRenderer.getCharWidth(' '); // 4
	}
}
