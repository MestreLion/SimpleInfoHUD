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

import net.minecraft.client.Minecraft;

// Remove these imports if using the non-Forge MCP+ModLoader environment
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;

public class mod_SimpleInfoHud extends BaseMod
{
	public void load()
	{
		ModLoader.setInGameHook(this, true, false);
	}

	public boolean onTickInGame(float f, Minecraft minecraft)
	{
		// For reference: F3 debug info is generated at GuiIngame.renderGameOverlay() @ 440
		// Screen "margin" is 2,2, Line height is 10 or 8,
		// color is 0xFFFFFF (white) or 0xE0E0E0 (gray)

		FontRenderer fr = minecraft.fontRenderer;

		String msg = "";
		int msgX = 2;
		int msgY = 2;
		int color = 0xFFFFFF;

		int x = MathHelper.floor_double(minecraft.thePlayer.posX);
		int z = MathHelper.floor_double(minecraft.thePlayer.posZ);

		// Feet Y, the one that matters. (also == posY - yOffset)
		// For head Y: minecraft.thePlayer.posY
		int fy = MathHelper.floor_double(minecraft.thePlayer.boundingBox.minY);

		float yaw = minecraft.thePlayer.rotationYaw;
		float angle = MathHelper.wrapAngleTo180_float(yaw);
		char direction = Direction.directions[MathHelper.floor_double((double)(yaw * 4.0F / 360.0F) + 0.5D) & 3].charAt(0);

		long time = minecraft.theWorld.getWorldTime() % 24000;
		long minutes = (6*60 + 24*60 * time / 24000) % (24*60);

		int light = minecraft.theWorld.getChunkFromBlockCoords(x, z).getSavedLightValue(EnumSkyBlock.Block, x & 15, fy, z & 15);
		String biome = minecraft.theWorld.getBiomeGenForCoords(x, z).biomeName;
		String fps = minecraft.debug.split(",", 2)[0];

		msg = String.format(
				"[%d %d %d] [%s %+3.0f] %02d:%02d %d %s %s",
				x, z, fy, direction, angle, minutes / 60, minutes % 60,
				light, biome, fps);
		fr.drawStringWithShadow(msg, msgX, msgY, color);

		return true;
	}

	public String getVersion()
	{
			return "Version 1.0";
	}
}