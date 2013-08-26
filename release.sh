#!/bin/bash
modname=SimpleInfoHUD
modversion=$1
mcversion=${2:-1.5.2}
moddir=$(dirname "$(readlink -f "$0")")
mcpdir=${MCP_LOC:-../forge/"$mcversion"/mcp}

[ -n "$mcversion" -a -n "$modversion" ] || { echo "Usage: release.sh MODVERSION [MCVERSION]" ; exit ; }
cd "$mcpdir"
./recompile.sh
cp -vr "$moddir"/bin/* bin/minecraft
./reobfuscate.sh
cd reobf/minecraft
zip -r "$moddir"/release/"[${mcversion}]$modname".v"$modversion".zip *
