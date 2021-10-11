package net.capsey.archeology.blocks.clay_pot;

import org.jetbrains.annotations.Nullable;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.clay_pot.ShardsContainer.Side;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RawClayPotBlock extends BlockWithEntity {

    public RawClayPotBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {        
        if (!world.isClient) {
            ItemStack item = player.getStackInHand(hand);
    
            if (item.isOf(ArcheologyMod.CERAMIC_SHARD)) {
                RawClayPotBlockEntity blockEntity = (RawClayPotBlockEntity) world.getBlockEntity(pos);
        
                if (Side.validHit(hit) && blockEntity.addShard(Side.fromHit(hit), item)) {
                    if (!player.isCreative()) {
                        player.setStackInHand(hand, ItemStack.EMPTY);
                    }
                    return ActionResult.SUCCESS;
                }
            }
        }

        return ActionResult.PASS;
    }

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!world.isClient && !state.isOf(ArcheologyMod.RAW_CLAY_POT) && !state.isOf(ArcheologyMod.CLAY_POT)) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			
			if (blockEntity instanceof ShardsContainer) {
				((ShardsContainer) blockEntity).getShards().values().forEach((item) -> {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), item);
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
    public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

    @Override @Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? null : checkType(type, ArcheologyMod.RAW_CLAY_POT_BLOCK_ENTITY, RawClayPotBlockEntity::tick);
	}

}
