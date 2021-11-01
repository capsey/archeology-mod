package net.capsey.archeology.blocks.clay_pot;

import java.util.Random;

import net.capsey.archeology.blocks.FallingBlockWithBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.LandingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ClayPotBlock extends AbstractClayPotBlock implements BlockEntityProvider, FallingBlockWithBlockEntity, LandingBlock {

    public static final BlockSoundGroup SOUND_GROUP = new BlockSoundGroup(1.0F, 1.0F, SoundEvents.BLOCK_GLASS_BREAK, SoundEvents.BLOCK_STONE_STEP, SoundEvents.BLOCK_STONE_PLACE, SoundEvents.BLOCK_STONE_HIT, SoundEvents.BLOCK_STONE_FALL);

    public ClayPotBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ClayPotBlockEntity(pos, state);
    }

	public NbtCompound writeFallingBlockNbt(NbtCompound nbt, BlockEntity entity) {
		nbt = entity.writeNbt(nbt);

		if (entity instanceof ClayPotBlockEntity) {
			((ClayPotBlockEntity) entity).clear();
		}

        return nbt;
    }

	@Override
	public boolean overrideDroppedItem() {
        return true;
    }

	@Override
	public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity entity) {
		DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

		if (entity.blockEntityData != null) {
			Inventories.readNbt(entity.blockEntityData, items);
			items.forEach(entity::dropStack);
		}

		world.playSound(null, pos, getSoundGroup(entity.getBlockState()).getBreakSound(), SoundCategory.AMBIENT, 1.0F, 1.0F);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		this.tryScheduleTick(world, pos, this);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		this.tryScheduleTick(world, pos, this);
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.trySpawnFallingBlock(state, world, pos, true);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof Inventory) {
				ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
				world.updateComparators(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

}
