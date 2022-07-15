package net.capsey.archeology.blocks.clay_pot;

import net.capsey.archeology.BlockEntities;
import net.capsey.archeology.Blocks;
import net.capsey.archeology.blocks.clay_pot.ShardsContainer.Side;
import net.capsey.archeology.items.CeramicShardItem;
import net.capsey.archeology.items.ceramic_shard.CeramicShard;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Map;
import java.util.Optional;

public class RawClayPotBlock extends AbstractClayPotBlock implements BlockEntityProvider {

    public static final IntProperty HARDENING_PROGRESS = IntProperty.of("hardening_progress", 0, 5);

    public RawClayPotBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(HARDENING_PROGRESS, 0));
    }

    public boolean canHarden(BlockState state, BlockState floor) {
        boolean dry = !state.get(AbstractClayPotBlock.WATERLOGGED);
        boolean fire = floor.isIn(BlockTags.FIRE) || (floor.isIn(BlockTags.CAMPFIRES) && floor.get(CampfireBlock.LIT));

        return dry && fire;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(HARDENING_PROGRESS) != 0;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!world.isClient && player.isCreative() && blockEntity instanceof RawClayPotBlockEntity clayPot) {
            clayPot.clearShards();
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (canHarden(state, world.getBlockState(pos.down())) && !world.isClient && !world.getBlockTickScheduler().isQueued(pos, this)) {
            world.createAndScheduleBlockTick(pos, this, 10);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            Optional<RawClayPotBlockEntity> blockEntity = world.getBlockEntity(pos, BlockEntities.RAW_CLAY_POT_BLOCK_ENTITY);

            if (blockEntity.isPresent()) {
                blockEntity.get().getShards().values().forEach(item ->
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), item.getStack())
                );

                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (Side.validHit(hit)) {
            Optional<RawClayPotBlockEntity> entity = world.getBlockEntity(pos, BlockEntities.RAW_CLAY_POT_BLOCK_ENTITY);

            if (entity.isPresent()) {
                ItemStack item = player.getStackInHand(hand);
                Side side = Side.fromHit(hit);

                if (item.isEmpty() && hand == Hand.MAIN_HAND) {
                    CeramicShard shard = entity.get().getShard(side);

                    if (shard != null) {
                        if (!world.isClient) {
                            player.setStackInHand(hand, shard.getStack());
                            entity.get().removeShard(side);
                        }

                        return ActionResult.success(!world.isClient);
                    }
                } else if (!entity.get().hasShard(side) && item.getItem() instanceof CeramicShardItem shard) {
                    if (!world.isClient) {
                        entity.get().setShard(side, shard.getShard());
                        if (!player.isCreative()) {
                            item.decrement(1);
                        }
                    }

                    return ActionResult.success(!world.isClient);
                }
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(HARDENING_PROGRESS);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!canHarden(state, world.getBlockState(pos.down()))) {
            int i = state.get(HARDENING_PROGRESS);

            if (i > 0) {
                world.setBlockState(pos, state.with(HARDENING_PROGRESS, i - 1), Block.NOTIFY_LISTENERS);
                world.updateComparators(pos, this);
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (canHarden(state, world.getBlockState(pos.down()))) {
            if (random.nextInt(2) == 0) {
                int i = state.get(HARDENING_PROGRESS) + 1;

                if (i <= 5) {
                    world.setBlockState(pos, state.with(HARDENING_PROGRESS, i), Block.NOTIFY_LISTENERS);
                    world.updateComparators(pos, this);
                } else {
                    harden(world, pos);
                }
            }

            world.createAndScheduleBlockTick(pos, this, 10);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && canHarden(state, neighborState) && !world.isClient() && !world.getBlockTickScheduler().isQueued(pos, this)) {
            world.createAndScheduleBlockTick(pos, this, 10);
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        super.appendProperties(stateManager);
        stateManager.add(HARDENING_PROGRESS);
    }

    public void harden(World world, BlockPos pos) {
        Optional<RawClayPotBlockEntity> entity = world.getBlockEntity(pos, BlockEntities.RAW_CLAY_POT_BLOCK_ENTITY);

        if (entity.isPresent()) {
            Map<Side, CeramicShard> shards = entity.get().getShards();
            entity.get().clearShards();

            BlockState newState = Blocks.CLAY_POT.getDefaultState();
            world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS);
            world.addBlockEntity(new ClayPotBlockEntity(pos, newState, shards));
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RawClayPotBlockEntity(pos, state);
    }

}
