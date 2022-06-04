package net.capsey.archeology;

import net.capsey.archeology.mixin.world.StructureFeatureAccessor;
import net.capsey.archeology.world.gen.AncientRuinsFeature;
import net.capsey.archeology.world.gen.AncientRuinsGenerator;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePieceType.ManagerAware;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class Features {

	private static final TagKey<Biome> ANCIENT_RUINS_HAS_STRUCTURE = TagKey.of(Registry.BIOME_KEY, new Identifier(ArcheologyMod.MODID, "has_structure/end_city"));

	private static final StructureFeature<DefaultFeatureConfig> ANCIENT_RUINS = new AncientRuinsFeature(DefaultFeatureConfig.CODEC);
	private static final ConfiguredStructureFeature<?, ?> CONFIGURED_ANCIENT_RUINS = ANCIENT_RUINS.configure(DefaultFeatureConfig.INSTANCE, ANCIENT_RUINS_HAS_STRUCTURE);

	public static final StructurePieceType ANCIENT_RUINS_PIECE = Registry.register(Registry.STRUCTURE_PIECE, "aruins", (ManagerAware) AncientRuinsGenerator.Piece::new);


	public static void onInitialize() {
		StructureFeatureAccessor.callRegister(ArcheologyMod.MODID + ":ancient_ruins", ANCIENT_RUINS, GenerationStep.Feature.SURFACE_STRUCTURES);
	}
	
}
