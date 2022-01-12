package net.capsey.archeology;

import net.capsey.archeology.blocks.clay_pot.ClayPotBlock;
import net.capsey.archeology.blocks.clay_pot.RawClayPotBlock;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlock;
import net.capsey.archeology.blocks.excavation_block.FallingExcavationBlock;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FallingBlock;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Blocks {

	// Blocks
	public static Block EXCAVATION_DIRT;
	public static Block EXCAVATION_GRAVEL;

	public static Block RAW_CLAY_POT;
	public static Block CLAY_POT;

	// Block Tags
	public static final Tag<Block> EXCAVATION_BLOCKS_TAG = TagFactory.BLOCK.create(new Identifier(ArcheologyMod.MODID, "excavation_blocks"));

	public static final Tag<Block> CLAY_POTS_TAG = TagFactory.BLOCK.create(new Identifier(ArcheologyMod.MODID, "clay_pots"));
	public static final Tag<Block> CLAY_POT_PLANTABLE_TAG = TagFactory.BLOCK.create(new Identifier(ArcheologyMod.MODID, "clay_pot_plantable"));

	public static void onInitialize() {
		EXCAVATION_DIRT = register("excavation_dirt", new ExcavationBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.DIRT).hardness(1.0F)));
		EXCAVATION_GRAVEL = register("excavation_gravel", new FallingExcavationBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.GRAVEL).hardness(1.2F).ticksRandomly(), (FallingBlock) net.minecraft.block.Blocks.GRAVEL));
		RAW_CLAY_POT = register("raw_clay_pot", new RawClayPotBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.CLAY).nonOpaque()));
		CLAY_POT = register("clay_pot", new ClayPotBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.TERRACOTTA).nonOpaque().strength(0.6F).sounds(ClayPotBlock.SOUND_GROUP)));
	}

	private static Block register(String id, Block block) {
		return Registry.register(Registry.BLOCK, new Identifier(ArcheologyMod.MODID, id), block);
	}
	
}
