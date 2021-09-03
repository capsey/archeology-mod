package net.capsey.archeology.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ExcavationBlock extends Block {

    public static final IntProperty BRUSHING_LEVEL = IntProperty.of("brushing_level", 0, 7);

    public ExcavationBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(BRUSHING_LEVEL, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(BRUSHING_LEVEL);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        float num = 8 - state.get(BRUSHING_LEVEL);
        return VoxelShapes.cuboid(0.0F, 0.0F, 0.0F, 1.0F, (num / 8), 1.0F);
    }

    public void brushingTick(World world, BlockPos pos, float progress) {
        int num = (int) Math.ceil(progress * 8);

        if (num >= 8) {
            world.breakBlock(pos, true);
            return;
        }

        world.setBlockState(pos, world.getBlockState(pos).with(BRUSHING_LEVEL, num));
    }

    public void stoppedBrushing(World world, BlockPos pos) {
        world.breakBlock(pos, false);
    }
    
}
