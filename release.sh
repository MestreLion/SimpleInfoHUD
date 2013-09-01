#!/bin/bash
modname=SimpleInfoHUD
modversion=${1:-git}
mcversion=${2:-1.5.2}
moddir=$(dirname "$(readlink -f "$0")")
mcploc=${MCP_LOC:-../forge/"$mcversion"/mcp}
mcpdir="$(dirname "$mcploc")"/mcp-temp

usage() { echo "Usage: release.sh [MODVERSION] [MCVERSION]" ; exit ; }
for arg in "$@"; do [[ "$arg" == "-h" || "$arg" == "--help" ]] && usage ; done

[ -n "$mcversion" -a -n "$modversion" ] || usage

set -e

cd "$moddir"
rm -rf "$mcpdir"
cp -r "$mcploc" "$mcpdir"
cd "$mcpdir"
cp -vr "$moddir"/src/* src/minecraft
./recompile.sh
./reobfuscate.sh
cd reobf/minecraft
zip -r "$moddir"/release/"[${mcversion}]$modname"."$modversion".zip *
cd "$moddir"
rm -rf "$mcpdir"

echo "Done"
