package net.capsey.archeology.blocks.clay_pot;

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
        ItemStack item = player.getStackInHand(hand);
        if (item.isOf(ArcheologyMod.CERAMIC_SHARD)) {
            RawClayPotBlockEntity blockEntity = (RawClayPotBlockEntity) world.getBlockEntity(pos);
    
            if (Side.validHit(hit) && blockEntity.addShard(Side.fromHit(hit), item)) {
                player.setStackInHand(hand, ItemStack.EMPTY);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
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

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ArcheologyMod.RAW_CLAY_POT_BLOCK_ENTITY, (world1, pos, state1, be) -> RawClayPotBlockEntity.tick(world1, pos, state1, be));
    }

}
