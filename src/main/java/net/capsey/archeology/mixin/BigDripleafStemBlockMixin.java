package net.capsey.archeology.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.block.BigDripleafStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

@Mixin(BigDripleafStemBlock.class)
public class BigDripleafStemBlockMixin {
    
    @Inject(method = "canPlaceAt(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private void canPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Block block = ((BigDripleafStemBlock) ((Object) this));
        
        BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		BlockState blockState2 = world.getBlockState(pos.up());
		
        boolean bl = blockState.isOf(block) || blockState.isIn(ArcheologyMod.CLAY_POTS_TAG) || blockState.isSideSolidFullSquare(world, blockPos, Direction.UP);
        boolean bl2 = blockState2.isOf(block) || blockState2.isOf(Blocks.BIG_DRIPLEAF);

        cir.setReturnValue(bl && bl2);
        cir.cancel();
	}

}
