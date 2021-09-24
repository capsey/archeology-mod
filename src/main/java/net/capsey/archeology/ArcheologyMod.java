package net.capsey.archeology;

import java.util.function.Consumer;

import net.capsey.archeology.blocks.ExcavationBlock;
import net.capsey.archeology.blocks.ExcavationBlockEntity;
import net.capsey.archeology.blocks.FallingExcavationBlock;
import net.capsey.archeology.items.CopperBrush;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ArcheologyMod implements ModInitializer {

    // Items
    public static final Item COPPER_BRUSH = new CopperBrush(new Item.Settings().maxDamage(64).group(ItemGroup.TOOLS));

    // Blocks
    public static final Block EXCAVATION_DIRT = new ExcavationBlock(FabricBlockSettings.copyOf(Blocks.DIRT).breakByTool(FabricToolTags.SHOVELS).hardness(1.0F));
    public static final Block EXCAVATION_GRAVEL = new FallingExcavationBlock(FabricBlockSettings.copyOf(Blocks.GRAVEL).breakByTool(FabricToolTags.SHOVELS).hardness(1.2F), Blocks.GRAVEL);

    public static BlockEntityType<ExcavationBlockEntity> EXCAVATION_BLOCK_ENTITY;

    // Loot
    public static final LootContextType EXCAVATION = createLootContextType((builder) -> {
		builder.require(LootContextParameters.TOOL).allow(LootContextParameters.THIS_ENTITY).allow(LootContextParameters.BLOCK_ENTITY);
	});

    // Sounds
    public static final Identifier BRUSHING_SOUND_ID = new Identifier("archeology:brushing");
    public static SoundEvent BRUSHING_SOUND_EVENT = new SoundEvent(BRUSHING_SOUND_ID);

    @Override
    public void onInitialize() {
        // Items
        Registry.register(Registry.ITEM, new Identifier("archeology", "copper_brush"), COPPER_BRUSH);

        // Blocks
        Registry.register(Registry.BLOCK, new Identifier("archeology", "excavation_dirt"), EXCAVATION_DIRT);
        Registry.register(Registry.ITEM, new Identifier("archeology", "excavation_dirt"), new BlockItem(EXCAVATION_DIRT, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier("archeology", "excavation_gravel"), EXCAVATION_GRAVEL);
        Registry.register(Registry.ITEM, new Identifier("archeology", "excavation_gravel"), new BlockItem(EXCAVATION_GRAVEL, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        EXCAVATION_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, "archeology:excavation_block_entity", FabricBlockEntityTypeBuilder.create(ExcavationBlockEntity::new, EXCAVATION_DIRT, EXCAVATION_GRAVEL).build(null));

        // Sounds
        Registry.register(Registry.SOUND_EVENT, BRUSHING_SOUND_ID, BRUSHING_SOUND_EVENT);
    }

    private static LootContextType createLootContextType(Consumer<LootContextType.Builder> type) {
		LootContextType.Builder builder = new LootContextType.Builder();
		type.accept(builder);
		LootContextType lootContextType = builder.build();

        return lootContextType;
	}
    
}
