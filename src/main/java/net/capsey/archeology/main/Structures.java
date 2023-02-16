package net.capsey.archeology.main;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.structures.ExcavationSite;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class Structures {

    public static final TagKey<Structure> EXCAVATION_SITES = TagKey.of(RegistryKeys.STRUCTURE, new Identifier(ArcheologyMod.MOD_ID, "excavation_site"));

    public static StructureType<ExcavationSite> EXCAVATION_SITE;

    public static void onInitialize() {
        EXCAVATION_SITE = Registry.register(
                Registries.STRUCTURE_TYPE,
                new Identifier(ArcheologyMod.MOD_ID, "excavation_site"),
                () -> ExcavationSite.CODEC
        );
    }

}
