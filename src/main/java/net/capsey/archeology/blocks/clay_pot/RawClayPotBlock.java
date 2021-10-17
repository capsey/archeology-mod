package net.capsey.archeology.blocks.clay_pot;

import java.util.EnumMap;
import java.util.Optional;
import java.util.Random;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.clay_pot.ShardsContainer.Side;
import net.capsey.archeology.items.CeramicShard;
import net.capsey.archeology.items.CeramicShardRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.ShapeContext;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class RawClayPotBlock extends BlockWithEntity {

    public static final IntProperty HARDENING_PROGRESS = IntProperty.of("hardening_progress", 0, 5);

    public RawClayPotBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(HARDENING_PROGRESS, 0));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {        
        if (!world.isClient) {
            ItemStack item = player.getStackInHand(hand);
            Optional<CeramicShard> shard = CeramicShardRegistry.getShard(item);
    
            if (shard.isPresent()) {
                RawClayPotBlockEntity blockEntity = (RawClayPotBlockEntity) world.getBlockEntity(pos);
        
                if (Side.validHit(hit) && blockEntity.addShard(Side.fromHit(hit), shard.get())) {
                    if (!player.isCreative()) {
                        item.decrement(1);
                    }

                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

    public boolean canHarden(BlockState state) {
        return state.isIn(BlockTags.FIRE) || (state.isIn(BlockTags.CAMPFIRES) && state.get(CampfireBlock.LIT));
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {        
        if (canHarden(world.getBlockState(pos.down()))) {
            world.getBlockTickScheduler().schedule(pos, this, 10);
        } else {
            int i = state.get(HARDENING_PROGRESS) - 1;

            if (i >= 0) {
                world.setBlockState(pos, state.with(HARDENING_PROGRESS, i), Block.NOTIFY_LISTENERS);
            }
        }
	}

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {                
        if (direction == Direction.DOWN && canHarden(neighborState)) {
			if (!world.isClient() && !world.getBlockTickScheduler().isScheduled(pos, this)) {
                world.getBlockTickScheduler().schedule(pos, this, 10);
            }
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (canHarden(world.getBlockState(pos.down()))) {
            if (random.nextInt(2) == 0) {
                int i = state.get(HARDENING_PROGRESS) + 1;

                if (i <= 5) {
                    world.setBlockState(pos, state.with(HARDENING_PROGRESS, i), Block.NOTIFY_LISTENERS);
                } else {
                    harden(world, pos);
                }
            }

            world.getBlockTickScheduler().schedule(pos, this, 10);
        }
	}

    public void harden(World world, BlockPos pos) {
        BlockEntity entity = world.getBlockEntity(pos);

        if (entity instanceof RawClayPotBlockEntity) {
            EnumMap<Side, CeramicShard> shards = ((RawClayPotBlockEntity) entity).getShards();

            BlockState newState = ArcheologyMod.CLAY_POT.getDefaultState();
            world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS);
            world.addBlockEntity(((ClayPotBlock) ArcheologyMod.CLAY_POT).createBlockEntity(pos, newState));

            BlockEntity newEntity = world.getBlockEntity(pos);

            if (newEntity instanceof ClayPotBlockEntity) {
                ((ClayPotBlockEntity) newEntity).configureShards(shards);
            }
        }
    }

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!world.isClient && !state.isOf(ArcheologyMod.RAW_CLAY_POT) && !state.isOf(ArcheologyMod.CLAY_POT)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof ShardsContainer) {
				((ShardsContainer) blockEntity).getShards().values().forEach((item) -> {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), item.getStack());
                });

				world.updateComparators(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof RawClayPotBlockEntity) {
            RawClayPotBlockEntity rawClayPotBlockEntity = (RawClayPotBlockEntity) blockEntity;

            if (!world.isClient && player.isCreative()) {
                rawClayPotBlockEntity.clearShards();
            }
        }

		super.onBreak(world, pos, state, player);
	}

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return ClayPotBlock.BLOCK_SHAPE;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RawClayPotBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(HARDENING_PROGRESS);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

}
