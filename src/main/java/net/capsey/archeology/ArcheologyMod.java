package net.capsey.archeology;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ArcheologyMod implements ModInitializer {

    public static final Item COPPER_BRUSH = new CopperBrush(new Item.Settings().maxDamage(238).group(ItemGroup.TOOLS));

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("archeology", "copper_brush"), COPPER_BRUSH);
    }
    
}
