package net.capsey.archeology.blocks.excavation_block;

import net.capsey.archeology.BlockEntities;
import net.capsey.archeology.Items;
import net.capsey.archeology.blocks.clay_pot.ClayPotBlockEntity;
import net.capsey.archeology.entity.ExcavatorPlayerEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Random;

public class ExcavationBlock extends BlockWithEntity {

    public static final int MAX_BRUSHING_LEVELS = 5;
    public static final IntProperty BRUSHING_LEVEL = IntProperty.of("brushing_level", 0, MAX_BRUSHING_LEVELS);

    private static final int[] BRUSH_TICKS_PER_LAYER = {4, 4, 6, 6};

    private final UniformIntProvider experienceDropped;

    public ExcavationBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.experienceDropped = UniformIntProvider.create(2, 5);
        setDefaultState(getStateManager().getDefaultState().with(BRUSHING_LEVEL, 0));
    }

    public static int getBrushTicksPerLayer(Difficulty difficulty) {
        switch (difficulty) {
            case PEACEFUL:
                return BRUSH_TICKS_PER_LAYER[0];
            case EASY:
                return BRUSH_TICKS_PER_LAYER[1];
            default:
                return BRUSH_TICKS_PER_LAYER[2];
            case HARD:
                return BRUSH_TICKS_PER_LAYER[3];
        }
    }

    public boolean startBrushing(World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
        if (stack.isOf(Items.COPPER_BRUSH)) {
            BlockState state = world.getBlockState(pos);

            if (state.isOf(this) && state.get(BRUSHING_LEVEL) == 0) {
                Optional<ExcavationBlockEntity> entity = world.getBlockEntity(pos, BlockEntities.EXCAVATION_BLOCK_ENTITY);

                if (entity.isPresent() && !world.getBlockTickScheduler().isQueued(pos, this)) {
                    entity.get().startBrushing(player, stack);

                    world.setBlockState(pos, state.with(BRUSHING_LEVEL, 1));
                    world.createAndScheduleBlockTick(pos, this, 2);

                    player.incrementStat(Stats.MINED.getOrCreateStat(this));
                    ((ExcavatorPlayerEntity) player).startBrushing(entity.get());
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = state.get(BRUSHING_LEVEL);

        if (i != 0) {
            Optional<ExcavationBlockEntity> entity = world.getBlockEntity(pos, BlockEntities.EXCAVATION_BLOCK_ENTITY);

            if (entity.isPresent() && entity.get().brushingCheck()) {

                if (i < MAX_BRUSHING_LEVELS) {
                    // Remove layer
                    if (entity.get().isTime(world.getDifficulty())) {
                        world.setBlockState(pos, state.with(ExcavationBlock.BRUSHING_LEVEL, i + 1));
                    }

                    world.createAndScheduleBlockTick(pos, this, 1);
                    return;
                } else {
                    // Drop experience
                    int exp = this.experienceDropped.get(world.random);
                    if (exp > 0) {
                        this.dropExperience(world, pos, exp);
                    }

                    // Drop loot
                    entity.get().successfullyBrushed();
                    entity.get().dropLoot();
                }
            }

            world.breakBlock(pos, true);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Chance 33.3%
        if (random.nextInt(3) != 0) return;

        Optional<ClayPotBlockEntity> under = world.getBlockEntity(pos.down(), BlockEntities.CLAY_POT_BLOCK_ENTITY);
        Optional<ExcavationBlockEntity> entity = world.getBlockEntity(pos, BlockEntities.EXCAVATION_BLOCK_ENTITY);

        if (under.isPresent() && entity.isPresent()) {
            // Generating item to insert
            ItemStack loot = entity.get().generateItem();

            // Trying to place into not fully filled stacks
            for (int i = 0; i < under.get().size(); i++) {
                ItemStack stack = under.get().getStack(i).copy();

                if (ItemStack.canCombine(loot, stack)) {
                    while (stack.getCount() < stack.getMaxCount() && !loot.isEmpty()) {
                        loot.decrement(1);
                        stack.increment(1);
                    }

                    under.get().setStack(i, stack);
                    if (loot.isEmpty()) break;
                }
            }

            // If didn't filled everything, try to fill empty slots
            if (!loot.isEmpty()) {
                for (int i = 0; i < under.get().size(); i++) {
                    if (under.get().getStack(i).isEmpty()) {
                        under.get().setStack(i, loot);
                        break;
                    }
                }
            }

            // Update comparators
            world.updateComparators(pos.down(), under.get().getCachedState().getBlock());
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (!world.isClient) {
                world.setBlockBreakingInfo(0, pos, -1);
                Optional<ExcavationBlockEntity> entity = world.getBlockEntity(pos, BlockEntities.EXCAVATION_BLOCK_ENTITY);

                if (entity.isPresent()) {
                    entity.get().onBlockBreak();
                }
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
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
        float num = 8.0F - state.get(BRUSHING_LEVEL);
        return VoxelShapes.cuboid(0.0F, 0.0F, 0.0F, 1.0F, (num / 8), 1.0F);
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }

}
