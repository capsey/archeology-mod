package net.capsey.archeology.main;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.clay_pot.ClayPotBlockEntity;
import net.capsey.archeology.blocks.clay_pot.RawClayPotBlockEntity;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.stream.Stream;

public class BlockEntities {

    public static BlockEntityType<ExcavationBlockEntity> EXCAVATION_BLOCK_ENTITY;
    public static BlockEntityType<RawClayPotBlockEntity> RAW_CLAY_POT_BLOCK_ENTITY;
    public static BlockEntityType<ClayPotBlockEntity> CLAY_POT_BLOCK_ENTITY;

    public static void onInitialize() {
        EXCAVATION_BLOCK_ENTITY = register("excavation_block_entity", ExcavationBlockEntity::new, Blocks.EXCAVATION_DIRT, Blocks.EXCAVATION_GRAVEL, Blocks.EXCAVATION_SAND);
        RAW_CLAY_POT_BLOCK_ENTITY = register("raw_clay_pot_block_entity", RawClayPotBlockEntity::new, Blocks.RAW_CLAY_POT);
        CLAY_POT_BLOCK_ENTITY = register("clay_pot_block_entity", ClayPotBlockEntity::new, Stream.concat(Arrays.stream(Blocks.CLAY_POT_DYED), Stream.of(Blocks.CLAY_POT)).toArray(Block[]::new));
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, FabricBlockEntityTypeBuilder.Factory<? extends T> factory, Block... blocks) {
        FabricBlockEntityTypeBuilder<T> builder = FabricBlockEntityTypeBuilder.create(factory, blocks);
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, ArcheologyMod.MOD_ID + ":" + id, builder.build(null));
    }

}
