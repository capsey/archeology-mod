package net.capsey.archeology.main;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.clay_pot.ClayPotBlock;
import net.capsey.archeology.blocks.clay_pot.RawClayPotBlock;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlock;
import net.capsey.archeology.blocks.excavation_block.FallingExcavationBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FallingBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.Arrays;

public class Blocks {

    // Block Tags
    public static final TagKey<Block> EXCAVATION_BLOCKS_TAG = TagKey.of(RegistryKeys.BLOCK, new Identifier(ArcheologyMod.MOD_ID, "excavation_blocks"));
    public static final TagKey<Block> CLAY_POTS_TAG = TagKey.of(RegistryKeys.BLOCK, new Identifier(ArcheologyMod.MOD_ID, "clay_pots"));
    public static final TagKey<Block> CLAY_POT_PLANTABLE_TAG = TagKey.of(RegistryKeys.BLOCK, new Identifier(ArcheologyMod.MOD_ID, "clay_pot_plantable"));

    // Blocks
    public static Block EXCAVATION_DIRT;
    public static Block EXCAVATION_GRAVEL;
    public static Block EXCAVATION_RED_SAND;
    public static Block EXCAVATION_SAND;
    public static Block RAW_CLAY_POT;
    public static Block CLAY_POT;
    public static Block[] CLAY_POT_DYED;

    public static void onInitialize() {
        EXCAVATION_DIRT = register("excavation_dirt", new ExcavationBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.DIRT).hardness(1.0F)));
        EXCAVATION_GRAVEL = register("excavation_gravel", new FallingExcavationBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.GRAVEL).hardness(1.2F), (FallingBlock) net.minecraft.block.Blocks.GRAVEL));
        EXCAVATION_RED_SAND = register("excavation_red_sand", new FallingExcavationBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.RED_SAND).hardness(1.0F), (FallingBlock) net.minecraft.block.Blocks.RED_SAND));
        EXCAVATION_SAND = register("excavation_sand", new FallingExcavationBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.SAND).hardness(1.0F), (FallingBlock) net.minecraft.block.Blocks.SAND));

        RAW_CLAY_POT = register("raw_clay_pot", new RawClayPotBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.CLAY).nonOpaque()));
        CLAY_POT = register("clay_pot", new ClayPotBlock(null, AbstractBlock.Settings.copy(net.minecraft.block.Blocks.TERRACOTTA).nonOpaque().strength(0.6F).sounds(ClayPotBlock.SOUND_GROUP)));
        CLAY_POT_DYED = Arrays.stream(DyeColor.values())
                .map(x -> register(x.getName() + "_clay_pot", new ClayPotBlock(x, AbstractBlock.Settings.copy(net.minecraft.block.Blocks.TERRACOTTA).nonOpaque().strength(0.6F).sounds(ClayPotBlock.SOUND_GROUP))))
                .toArray(Block[]::new);
    }

    private static Block register(String id, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(ArcheologyMod.MOD_ID, id), block);
    }

}
