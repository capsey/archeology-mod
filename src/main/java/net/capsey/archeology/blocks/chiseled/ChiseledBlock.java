package net.capsey.archeology.blocks.chiseled;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.Arrays;
import java.util.Map;

public class ChiseledBlock extends Block {

    public enum Segment {
        UPPER_NORTH_WEST(makeSegmentShape(0.0F, 0.5F, 0.0F)),
        UPPER_NORTH_EAST(makeSegmentShape(0.5F, 0.5F, 0.0F)),
        UPPER_SOUTH_WEST(makeSegmentShape(0.0F, 0.5F, 0.5F)),
        UPPER_SOUTH_EAST(makeSegmentShape(0.5F, 0.5F, 0.5F)),
        LOWER_NORTH_WEST(makeSegmentShape(0.0F, 0.0F, 0.0F)),
        LOWER_NORTH_EAST(makeSegmentShape(0.5F, 0.0F, 0.0F)),
        LOWER_SOUTH_WEST(makeSegmentShape(0.0F, 0.0F, 0.5F)),
        LOWER_SOUTH_EAST(makeSegmentShape(0.5F, 0.0F, 0.5F));

        public final VoxelShape shape;

        Segment(VoxelShape shape) {
            this.shape = shape;
        }

        static VoxelShape makeSegmentShape(float x, float y, float z) {
            return VoxelShapes
                    .cuboid(0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F)
                    .offset(x, y, z);
        }
    }

    public static final BooleanProperty[] SEGMENTS = new BooleanProperty[Segment.values().length];

    static {
        Segment[] values = Segment.values();
        for (int i = 0; i < values.length; i++) {
            SEGMENTS[i] = BooleanProperty.of(values[i].name().toLowerCase());
        }
    }

    public final Block mimickingBlock;
    private final Map<BlockState, VoxelShape> shapesByState;

    public ChiseledBlock(Block block) {
        super(FabricBlockSettings.copy(block));
        mimickingBlock = block;
        shapesByState = getShapesForStates(ChiseledBlock::getShapeForState);

        // Setting default state
        BlockState state = getStateManager().getDefaultState();

        for (BooleanProperty property : SEGMENTS) {
            state = state.with(property, true);
        }

        setDefaultState(state);
    }

    private static VoxelShape getShapeForState(BlockState state) {
        VoxelShape shape = VoxelShapes.empty();
        Segment[] values = Segment.values();

        for (int i = 0; i < values.length; i++) {
            if (state.get(SEGMENTS[i])) {
                shape = VoxelShapes.union(shape, values[i].shape);
            }
        }

        return shape;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (Arrays.stream(SEGMENTS).allMatch(state::get)) {
            return mimickingBlock.getDefaultState();
        } else if (Arrays.stream(SEGMENTS).noneMatch(state::get)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(mimickingBlock);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SEGMENTS);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return shapesByState.get(state);
    }

}
