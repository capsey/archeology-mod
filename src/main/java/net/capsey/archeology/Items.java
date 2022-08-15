package net.capsey.archeology;

import net.capsey.archeology.items.ChiselItem;
import net.capsey.archeology.items.CopperBrushItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Items {

    // Tools
    public static Item COPPER_BRUSH;
    public static Item CHISEL;

    // Excavation Blocks
    public static Item EXCAVATION_GRAVEL;
    public static Item EXCAVATION_DIRT;

    // Clay Pots
    public static Item CLAY_POT;
    public static Item RAW_CLAY_POT;

    // New stone variants
    public static Item CALCITE_BRICKS;
    public static Item CALCITE_PILLAR;

    public static void onInitialize() {
        COPPER_BRUSH = register("copper_brush", new CopperBrushItem(new Item.Settings().maxDamage(64).group(ItemGroup.TOOLS)));
        CHISEL = register("chisel", new ChiselItem(new Item.Settings().maxDamage(251).group(ItemGroup.TOOLS)));

        EXCAVATION_DIRT = register("excavation_dirt", new BlockItem(Blocks.EXCAVATION_DIRT, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
        EXCAVATION_GRAVEL = register("excavation_gravel", new BlockItem(Blocks.EXCAVATION_GRAVEL, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        RAW_CLAY_POT = register("raw_clay_pot", new BlockItem(Blocks.RAW_CLAY_POT, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
        CLAY_POT = register("clay_pot", new BlockItem(Blocks.CLAY_POT, new FabricItemSettings().maxCount(1).group(ItemGroup.DECORATIONS)));

        CALCITE_BRICKS = register("calcite_bricks", new BlockItem(Blocks.CALCITE_BRICKS, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
        CALCITE_PILLAR = register("calcite_pillar", new BlockItem(Blocks.CALCITE_PILLAR, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
    }

    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(ArcheologyMod.MOD_ID, id), item);
    }

}
