package net.capsey.archeology;

import net.capsey.archeology.blocks.ExcavationBlock;
import net.capsey.archeology.items.CopperBrush;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ArcheologyMod implements ModInitializer {

    // Items
    public static final Item COPPER_BRUSH = new CopperBrush(new Item.Settings().maxDamage(8).group(ItemGroup.TOOLS)); // 238

    // Blocks
    public static final Block EXCAVATION_DIRT = new ExcavationBlock(FabricBlockSettings.copyOf(Blocks.DIRT));
    public static final Block EXCAVATION_GRAVEL = new ExcavationBlock(FabricBlockSettings.copyOf(Blocks.GRAVEL));

    @Override
    public void onInitialize() {
        // Items
        Registry.register(Registry.ITEM, new Identifier("archeology", "copper_brush"), COPPER_BRUSH);

        // Blocks
        Registry.register(Registry.BLOCK, new Identifier("archeology", "excavation_dirt"), EXCAVATION_DIRT);
        Registry.register(Registry.ITEM, new Identifier("archeology", "excavation_dirt"), new BlockItem(EXCAVATION_DIRT, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        Registry.register(Registry.BLOCK, new Identifier("archeology", "excavation_gravel"), EXCAVATION_GRAVEL);
        Registry.register(Registry.ITEM, new Identifier("archeology", "excavation_gravel"), new BlockItem(EXCAVATION_GRAVEL, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
    }
    
}
