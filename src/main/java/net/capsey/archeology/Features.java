package net.capsey.archeology;

import java.util.List;

import net.capsey.archeology.world.gen.AncientRuinsFeature;
import net.capsey.archeology.world.gen.AncientRuinsGenerator;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePieceType.ManagerAware;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class Features {

	public static final StructurePieceType ANCIENT_RUINS_PIECE = Registry.register(Registry.STRUCTURE_PIECE, "aruins", (ManagerAware) AncientRuinsGenerator.Piece::new);

	private static final StructureFeature<DefaultFeatureConfig> ANCIENT_RUINS = new AncientRuinsFeature(DefaultFeatureConfig.CODEC);
	private static final ConfiguredStructureFeature<?, ?> CONFIGURED_ANCIENT_RUINS = ANCIENT_RUINS.configure(FeatureConfig.DEFAULT);

	private static final List<RegistryKey<Biome>> ANCIENT_RUINS_BIOMES = List.of(BiomeKeys.PLAINS, BiomeKeys.SAVANNA, BiomeKeys.DESERT, BiomeKeys.FOREST, BiomeKeys.BIRCH_FOREST, BiomeKeys.TAIGA);

	public static void registerDefaultFeatures() {
		// Registering a Surface Structure
		FabricStructureBuilder.create(new Identifier(ArcheologyMod.MODID, "ancient_ruins"), ANCIENT_RUINS)
				.step(GenerationStep.Feature.SURFACE_STRUCTURES)
				.defaultConfig(new StructureConfig(20, 8, 14357621))
				.adjustsSurface()
				.register();

		// Registering Configured Structure
		RegistryKey<ConfiguredStructureFeature<?, ?>> ancientRuins = RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(ArcheologyMod.MODID, "ancient_ruins"));
		Registry.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, ancientRuins.getValue(), CONFIGURED_ANCIENT_RUINS);

		// Adding to Biomes
		BiomeModifications.create(new Identifier(ArcheologyMod.MODID, "ancient_ruins_addition")).add(ModificationPhase.ADDITIONS,
			context -> ANCIENT_RUINS_BIOMES.contains(context.getBiomeKey()),
			context -> context.getGenerationSettings().addBuiltInStructure(CONFIGURED_ANCIENT_RUINS)
		);
	}
	
}
