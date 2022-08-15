package net.capsey.archeology.blocks.chiseled;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

import java.util.*;

public class ChiseledBlock extends Block implements Waterloggable {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final Map<Segment, BooleanProperty> SEGMENTS = new EnumMap<>(Segment.class);
    public static final Map<Block, ChiseledBlock> CHISELABLE_BLOCKS = new HashMap<>();

    static {
        for (Segment segment : Segment.values()) {
            SEGMENTS.put(segment, BooleanProperty.of(segment.name().toLowerCase()));
        }
    }

    public final Block mimickingBlock;
    private final Map<BlockState, VoxelShape> shapesByState;

    public ChiseledBlock(Block block) {
        super(FabricBlockSettings.copy(block));
        shapesByState = getShapesForStates(ChiseledBlock::getShapeForState);
        mimickingBlock = block;

        CHISELABLE_BLOCKS.put(mimickingBlock, this);

        setDefaultState(processDefaultState(getStateManager().getDefaultState()));
    }

    protected BlockState processDefaultState(BlockState state) {
        for (BooleanProperty property : SEGMENTS.values()) {
            state = state.with(property, true);
        }

        return state.with(WATERLOGGED, false);
    }

    public static boolean isChiselable(Block block) {
        return CHISELABLE_BLOCKS.containsKey(block) || block instanceof ChiseledBlock;
    }

    public static void chiselSegment(ServerWorld world, BlockPos pos, Segment segment, Entity player) {
        BlockState state = world.getBlockState(pos);

        if (CHISELABLE_BLOCKS.containsKey(state.getBlock())) {
            emitEvents(world, state, pos, player);
            world.setBlockState(pos, CHISELABLE_BLOCKS
                    .get(state.getBlock())
                    .getConvertedState(state)
                    .with(SEGMENTS.get(segment), false)
            );
        } else if (state.getBlock() instanceof ChiseledBlock) {
            state = state.with(SEGMENTS.get(segment), false);

            if (SEGMENTS.values().stream().anyMatch(state::get)) {
                emitEvents(world, state, pos, player);
                world.setBlockState(pos, state);
                if (state.get(WATERLOGGED)) {
                    world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
                }
            } else {
                world.breakBlock(pos, true, player);
            }
        }
    }

    protected BlockState getConvertedState(BlockState state) {
        return getDefaultState();
    }

    protected static void emitEvents(ServerWorld world, BlockState state, BlockPos pos, Entity player) {
        world.playSound(null, pos, state.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state));
        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(player, state));
    }

    protected static VoxelShape getShapeForState(BlockState state) {
        VoxelShape shape = VoxelShapes.empty();

        for (Segment segment : Segment.values()) {
            if (state.get(SEGMENTS.get(segment))) {
                shape = VoxelShapes.union(shape, segment.shape);
            }
        }

        return shape;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // Scheduling water update
        if (state.get(WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        // Replace, if needed
        if (SEGMENTS.values().stream().allMatch(state::get)) {
            return getMimickingState(state);
        } else if (SEGMENTS.values().stream().noneMatch(state::get)) {
            return Blocks.AIR.getDefaultState();
        }

        return state;
    }

    protected BlockState getMimickingState(BlockState state) {
        return mimickingBlock.getDefaultState();
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        int segmentsLeft = SEGMENTS.values().stream().mapToInt(x -> state.get(x) ? 1 : 0).sum();

        if (segmentsLeft <= 4) {
            return Collections.emptyList();
        }

        return mimickingBlock.getDroppedStacks(state, builder);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return shapesByState.get(state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        SEGMENTS.values().forEach(builder::add);
        builder.add(WATERLOGGED);
    }

    @Override
    public Item asItem() {
        return mimickingBlock.asItem();
    }

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

        public static Optional<Segment> get(String name) {
            try {
                return Optional.of(Segment.valueOf(name));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }

        public static Segment fromRaycast(BlockHitResult raycast, BlockState state) {
            Vec3d blockPos = Vec3d.ofCenter(raycast.getBlockPos());
            Vec3d pos = blockPos.relativize(raycast.getPos());
            Segment segment;

            if (pos.x < 0) {
                if (pos.z < 0) {
                    segment = pos.y < 0 ? LOWER_NORTH_WEST : UPPER_NORTH_WEST;
                } else {
                    segment = pos.y < 0 ? LOWER_SOUTH_WEST : UPPER_SOUTH_WEST;
                }
            } else {
                if (pos.z < 0) {
                    segment = pos.y < 0 ? LOWER_NORTH_EAST : UPPER_NORTH_EAST;
                } else {
                    segment = pos.y < 0 ? LOWER_SOUTH_EAST : UPPER_SOUTH_EAST;
                }
            }

            if (state.getBlock() instanceof ChiseledBlock && !state.get(SEGMENTS.get(segment))) {
                return segment.offset(raycast.getSide().getOpposite());
            }

            return segment;
        }

        public Segment offset(Direction direction) {
            return switch (direction) {
                case DOWN -> Segment.valueOf(this.name().replace("UPPER", "LOWER"));
                case UP -> Segment.valueOf(this.name().replace("LOWER", "UPPER"));
                case NORTH -> Segment.valueOf(this.name().replace("SOUTH", "NORTH"));
                case SOUTH -> Segment.valueOf(this.name().replace("NORTH", "SOUTH"));
                case WEST -> Segment.valueOf(this.name().replace("EAST", "WEST"));
                case EAST -> Segment.valueOf(this.name().replace("WEST", "EAST"));
            };
        }
    }

}
