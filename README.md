# Archeology Mod
Archeology Mod is a [Fabric](https://fabricmc.net/) mod for [Minecraft](https://www.minecraft.net/), that adds Archeology System! My goal was to make it as Vanilla-like as I possibly could, to try to imagine how Mojang will implement it (and maybe even give them inspiration).

This is my first-ever mod, so don't expect too stable experience. If you found a bug, or have a suggestion, please create a [GitHub Issue](https://github.com/capsey/archeology-mod/issues).

## What is the Archeology System?
Archeology was firstly introduced at [Minecraft Live 2020](https://youtu.be/DWZIfsaIgtE?t=7229), but was later [delayed from 1.17](https://youtu.be/6YgKUZnUyak?t=285) Caves and Cliffs Update.

Announced features of the Archeology System:
* [Excavation Dig Sites](https://minecraft-archive.fandom.com/wiki/Excavation_Sites) (structures)
* [Archeology Brush](https://minecraft-archive.fandom.com/wiki/Brush) (presumably made of Copper and can oxidize)
* Brushing mechanic (removing layers from soft blocks to get loot)
* [Ceramic Shards](https://minecraft-archive.fandom.com/wiki/Ceramic_Shard)
* [Hardened and Raw Clay Pots](https://minecraft-archive.fandom.com/wiki/Clay_Pot)
* Hardening (firing up) Clay Pots

## Mod features
I already implemented almost all the features that was announced (besides dig sites). Some of the features that I added wasn't announced by Mojang and was made up by me. There are full list of all implemented features:
* Copper Brush (AKA Archeology Brush)
	* Made using a [Copper Ingot](https://minecraft.fandom.com/wiki/Copper_Ingot) and a [Feather](https://minecraft.fandom.com/wiki/Feather)
	* Less durability left on a brush, more oxidized it gets, until it breaks. More oxidized brush is, rarer loot you get (similar to [Luck of the Sea](https://minecraft.fandom.com/wiki/Luck_of_the_Sea)), but harder to control brushing.
* Brushing Excavation Blocks
	* New block type — Excavation Block!
	* They look like regular block (e.g., Dirt or Gravel), but you can use a Copper Brush on it to start brushing its layers off.
	* While brushing, you should shake your camera to stop the block from breaking. If you don't shake enough, the block will break eventually, and you won't get anything. The more oxidized your brush is, the faster the block breaks and therefore faster you need to shake. 
		* Actually, Mojang showed this completely opposite: if you shake your screen too much, the block will break. However, this seemed to me too easy. I'm still not sure about this, though, so this is still subject to change
	* When fully brushed off, block drops random loot (depending on excavation site you found it, and oxidation level of the brush)
* Ceramic Shards
	* These are dropped from brushing the Excavation Blocks
	* Different shards have different drawings on them, and the drawing of the shard depends on the Excavation Site you found it.
	* You can place the shard on the Raw Clay Pot. When hardened, this pot will retain placed shards and have pretty drawing on it
* Raw Clay Pots
	* This block is made of Clay Balls. You can place it, but when broken it drops 2-4 clay pots (and placed shards). Fastest tool is shovel
	* If you light up fire of a campfire underneath it, it will harden after some time and become a regular Clay Pot.
* Clay Pots (AKA Hardened Clay Pots)
	* Clay Pots are containers in my mod, but the player can't interact with them directly, but rather use a hopper or dropper to insert items in it. However, after inserting, there's no way to get these items back without breaking the pot. Think about it like a [Piggy Bank](https://en.wikipedia.org/wiki/Piggy_bank). It has 9 slots.
	* Clay Pots is a [Gravity-Affected Block](https://minecraft.fandom.com/wiki/Falling_Block), and preserve inserted items after landing. This makes it sorta-movable container. If a Clay Pot don't find a place to land (like [sand landed on a torch](https://minecraft.fandom.com/wiki/Falling_Block#Behavior)), or despawn over time, it will break and pop all items in it, without block itself.
	* Besides ceramic shards, you can customize pots using [Dyes](https://minecraft.fandom.com/wiki/Dye), but this only creates a colorful pattern around the edges of the pot. You can remove dye from the pot using a [Water Bottle](https://minecraftpc.fandom.com/wiki/Water_Bottle).

Also, I plan to add [The Copper Golem](https://www.youtube.com/watch?v=jVdBhu0KgJo) that lost the Minecraft Live 2021 Mob Vote, because it will fit perfectly into Archeology Theme, but I won't promise anything and firstly finish all other features.
