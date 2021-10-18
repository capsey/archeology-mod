package net.capsey.archeology;

import java.util.function.Consumer;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.capsey.archeology.blocks.clay_pot.ClayPotBlock;
import net.capsey.archeology.blocks.clay_pot.ClayPotBlockEntity;
import net.capsey.archeology.blocks.clay_pot.RawClayPotBlock;
import net.capsey.archeology.blocks.clay_pot.RawClayPotBlockEntity;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlock;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.capsey.archeology.blocks.excavation_block.FallingExcavationBlock;
import net.capsey.archeology.items.CeramicShards;
import net.capsey.archeology.items.CopperBrushItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ArcheologyMod implements ModInitializer {

    // Tags
    public static final Tag<Block> CLAY_POTS_TAG = TagFactory.BLOCK.create(new Identifier("archeology", "clay_pots"));
    public static final Tag<Block> CLAY_POT_PLANTABLE_TAG = TagFactory.BLOCK.create(new Identifier("archeology", "clay_pot_plantable"));

    // Items
    public static final Item COPPER_BRUSH = new CopperBrushItem(new Item.Settings().maxDamage(64).group(ItemGroup.TOOLS));

    // Blocks
    public static final Block EXCAVATION_DIRT = new ExcavationBlock(FabricBlockSettings.copyOf(Blocks.DIRT).hardness(1.0F));
    public static final Block EXCAVATION_GRAVEL = new FallingExcavationBlock(FabricBlockSettings.copyOf(Blocks.GRAVEL).hardness(1.2F), Blocks.GRAVEL);

    public static final Block RAW_CLAY_POT = new RawClayPotBlock(FabricBlockSettings.copyOf(Blocks.CLAY).ticksRandomly()); // .hardness(1.4F)
    public static final Block CLAY_POT = new ClayPotBlock(FabricBlockSettings.copyOf(Blocks.TERRACOTTA).sounds(ClayPotBlock.SOUND_GROUP));

    public static BlockEntityType<ExcavationBlockEntity> EXCAVATION_BLOCK_ENTITY;
    public static BlockEntityType<RawClayPotBlockEntity> RAW_CLAY_POT_BLOCK_ENTITY;
    public static BlockEntityType<ClayPotBlockEntity> CLAY_POT_BLOCK_ENTITY;

    // Loot
    public static final LootContextType EXCAVATION = createLootContextType((builder) -> {
		builder.require(LootContextParameters.TOOL).allow(LootContextParameters.THIS_ENTITY).allow(LootContextParameters.BLOCK_ENTITY);
	});

    // Sounds
    public static final Identifier BRUSHING_SOUND_ID = new Identifier("archeology:item.copper_brush.brushing");
    public static SoundEvent BRUSHING_SOUND_EVENT = new SoundEvent(BRUSHING_SOUND_ID);

    @Override
    public void onInitialize() {
        // Items
        Registry.register(Registry.ITEM, new Identifier("archeology", "copper_brush"), COPPER_BRUSH);
        CeramicShards.registerDefaultShards();

        // Blocks
        Registry.register(Registry.BLOCK, new Identifier("archeology", "excavation_dirt"), EXCAVATION_DIRT);
        Registry.register(Registry.ITEM, new Identifier("archeology", "excavation_dirt"), new BlockItem(EXCAVATION_DIRT, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier("archeology", "excavation_gravel"), EXCAVATION_GRAVEL);
        Registry.register(Registry.ITEM, new Identifier("archeology", "excavation_gravel"), new BlockItem(EXCAVATION_GRAVEL, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier("archeology", "raw_clay_pot"), RAW_CLAY_POT);
        Registry.register(Registry.ITEM, new Identifier("archeology", "raw_clay_pot"), new BlockItem(RAW_CLAY_POT, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier("archeology", "clay_pot"), CLAY_POT);
        Registry.register(Registry.ITEM, new Identifier("archeology", "clay_pot"), new BlockItem(CLAY_POT, new FabricItemSettings().maxCount(1).group(ItemGroup.DECORATIONS)));

        EXCAVATION_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "archeology:excavation_block_entity", FabricBlockEntityTypeBuilder.create(ExcavationBlockEntity::new, EXCAVATION_DIRT, EXCAVATION_GRAVEL).build(null));
        RAW_CLAY_POT_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "archeology:raw_clay_pot_block_entity", FabricBlockEntityTypeBuilder.create(RawClayPotBlockEntity::new, RAW_CLAY_POT).build(null));
        CLAY_POT_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "archeology:clay_pot_block_entity", FabricBlockEntityTypeBuilder.create(ClayPotBlockEntity::new, CLAY_POT).build(null));

        // Sounds
        Registry.register(Registry.SOUND_EVENT, BRUSHING_SOUND_ID, BRUSHING_SOUND_EVENT);

        // Config
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
    }

    private static LootContextType createLootContextType(Consumer<LootContextType.Builder> type) {
		LootContextType.Builder builder = new LootContextType.Builder();
		type.accept(builder);
		LootContextType lootContextType = builder.build();

        return lootContextType;
	}
    
}
