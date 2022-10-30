package net.capsey.archeology.main;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.structures.ExcavationSite;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class Structures {

    public static final TagKey<Structure> EXCAVATION_SITES = TagKey.of(Registry.STRUCTURE_KEY, new Identifier(ArcheologyMod.MOD_ID, "excavation_site"));

    public static StructureType<ExcavationSite> EXCAVATION_SITE;

    public static void onInitialize() {
        EXCAVATION_SITE = Registry.register(
                Registry.STRUCTURE_TYPE,
                new Identifier(ArcheologyMod.MOD_ID, "excavation_site"),
                () -> ExcavationSite.CODEC
        );
    }

}
