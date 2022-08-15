package net.capsey.archeology.blocks.chiseled;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public class ChiseledPillarBlock extends ChiseledBlock {

    public ChiseledPillarBlock(PillarBlock block) {
        super(block);
    }

    @Override
    protected BlockState processDefaultState(BlockState state) {
        return super.processDefaultState(state).with(PillarBlock.AXIS, Direction.Axis.Y);
    }

    @Override
    protected BlockState getConvertedState(BlockState state) {
        return super.getConvertedState(state).with(PillarBlock.AXIS, state.get(PillarBlock.AXIS));
    }

    @Override
    protected BlockState getMimickingState(BlockState state) {
        return super.getMimickingState(state).with(PillarBlock.AXIS, state.get(PillarBlock.AXIS));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return PillarBlock.changeRotation(state, rotation);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(PillarBlock.AXIS);
    }

}
