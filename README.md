# Archeology Mod

[![Community on Discord](https://raw.githubusercontent.com/capsey/archeology-mod/main/badge-discord.png "Community on Discord")](https://discord.gg/RmSUYprbQs)
[![Download on Modrinth](https://raw.githubusercontent.com/capsey/archeology-mod/main/badge-modrinth.png "Download on Modrinth")](https://modrinth.com/mod/archeology)
[![Download on CurseForge](https://raw.githubusercontent.com/capsey/archeology-mod/main/badge-curseforge.png "Download on CurseForge")](https://www.curseforge.com/minecraft/mc-mods/archeology)

Archeology Mod is a [Fabric](https://fabricmc.net/) mod for [Minecraft](https://www.minecraft.net/), that adds Archeology System! My goal was to make it as Vanilla-like as I possibly could, to try to imagine how Mojang will implement it (and maybe even give them inspiration).

**If you found a bug, or have a suggestion, advice, please create a [GitHub Issue](https://github.com/capsey/archeology-mod/issues).**

![Fancy screenie](https://user-images.githubusercontent.com/46106832/191838583-82b8afc4-5e5f-4369-96aa-b307a4f9d38a.png "Fancy screenie")

**The mod requires [Fabric API](https://github.com/FabricMC/fabric) to work! Also, this is recommended to use [Mod Menu](https://github.com/TerraformersMC/ModMenu) to easily edit mod's config.**

## What is the Archeology System?
Archeology was firstly introduced at [Minecraft Live 2020](https://youtu.be/DWZIfsaIgtE?t=7229), but was later [delayed from 1.17](https://youtu.be/6YgKUZnUyak?t=285) Caves and Cliffs Update. Officially announced features of the Archeology System:
* [Excavation Dig Sites](https://minecraft-archive.fandom.com/wiki/Excavation_Sites) (structures)
* [Archeology Brush](https://minecraft-archive.fandom.com/wiki/Brush) (presumably made of Copper and can oxidize)
* Brushing mechanic (removing layers from soft blocks to get loot)
* [Ceramic Shards](https://minecraft-archive.fandom.com/wiki/Ceramic_Shard)
* [Hardened and Raw Clay Pots](https://minecraft-archive.fandom.com/wiki/Clay_Pot)
* Hardening (firing up) Clay Pots

## Mod features
Some of the features that I added wasn't announced by Mojang and was made up by me. There is a full list of all implemented features:

### Copper Brush (AKA Archeology Brush)
Copper Brush is a new tool, used for excavating special blocks for getting loot. It is made using a [Copper Ingot](https://minecraft.fandom.com/wiki/Copper_Ingot) and a [Feather](https://minecraft.fandom.com/wiki/Feather).
* More you use the brush, more oxidized it gets.
* More oxidized brush is, rarer loot you get (similar to [Luck of the Sea](https://minecraft.fandom.com/wiki/Luck_of_the_Sea) enchantment), but harder it is to not break brushing block.

![Excavation Dirt (Emerald)](https://user-images.githubusercontent.com/46106832/191838488-1c611660-0709-46d0-a5ed-64732033511d.png "Excavation Dirt (Emerald)")

### Excavation Blocks
New block type — Excavation Block! Currently, there are four excavation blocks in the mod: Excavation Dirt, Gravel, Sand and Red Sand.
* They look similar to regular block (e.g., Dirt or Gravel), but have white stripes, and you can use a Copper Brush on them to start brushing layers off.
* When fully brushed off, the block drops random loot (depending on excavation site you found it, and oxidation level of the brush)
* While brushing, you should move your mouse like you are brushing to stop the block from breaking. If you don't move the mouse enough, the block will eventually break, and you won't get anything. The more oxidized your brush is, the faster the block breaks, and therefore faster you need to move the mouse.
    * Actually, Mojang showed this feature other way around: if you are not careful enough, the block will break. However, I changed it so that it's more challenging and exciting (you can tweak the difficulty in the mod's config).

### Ceramic Shards
Ceramic Shards are items, that are dropped from brushing the Excavation Blocks. You can place them on the Raw Clay Pot, so that when hardened, this pot will retain placed shards and have pretty drawing on it.
* Different shards have different drawings on them, and the drawing of the shard depends on the Excavation Site you found it.

### Raw Clay Pots
This is a block that used to make (Hardened) Clay Pot. It is made of 5 [Clay Balls](https://minecraft.fandom.com/wiki/Clay_Ball). When broken, it drops 2-4 clay balls (or block itself if mined using [Silk Touch](https://minecraft.fandom.com/wiki/Silk_Touch) enchantment), and shards, if there were any. The fastest tool is shovel.
* You can put up to 8 shards on one raw clay pot: 4 on the sides and 4 on the corners.
* If you light up a fire or a campfire underneath it, it will harden after some time and become a Clay Pot.

![Hardening the Raw Clay Pot (1)](https://user-images.githubusercontent.com/46106832/191838419-c52d8aa4-f76d-4e08-be2e-f28645527d40.png "Hardening the Raw Clay Pot (1)")

### Clay Pots (AKA Hardened Clay Pots)
This is a container block, that has 9 item slots (not counting shards). The player cannot insert items into it directly, but should rather use a hopper or a dropper to insert items. However, after inserting, there is no way to get these items back without breaking the pot. Think about it like a [Piggy Bank](https://en.wikipedia.org/wiki/Piggy_bank).
* [Shulker Boxes](https://minecraft.fandom.com/wiki/Shulker_Box) or other Clay Pots cannot be inserted into a Clay Pot.
* If you break the pot with [Silk Touch](https://minecraft.fandom.com/wiki/Silk_Touch) enchantment, you will get the Clay Pot with all contents. It could be thought of like early-game [Shulker Box](https://minecraft.fandom.com/wiki/Shulker_Box) with less space and no reusability.

![Hardening the Raw Clay Pot (2)](https://user-images.githubusercontent.com/46106832/191838346-1d584fe8-beaa-4dbd-a612-eb84387c3fd6.png "Hardening the Raw Clay Pot (2)")

You can dye them by using [Dye](https://minecraft.fandom.com/wiki/Dye) on an uncolored Clay Pot, and you can remove dye using [Water Bottle](https://minecraft.fandom.com/wiki/Water_Bottle) on already dyed ones.

### Excavation Sites
Excavation Sites are structures in where you can find Excavation Blocks. This is the only place where these blocks spawn naturally and where you can find them in Survival. You can also find a Clay Pots with loot in them here! These are all currently existing structures in the mod:
1. Excavation Site Plains
2. Excavation Site Snow
3. Excavation Site Mangrove
4. Excavation Site Desert

![Excavation Site Plains](https://user-images.githubusercontent.com/46106832/191838192-69d867ae-a32b-4ff9-88f5-9b29e5f8afe4.png "Excavation Site Plains")

When you enter any Excavation Site, you get an advancement called "Forgotten Legacy".

## Unimplemented Features (yet)
* Copper Brush enchantments
* (Maybe) [The Copper Golem](https://www.youtube.com/watch?v=jVdBhu0KgJo)
