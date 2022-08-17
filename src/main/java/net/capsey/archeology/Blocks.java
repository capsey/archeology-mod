package net.capsey.archeology;

import net.capsey.archeology.blocks.chiseled.ChiseledBlock;
import net.capsey.archeology.blocks.chiseled.ChiseledPillarBlock;
import net.capsey.archeology.blocks.clay_pot.ClayPotBlock;
import net.capsey.archeology.blocks.clay_pot.RawClayPotBlock;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlock;
import net.capsey.archeology.blocks.excavation_block.FallingExcavationBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Blocks {

    // Block Tags
    public static final TagKey<Block> EXCAVATION_BLOCKS_TAG = TagKey.of(Registry.BLOCK_KEY, new Identifier(ArcheologyMod.MOD_ID, "excavation_blocks"));
    public static final TagKey<Block> CLAY_POTS_TAG = TagKey.of(Registry.BLOCK_KEY, new Identifier(ArcheologyMod.MOD_ID, "clay_pots"));
    public static final TagKey<Block> CLAY_POT_PLANTABLE_TAG = TagKey.of(Registry.BLOCK_KEY, new Identifier(ArcheologyMod.MOD_ID, "clay_pot_plantable"));

    // Blocks
    public static Block EXCAVATION_DIRT;
    public static Block EXCAVATION_GRAVEL;
    public static Block RAW_CLAY_POT;
    public static Block CLAY_POT;
    public static Block CALCITE_BRICKS;
    public static Block CALCITE_PILLAR;
    public static Block CALCITE_WALL;
    public static Block CHISELED_CALCITE;
    public static Block CHISELED_CALCITE_BRICKS;
    public static Block CHISELED_CALCITE_PILLAR;

    public static void onInitialize() {
        // Excavation Blocks
        EXCAVATION_DIRT = register("excavation_dirt", new ExcavationBlock(FabricBlockSettings.copy(net.minecraft.block.Blocks.DIRT).hardness(1.0F)));
        EXCAVATION_GRAVEL = register("excavation_gravel", new FallingExcavationBlock(FabricBlockSettings.copy(net.minecraft.block.Blocks.GRAVEL).hardness(1.2F).ticksRandomly(), (FallingBlock) net.minecraft.block.Blocks.GRAVEL));

        // Clay Pots
        RAW_CLAY_POT = register("raw_clay_pot", new RawClayPotBlock(FabricBlockSettings.copy(net.minecraft.block.Blocks.CLAY).nonOpaque()));
        CLAY_POT = register("clay_pot", new ClayPotBlock(FabricBlockSettings.copy(net.minecraft.block.Blocks.TERRACOTTA).nonOpaque().strength(0.6F).sounds(ClayPotBlock.SOUND_GROUP)));

        // New stone variants
        CALCITE_BRICKS = register("calcite_bricks", new Block(FabricBlockSettings.copy(net.minecraft.block.Blocks.CALCITE)));
        CALCITE_PILLAR = register("calcite_pillar", new PillarBlock(FabricBlockSettings.copy(net.minecraft.block.Blocks.CALCITE)));
        CALCITE_WALL = register("calcite_wall", new WallBlock(FabricBlockSettings.copy(net.minecraft.block.Blocks.CALCITE)));

        // Chiseled blocks
        CHISELED_CALCITE = register("chiseled_calcite", new ChiseledBlock(net.minecraft.block.Blocks.CALCITE));
        CHISELED_CALCITE_BRICKS = register("chiseled_calcite_bricks", new ChiseledBlock(CALCITE_BRICKS));
        CHISELED_CALCITE_PILLAR = register("chiseled_calcite_pillar", new ChiseledPillarBlock((PillarBlock) CALCITE_PILLAR));
    }

    private static Block register(String id, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(ArcheologyMod.MOD_ID, id), block);
    }

}
