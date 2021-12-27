package net.capsey.archeology.world.gen;

import com.mojang.serialization.Codec;

import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class AncientRuinsFeature extends StructureFeature<DefaultFeatureConfig> {

	public AncientRuinsFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec, StructureGeneratorFactory.simple(StructureGeneratorFactory.checkForBiomeOnTop(Heightmap.Type.WORLD_SURFACE_WG), AncientRuinsFeature::addPieces));
    }

	public static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context) {
		ChunkPos pos = context.chunkPos();

		int y = context.chunkGenerator().getHeightOnGround(pos.getStartX(), pos.getStartZ(), Heightmap.Type.WORLD_SURFACE_WG, context.world());
		BlockPos blockPos = new BlockPos(pos.getStartX(), y - 1, pos.getStartZ());
		BlockRotation rotation = BlockRotation.random(context.random());
		
		AncientRuinsGenerator.addPieces(context.structureManager(), blockPos, rotation, collector, context.random());
	}

}
