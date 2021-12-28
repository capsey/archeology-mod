package net.capsey.archeology;

import net.capsey.archeology.items.CopperBrushItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Items {

	public static final Item COPPER_BRUSH = register("copper_brush", new CopperBrushItem(new Item.Settings().maxDamage(64).group(ItemGroup.TOOLS)));

	public static final Item EXCAVATION_DIRT = register("excavation_dirt", new BlockItem(Blocks.EXCAVATION_DIRT, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
	public static final Item EXCAVATION_GRAVEL = register("excavation_gravel", new BlockItem(Blocks.EXCAVATION_GRAVEL, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

	public static final Item RAW_CLAY_POT = register("raw_clay_pot", new BlockItem(Blocks.RAW_CLAY_POT, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
	public static final Item CLAY_POT = register("clay_pot", new BlockItem(Blocks.CLAY_POT, new FabricItemSettings().maxCount(1).group(ItemGroup.DECORATIONS)));

	private static Item register(String id, Item item) {
		return Registry.register(Registry.ITEM, new Identifier(ArcheologyMod.MODID, id), item);
	}
	
}
