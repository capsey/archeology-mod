package net.capsey.archeology.blocks.excavation_block;

import java.util.Random;

import net.capsey.archeology.blocks.FallingBlockWithBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FallingExcavationBlock extends ExcavationBlock implements FallingBlockWithBlockEntity {

    private FallingBlock mimicingBlock;

    public FallingExcavationBlock(Settings settings, FallingBlock block) {
        super(settings);
        mimicingBlock = block;
    }

	@Override
	public boolean overrideDroppedItem() {
        return true;
    }

	@Override
    public ItemConvertible getStackOnDestroy() {
        return mimicingBlock;
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
		if (this.canFallThrough(world, pos)) {
            int level = state.get(ExcavationBlock.BRUSHING_LEVEL);

			if (level == 0) {
				this.trySpawnFallingBlock(state, world, pos, true);
			} else {
				world.breakBlock(pos, true);
			}
		} else {
			super.scheduledTick(state, world, pos, random);
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		mimicingBlock.randomDisplayTick(state, world, pos, random);
	}
    
}
