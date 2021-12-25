package net.capsey.archeology.world.gen;

import com.mojang.serialization.Codec;

import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class AncientRuinsFeature extends StructureFeature<DefaultFeatureConfig> {

	public AncientRuinsFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
		return AncientRuinsFeature.Start::new;
	}

	public static class Start extends StructureStart<DefaultFeatureConfig> {

		public Start(StructureFeature<DefaultFeatureConfig> structureFeature, ChunkPos chunkPos, int i, long l) {
			super(structureFeature, chunkPos, i, l);
		}

		// Called when the world attempts to spawn in a new structure,
		// and is the gap between your feature and generator.
		@Override
		public void init(DynamicRegistryManager registry, ChunkGenerator generator, StructureManager manager, ChunkPos pos, Biome biome, DefaultFeatureConfig config, HeightLimitView world) {
			int y = generator.getHeightOnGround(pos.getStartX(), pos.getStartZ(), Heightmap.Type.WORLD_SURFACE_WG, world);
			BlockPos blockPos = new BlockPos(pos.getStartX(), y - 1, pos.getStartZ());
			BlockRotation rotation = BlockRotation.random(this.random);
			
			AncientRuinsGenerator.addPieces(manager, blockPos, rotation, this, this.random);
		}

	}

}
