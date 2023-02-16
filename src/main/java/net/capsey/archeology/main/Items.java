package net.capsey.archeology.main;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.items.CopperBrushItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Oxidizable;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;

public class Items {

    public static Item COPPER_BRUSH;
    public static Item EXPOSED_COPPER_BRUSH;
    public static Item WEATHERED_COPPER_BRUSH;
    public static Item OXIDIZED_COPPER_BRUSH;

    public static Item EXCAVATION_DIRT;
    public static Item EXCAVATION_GRAVEL;
    public static Item EXCAVATION_RED_SAND;
    public static Item EXCAVATION_SAND;

    public static Item RAW_CLAY_POT;
    public static Item CLAY_POT;
    public static Item[] CLAY_POT_DYED;

    public static void onInitialize() {
        COPPER_BRUSH = register("copper_brush", new CopperBrushItem(Oxidizable.OxidationLevel.UNAFFECTED, new Item.Settings().maxDamage(64)), ItemGroups.TOOLS, null);
        EXPOSED_COPPER_BRUSH = register("exposed_copper_brush", new CopperBrushItem(Oxidizable.OxidationLevel.EXPOSED, new Item.Settings().maxDamage(64)), ItemGroups.TOOLS, COPPER_BRUSH);
        WEATHERED_COPPER_BRUSH = register("weathered_copper_brush", new CopperBrushItem(Oxidizable.OxidationLevel.WEATHERED, new Item.Settings().maxDamage(64)), ItemGroups.TOOLS, EXPOSED_COPPER_BRUSH);
        OXIDIZED_COPPER_BRUSH = register("oxidized_copper_brush", new CopperBrushItem(Oxidizable.OxidationLevel.OXIDIZED, new Item.Settings().maxDamage(64)), ItemGroups.TOOLS, WEATHERED_COPPER_BRUSH);

        EXCAVATION_DIRT = register("excavation_dirt", new BlockItem(Blocks.EXCAVATION_DIRT, new FabricItemSettings()), ItemGroups.NATURAL, net.minecraft.item.Items.DIRT);
        EXCAVATION_GRAVEL = register("excavation_gravel", new BlockItem(Blocks.EXCAVATION_GRAVEL, new FabricItemSettings()), ItemGroups.NATURAL, net.minecraft.item.Items.GRAVEL);
        EXCAVATION_RED_SAND = register("excavation_red_sand", new BlockItem(Blocks.EXCAVATION_RED_SAND, new FabricItemSettings()), ItemGroups.NATURAL, net.minecraft.item.Items.RED_SAND);
        EXCAVATION_SAND = register("excavation_sand", new BlockItem(Blocks.EXCAVATION_SAND, new FabricItemSettings()), ItemGroups.NATURAL, net.minecraft.item.Items.SAND);

        RAW_CLAY_POT = register("raw_clay_pot", new BlockItem(Blocks.RAW_CLAY_POT, new FabricItemSettings()), ItemGroups.FUNCTIONAL, null);
        CLAY_POT = register("clay_pot", new BlockItem(Blocks.CLAY_POT, new FabricItemSettings().maxCount(1)), ItemGroups.FUNCTIONAL, RAW_CLAY_POT);
        CLAY_POT_DYED = Arrays.stream(DyeColor.values())
                .sorted(Comparator.comparingInt(DyeColor::getId))
                .map(x -> register(x.getName() + "_clay_pot", new BlockItem(Blocks.CLAY_POT_DYED[x.getId()], new FabricItemSettings().maxCount(1)), ItemGroups.FUNCTIONAL, CLAY_POT))
                .toArray(Item[]::new);
    }

    private static Item register(String id, Item item, ItemGroup group, @Nullable Item after) {
        ItemGroupEvents.modifyEntriesEvent(group).register(content -> {
            if (after != null) {
                content.addAfter(after, item);
            } else {
                content.add(item);
            }
        });

        return Registry.register(Registries.ITEM, new Identifier(ArcheologyMod.MOD_ID, id), item);
    }

}
