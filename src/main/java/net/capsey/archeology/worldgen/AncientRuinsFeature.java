package net.capsey.archeology.worldgen;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.PostPlacementProcessor;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import org.apache.logging.log4j.Level;

import java.util.Optional;

public class AncientRuinsFeature extends StructureFeature<StructurePoolFeatureConfig> {
    public AncientRuinsFeature() {
        super(StructurePoolFeatureConfig.CODEC, AncientRuinsFeature::createPiecesGenerator, PostPlacementProcessor.EMPTY);
    }

    public static Optional<StructurePiecesGenerator<StructurePoolFeatureConfig>> createPiecesGenerator(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context) {
        BlockPos blockpos = context.chunkPos().getCenterAtY(0);

        Optional<StructurePiecesGenerator<StructurePoolFeatureConfig>> structurePiecesGenerator =
                StructurePoolBasedGenerator.generate(
                        context,
                        PoolStructurePiece::new,
                        blockpos,
                        false,
                        true
                );

        if (structurePiecesGenerator.isPresent()) {
            ArcheologyMod.LOGGER.log(Level.DEBUG, "Ancient Ruins at {}", blockpos);
        }

        return structurePiecesGenerator;
    }
}