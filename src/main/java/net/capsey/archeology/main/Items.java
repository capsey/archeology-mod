package net.capsey.archeology.main;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.items.CopperBrushItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.Comparator;

public class Items {

    public static Item COPPER_BRUSH;
    public static Item EXCAVATION_GRAVEL;
    public static Item EXCAVATION_DIRT;

    public static Item RAW_CLAY_POT;
    public static Item CLAY_POT;
    public static Item[] CLAY_POT_DYED;

    public static void onInitialize() {
        COPPER_BRUSH = register("copper_brush", new CopperBrushItem(new Item.Settings().maxDamage(64).group(ItemGroup.TOOLS)));

        EXCAVATION_DIRT = register("excavation_dirt", new BlockItem(Blocks.EXCAVATION_DIRT, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
        EXCAVATION_GRAVEL = register("excavation_gravel", new BlockItem(Blocks.EXCAVATION_GRAVEL, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

        RAW_CLAY_POT = register("raw_clay_pot", new BlockItem(Blocks.RAW_CLAY_POT, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
        CLAY_POT = register("clay_pot", new BlockItem(Blocks.CLAY_POT, new FabricItemSettings().maxCount(1).group(ItemGroup.DECORATIONS)));
        CLAY_POT_DYED = Arrays.stream(DyeColor.values())
                .sorted(Comparator.comparingInt(DyeColor::getId))
                .map(x -> register(x.getName() + "_clay_pot", new BlockItem(Blocks.CLAY_POT_DYED[x.getId()], new FabricItemSettings().maxCount(1).group(ItemGroup.DECORATIONS))))
                .toArray(Item[]::new);
    }

    private static Item register(String id, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(ArcheologyMod.MOD_ID, id), item);
    }

}
