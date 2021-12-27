package net.capsey.archeology.blocks;

import net.capsey.archeology.entity.FallingBlockEntityMixinInterface;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public interface FallingBlockWithEntity {
    
    public static final int FALL_DELAY = 2;

    default ItemEntity dropItem(FallingBlockEntity entity, ItemConvertible item) {
        return entity.dropItem(item);
    }

    default NbtCompound writeFallingBlockNbt(BlockEntity entity) {
        return entity.createNbt();
    }

    default NbtCompound writeClientData(NbtCompound nbt, BlockEntity entity) {
        return nbt;
    }

    default boolean canFallThrough(WorldAccess world, BlockPos pos) {
        return FallingBlock.canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= world.getBottomY();
    }

	default void tryScheduleTick(WorldAccess world, BlockPos pos, Block block) {
		if (canFallThrough(world, pos)) {
            world.createAndScheduleBlockTick(pos, block, FALL_DELAY);
		}
	}

	default void trySpawnFallingBlock(BlockState state, World world, BlockPos pos, boolean dropItem) {
		if (!world.isClient && canFallThrough(world, pos)) {
            FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state);
            fallingBlockEntity.dropItem = dropItem;
            
            BlockEntity entity = world.getBlockEntity(pos);

            if (entity != null) {
                fallingBlockEntity.blockEntityData = writeFallingBlockNbt(entity);
                ((FallingBlockEntityMixinInterface) fallingBlockEntity).setClientBlockEntityData(writeClientData(new NbtCompound(), entity));
            }

            world.spawnEntity(fallingBlockEntity);
		}
	}

}
