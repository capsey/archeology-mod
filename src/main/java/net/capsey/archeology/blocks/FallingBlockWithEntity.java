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
    
    public static final int fallDelay = 2;

    default ItemEntity dropItem(FallingBlockEntity entity, ItemConvertible item) {
        return entity.dropItem(item);
    }

    default NbtCompound writeFallingBlockNbt(NbtCompound nbt, BlockEntity entity) {
        return entity.writeNbt(nbt);
    }

    default NbtCompound writeClientData(NbtCompound nbt, BlockEntity entity) {
        return nbt;
    }

    default boolean canFallThrough(WorldAccess world, BlockPos pos) {
        return FallingBlock.canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= world.getBottomY();
    }

	default void tryScheduleTick(WorldAccess world, BlockPos pos, Block block) {
		if (canFallThrough(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, block, fallDelay);
		}
	}

	default void trySpawnFallingBlock(BlockState state, World world, BlockPos pos, boolean dropItem) {
		if (!world.isClient && canFallThrough(world, pos)) {
            FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state);
            fallingBlockEntity.dropItem = dropItem;
            
            BlockEntity entity = world.getBlockEntity(pos);

            if (entity != null) {
                fallingBlockEntity.blockEntityData = writeFallingBlockNbt(new NbtCompound(), entity);
                ((FallingBlockEntityMixinInterface) fallingBlockEntity).setClientBlockEntityData(writeClientData(new NbtCompound(), entity));
            }

            world.spawnEntity(fallingBlockEntity);
		}
	}

}
