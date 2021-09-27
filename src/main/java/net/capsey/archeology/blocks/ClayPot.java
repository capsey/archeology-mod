package net.capsey.archeology.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class ClayPot extends Block {

    public static final VoxelShape BLOCK_SHAPE;
	private static final VoxelShape BASE_SHAPE;
	private static final VoxelShape NECK_SHAPE;
    private static final VoxelShape HEAD_SHAPE;

    public ClayPot(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return ClayPot.BLOCK_SHAPE;
    }

    static {
		BASE_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D);
		NECK_SHAPE = Block.createCuboidShape(3.0D, 10.0D, 3.0D, 13.0D, 14.0D, 13.0D);
        HEAD_SHAPE = Block.createCuboidShape(2.0D, 14.0D, 2.0D, 14.0D, 16.0D, 14.0D);
		BLOCK_SHAPE = VoxelShapes.union(BASE_SHAPE, NECK_SHAPE, HEAD_SHAPE);
	}

}
