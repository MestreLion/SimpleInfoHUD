Simple Info HUD
===============

Minecraft mod of a simple HUD that displays coordinates, facing direction, in-game time and other info.

Designed to be an unbtrusive version of the F3 Debug screen, offering more useful information in a single line.

Information displayed:

- Coordinates: `[x z y]`, `y` being the altitude / layer.
- Facing direction: `N`orth, `S`outh, `W`est, `E`ast, and the exact angle, from -180º to 180º.
- In-game time, in `HH:MM` format. To match the clock item, day starts at 6AM.
- Light level, from 0 (fully dark) to 15 (fully lit).
- Biome
- FPS

An example would be:

`[220 440 16] [E 90] 13:20 20 Jungle 60 fps`

Valuable information in very little screen area!

*Requires [ModLoader](http://www.minecraftforum.net/topic/75440-) or equivalent (such as [Forge](http://files.minecraftforge.net))*

* * *

To-Do List
-----------

- Assign a key to toggle display on/off. `F4` perhaps?
- Add more directions: `NW, NE, SW, SE`
- Use color for additional info:
	- Light Level: red for <= 7 (mob spawn), blue for "safe"
	- Time: yellow for dawn/dusk, red for night
	- Altitude: perhaps blue/yellow/cyan/red for Sea Level, top Gold, top Diamonds, Lava Lakes, etc
	- Biome?
- Split info in *simple* and *full* mode (when `CTRL` or `Shift` is down). In Simple mode, display only the really useful info such as coordinates, direction and time. Angle, Biome, FPS etc can be tossed to full mode only.
- Add game day, and perhaps week/moon phase to Full mode. And Real-world date/time.
- Simple mode display a single **`!`** when Light <=7
- Make it fade to gray or dissapear when game paused (GUI, Options, etc), just like F3 does.
- Add proper `mcmod.info` and other metadata
- Set up a forum thread and download page


Wont-Do List
------------

- Add a ton of customization options. There is Lunatrius' amazing [InGame-Info-XML](https://github.com/Lunatrius/InGame-Info-XML) for that already. The idea of Simple Info HUD is to keep it simple and provide good defaults. Plug and play, no need to fiddle with any file.

* * *

Credits and Disclaimer
----------------------

This is my first mod, and I've used many ideas and templates from other mods. Special thanks to:

- [Lunatrius](https://github.com/Lunatrius) and [bspkrs](https://github.com/bspkrs), for their kind support and references, and also very inspiring mods.

- [Zyin's HUD](http://minecraft.curseforge.com/mc-mods/zyins-hud), for many ideas and inspirations on useful information to display in a consise HUD.

- Gambit and counteless other kind souls on IRC.

Feel free to use, fork and contribute!

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