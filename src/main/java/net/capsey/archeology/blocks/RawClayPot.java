package net.capsey.archeology.blocks;

import java.util.Random;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class RawClayPot extends Block {

    public RawClayPot(Settings settings) {
        super(settings);
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockState block = world.getBlockState(pos.down());

		if (block.isOf(Blocks.CAMPFIRE) && block.get(CampfireBlock.LIT)) {
			world.setBlockState(pos, ArcheologyMod.CLAY_POT.getDefaultState());
		}
	}

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return ClayPot.BLOCK_SHAPE;
    }

}
