package net.capsey.archeology.blocks.excavation_block;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.main.BlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.GameRules;

public class ExcavationBlockEntity extends FossilContainerBlockEntity {

    public ExcavationBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.EXCAVATION_BLOCK_ENTITY, pos, state, null);
    }

    @Override
    public void dropLoot(ServerPlayerEntity player) {
        super.dropLoot(player);

        if (world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            int experience = UniformIntProvider.create(2, 5).get(world.random);
            if (experience > 0) {
                ExperienceOrbEntity.spawn((ServerWorld) world, Vec3d.ofCenter(pos), experience);
            }
        }

        player.incrementStat(ArcheologyMod.EXCAVATED);
        ArcheologyMod.EXCAVATION_SUCCESS_CRITERION.trigger(player, getCachedState());
    }
}
