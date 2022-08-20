package net.capsey.archeology.blocks.excavation_block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Map;

public class ExcavationBlock extends BlockWithEntity {

    public static final int MAX_BRUSHING_LEVELS = 5;
    public static final IntProperty BRUSHING_LEVEL = IntProperty.of("brushing_level", 0, MAX_BRUSHING_LEVELS);

    private final Map<BlockState, VoxelShape> shapesByState;

    public ExcavationBlock(AbstractBlock.Settings settings) {
        super(settings);
        shapesByState = getShapesForStates(state -> {
            float num = 8.0F - state.get(BRUSHING_LEVEL);
            return VoxelShapes.cuboid(0.0F, 0.0F, 0.0F, 1.0F, (num / 8), 1.0F);
        });
        setDefaultState(getStateManager().getDefaultState().with(BRUSHING_LEVEL, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(BRUSHING_LEVEL);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ExcavationBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return shapesByState.get(state);
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

}
