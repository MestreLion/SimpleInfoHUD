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
import net.minecraft.client.gui.FontRenderer;
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
		int y = MathHelper.floor_double(minecraft.thePlayer.posY);

		// feet pos also == minecraft.thePlayer.posY - minecraft.thePlayer.yOffset
		int feet = MathHelper.floor_double(minecraft.thePlayer.boundingBox.minY);

		float angle = minecraft.thePlayer.rotationYaw;
		int direction = MathHelper.floor_double((double)(angle * 4.0F / 360.0F) + 0.5D) & 3;

		long time = minecraft.theWorld.getWorldTime() % 24000;
		long minutes = (6*60 + 24*60 * time / 24000) % (24*60);

		int light = minecraft.theWorld.getChunkFromBlockCoords(x, z).getSavedLightValue(EnumSkyBlock.Block, x & 15, y, z & 15);
		String biome = minecraft.theWorld.getBiomeGenForCoords(x, z).biomeName;
		String fps = minecraft.debug.split(",", 2)[0];

		msg = String.format("[%d %d %d]", new Object[] {x, z, feet});
		fr.drawStringWithShadow(msg, msgX, msgY, color);

		return true;
	}

	public String getVersion()
	{
			return "Version 1.0";
	}
}