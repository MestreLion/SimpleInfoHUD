Simple Info HUD
===============

Minecraft mod of a simple HUD that displays coordinates, facing direction, in-game time and other info.

Designed to be an unbtrusive version of the F3 Debug screen, offering more useful information in a single line.

Information displayed:

- Coordinates
- Direction
- In-game and real-life time of day.
- <s>Light level</s>
- Biome
- FPS

Valuable information in very little screen area!

*Requires [ModLoader](http://www.minecraftforum.net/topic/75440-) or equivalent (such as [Forge](http://files.minecraftforge.net))*

*No base classes were harmed in the making of this mod*


Usage and details
------------------

`F4` toggles HUD on and off. By default is displays:

- Coordinates: `[x z y]`, `y` being the altitude / layer.
- Facing direction: `N`orth, `S`outh, `W`est, `E`ast, `NW`, `NE`, `SW`, `SE`.
- World time, in `HH:MM` format. To match the [clock](http://www.minecraftwiki.net/wiki/Clock) item, [day starts at 6AM](http://www.minecraftwiki.net/wiki/Day-night_cycle). Colors indicate day, dusk (sunset), night and dawn (sunrise).


And by holing `CTRL`, additional information is displayed:

- Exact facing angle, from -180º to 180º
- Exact world time in [game ticks](http://www.minecraftwiki.net/wiki/Tick).
- Real-life time, `HH:MM:SS` format.
- <s>Light level, from 0 (fully dark) to 15 (fully lit).</s>
- Biome
- Frames per second (FPS)

An example would be:

`[220 440 16] [E 90] 13:20 T12345 21:52:45 Jungle 60 fps`

* * *

Install
-------

- From zip
	- Install ModLoader or Forge
	- Download the pre-built mod for your Minecraft version (soon)
	- Save the zip to your Minecraft mods folder (`~/.minecraft` in Linux, `%APPDATA%/.mineraft in Windows) or use a lancher such as [Magic Launcher](http://www.minecraftforum.net/topic/939149-)

- From source
	- Install MCP
	- Clone this repository and copy its contents to `<MCP_LOC>/src/minecraft`
	- Run `<MCP_LOC>/recompile.{sh,bat}` and `<MCP_LOC>/reobfuscate.{sh,bat}`
	- Zip the content generated in `<MCP_LOC>/reobfs` and follow the above section.


Contributing
------------

Patches and Pull requests are very welcome, specially for features in the To-Do list. Bug reports and suggestions and comments too! If you want to hack the code, the easiest way to set up a development environment is:

- Download and install [Eclipse IDE for Java](http://www.eclipse.org/downloads/)
- Download and install Forge. While forge is not required to *run* the mod, it is needed to keep projects and sources separated and still build and run the mod from within Eclipse.
- In Eclipse, go to `File > Switch Workspace` and select `<FORGE_DIR>/mcp/eclipse`
- Select `Minecraft/Client` project, go `Project > Properties > Java Build Path > Order and Export` and select all but the JRE System Library
- Clone this repository
- In Eclipse, go to `File > Import > General > Existing Projects into Workspace` and Browse to the repository root folder.
- To launch, use the pre-set `SimpleInfoHUD` debug configuration.


* * *

To-Do List
-----------

- Use more colors:
	- Altitude: perhaps blue/yellow/cyan/red for Sea Level, top Gold, top Diamonds, Lava Lakes, etc
	- Biome?
- Maybe add week/moon phase to Full mode.
- Make it fade to gray or dissapear when game paused (GUI, Options, etc), just like F3 does.
- Parametrize some constants like time cycle colors and HUD position
- Add proper `mcmod.info` and other metadata. Would require Forge.
- Set up a forum thread and download page


Wont-Do List
------------

- Add a ton of in-game customization options. There is Lunatrius' amazing [InGame-Info-XML](https://github.com/Lunatrius/InGame-Info-XML) or [Zyin's HUD](http://www.minecraftforum.net/topic/1851472-) for that already. The idea of Simple Info HUD is to keep it simple and provide good defaults. Plug and play, no need to tinker with.

- Add Light Level back. Light level has many corner cases, such as half-slabs, stairs and ice. Besides, they are not enough to deem an area as unsafe. Block type (oppacity) also plays a major role, such as air, water, glass, etc. And there are many exceptions, corner cases and additional rules. A simple light level calculation is not helpful, and a good mob spawn calculation is a complex task unsuitable for the purpose of this mod. For that I recommend either Zyin's HUD or Lunatrius' [Monster Spawn Highlighter](https://github.com/Lunatrius/Monster-Spawn-Highlighter).


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
