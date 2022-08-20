package net.capsey.archeology.blocks.excavation_block;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.BlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.GameRules;

public class ExcavationBlockEntity extends FossilContainerBlockEntity {

    public static final Identifier DEFAULT_LOOT_TABLE = new Identifier(ArcheologyMod.MOD_ID, "excavation/ancient_ruins");

    public ExcavationBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.EXCAVATION_BLOCK_ENTITY, pos, state, DEFAULT_LOOT_TABLE);
    }

    public void setLootTable(Identifier id) {
        loot.clear();
        lootTableId = id;
    }

    @Override
    public void dropLoot(PlayerEntity player) {
        super.dropLoot(player);

        // Drop experience
        if (world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            int experience = UniformIntProvider.create(2, 5).get(world.random);
            if (experience > 0) {
                ExperienceOrbEntity.spawn((ServerWorld) world, Vec3d.ofCenter(pos), experience);
            }
        }

        // Give achievement for successful brushing
        // TODO
    }
}
