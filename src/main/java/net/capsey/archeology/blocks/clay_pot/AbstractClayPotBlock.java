package net.capsey.archeology.blocks.clay_pot;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public abstract class AbstractClayPotBlock extends Block implements Waterloggable {
    
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final VoxelShape BLOCK_SHAPE;
	public static final VoxelShape BASE_SHAPE;
	public static final VoxelShape NECK_SHAPE;
    public static final VoxelShape HEAD_SHAPE;

    public AbstractClayPotBlock(Settings settings) {
        super(settings);
		setDefaultState(getStateManager().getDefaultState().with(WATERLOGGED, false));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if ((Boolean)state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return ClayPotBlock.BLOCK_SHAPE;
    }

    @Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

    @Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
		return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

    static {
		BASE_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D);
		NECK_SHAPE = Block.createCuboidShape(3.0D, 10.0D, 3.0D, 13.0D, 14.0D, 13.0D);
        HEAD_SHAPE = Block.createCuboidShape(2.0D, 14.0D, 2.0D, 14.0D, 16.0D, 14.0D);
		BLOCK_SHAPE = VoxelShapes.union(BASE_SHAPE, NECK_SHAPE, HEAD_SHAPE);
	}

}
