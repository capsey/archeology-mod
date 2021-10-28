package net.capsey.archeology.blocks.excavation_block;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.LandingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FallingExcavationBlock extends ExcavationBlock implements LandingBlock {

    private FallingBlock mimicingBlock;

    public FallingExcavationBlock(Settings settings, FallingBlock block) {
        super(settings);
        mimicingBlock = block;
    }

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		world.getBlockTickScheduler().schedule(pos, this, getFallDelay());
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		world.getBlockTickScheduler().schedule(pos, this, getFallDelay());
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (FallingBlock.canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= world.getBottomY()) {
            int level = world.getBlockState(pos).get(ExcavationBlock.BRUSHING_LEVEL);
            BlockEntity entity = world.getBlockEntity(pos);

			if (entity instanceof ExcavationBlockEntity) {
				((ExcavationBlockEntity) entity).breakBlock();

				if (level == 0) {
					world.setBlockState(pos, mimicingBlock.getDefaultState());
					FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, world.getBlockState(pos));
					world.spawnEntity(fallingBlockEntity);
				}
			}

		}
	}

	private int getFallDelay() {
		return 2;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		mimicingBlock.randomDisplayTick(state, world, pos, random);
	}
    
}
