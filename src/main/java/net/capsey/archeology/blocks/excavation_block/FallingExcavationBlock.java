package net.capsey.archeology.blocks.excavation_block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.LandingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FallingExcavationBlock extends ExcavationBlock implements LandingBlock {

    private Block mimicingBlock;

    public FallingExcavationBlock(Settings settings, Block block) {
        super(settings);
        mimicingBlock = block;
    }

    // Code copied from net.minecraft.block.FallingBlock
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		world.getBlockTickScheduler().schedule(pos, this, this.getFallDelay());
	}

	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		world.getBlockTickScheduler().schedule(pos, this, this.getFallDelay());
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (FallingBlock.canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= world.getBottomY()) {
            int level = world.getBlockState(pos).get(ExcavationBlock.BRUSHING_LEVEL);
            ((ExcavationBlockEntity) world.getBlockEntity(pos)).breakBlock();

            if(level == 0) {
                world.setBlockState(pos, mimicingBlock.getDefaultState());
                FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, world.getBlockState(pos));
                world.spawnEntity(fallingBlockEntity);
            }
		}
	}

	protected int getFallDelay() {
		return 2;
	}

	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(16) == 0) {
			BlockPos blockPos = pos.down();
			if (FallingBlock.canFallThrough(world.getBlockState(blockPos))) {
				double d = (double)pos.getX() + random.nextDouble();
				double e = (double)pos.getY() - 0.05D;
				double f = (double)pos.getZ() + random.nextDouble();
				world.addParticle(new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state), d, e, f, 0.0D, 0.0D, 0.0D);
			}
		}

	}

	public int getColor(BlockState state, BlockView world, BlockPos pos) {
		return -16777216;
	}
    
}
