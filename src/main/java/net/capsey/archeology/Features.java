package net.capsey.archeology;

import net.capsey.archeology.mixin.world.StructureFeatureAccessor;
import net.capsey.archeology.worldgen.AncientRuinsFeature;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;

public class Features {
    public static StructureFeature<?> ANCIENT_RUINS = new AncientRuinsFeature();

    public static void onInitialize() {
        StructureFeatureAccessor.callRegister(ArcheologyMod.MOD_ID + ":ancient_ruins", ANCIENT_RUINS, GenerationStep.Feature.SURFACE_STRUCTURES);
    }
}