# Archeology Mod
Archeology Mod is a [Fabric](https://fabricmc.net/) mod for [Minecraft](https://www.minecraft.net/), that adds Archeology System! My goal was to make it as Vanilla-like as I possibly could, to try to imagine how Mojang will implement it (and maybe even give them inspiration).

_This is my first-ever mod, so don't expect too stable experience or sensible code. If you found a bug, or have a suggestion, advice, please create a [GitHub Issue](https://github.com/capsey/archeology-mod/issues) or message me on Discord._

[![CurseForge Badge](https://cf.way2muchnoise.eu/versions/539957.svg "CurseForge Badge")](https://www.curseforge.com/minecraft/mc-mods/archeology)

![Fancy screenie](https://i.ibb.co/4s0pb9F/screenie.png "Fancy screenie")

**The mod requires [Cloth Config API (Fabric)](https://github.com/shedaniel/cloth-config) and [Fabric API](https://github.com/FabricMC/fabric) to work! Also, this is recommended to use [Mod Menu](https://github.com/TerraformersMC/ModMenu) to easily edit mod's config.**

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
* Less durability left on a brush, more oxidized it gets, until it breaks.
* More oxidized brush is, rarer loot you get (similar to [Luck of the Sea](https://minecraft.fandom.com/wiki/Luck_of_the_Sea) enchantment), but harder to control brushing.

![Excavation Dirt (Golden Helmet)](https://i.ibb.co/pxgX1ft/golden-helmet.png "Excavation Dirt (Golden Helmet)")

### Brushing Excavation Blocks
New block type — Excavation Block! Currently, there are two excavation blocks in the mod: Excavation Dirt and Gravel, since this is two blocks that was shown to be able to be brushed by Mojang.
* They look similar to regular block (e.g., Dirt or Gravel), but have white stripes, and you can use a Copper Brush on them to start brushing layers off.
* When fully brushed off, the block drops random loot (depending on excavation site you found it, and oxidation level of the brush)
* While brushing, you should move your camera to stop the block from breaking. If you don't move enough, the block will break eventually, and you won't get anything. The more oxidized your brush is, the faster the block breaks, and therefore faster you need to move.
	> Actually, Mojang showed this feature as completely opposite: if you move your screen too much, the block will break. You can enable this instead, if you prefer it more than mine.

![Excavation Dirt (Breaking)](https://i.ibb.co/QNkhBWZ/breaking.png "Excavation Dirt (Breaking)")

### Ceramic Shards
Ceramic Shards are items, that are dropped from brushing the Excavation Blocks. You can place them on the Raw Clay Pot, so that when hardened, this pot will remain placed shards and have pretty drawing on it.
* Different shards have different drawings on them, and the drawing of the shard depends on the Excavation Site you found it.

### Raw Clay Pots
This is block, that used to make (Hardened) Clay Pot. It is made of 5 [Clay Balls](https://minecraft.fandom.com/wiki/Clay_Ball). When broken, it drops 2-4 clay balls (and shards, if had). The fastest tool is shovel.
* You can put up to 8 shards on one raw clay pot: 4 on the sides and 4 on the corners.
* If you light up a fire or a campfire underneath it, it will harden after some time and become a Clay Pot.

![Hardening the Raw Clat Pot (1)](https://i.ibb.co/bsM6NgC/hardening-2.png "Hardening the Raw Clat Pot (1)")

### Clay Pots (AKA Hardened Clay Pots)
This is a container block, that have 9 item slots (shards contained separately). The player cannot interact with this container in any way, and should rather use a hopper or a dropper to insert items. However, after inserting, there's no way to get these items back without breaking the pot. Think about it like a [Piggy Bank](https://en.wikipedia.org/wiki/Piggy_bank).
* Clay Pots are [Gravity-Affected Blocks](https://minecraft.fandom.com/wiki/Falling_Block), and preserve inserted items after landing. This makes it sorta-movable container block.
* If a Clay Pot don't find a place to land (like [sand landed on a torch](https://minecraft.fandom.com/wiki/Falling_Block#Behavior)), or despawn over time, it will break and pop all items in it, without block itself.

![Hardening the Raw Clat Pot (2)](https://i.ibb.co/k0K92yB/hardening-1.png "Hardening the Raw Clat Pot (2)")

### Excavation Sites
Excavation Sites are structures in where you can find Excavation Blocks. This is only place where these blocks spawn naturally and where you can find them in Survival. Currently there is only one type of Excavation Site - Ancient Ruins. You can find them in such biomes as Plains, Savanna, Desert, Forest, Birch Forest and Taiga.

When you enter any Excavation Site you get an advancement called "Forgotten Legacy".

## Unimplemented Features (yet)
* Dyeing the Clay Pots (separate from Ceramic Shards)
* Copper Brush enchantments
* More ceramic shards
* (Maybe) [The Copper Golem](https://www.youtube.com/watch?v=jVdBhu0KgJo)
