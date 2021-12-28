package net.capsey.archeology.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.capsey.archeology.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

@Mixin(PlantBlock.class)
public class PlantBlockMixin {
    
    @Inject(method = "canPlaceAt(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z", at = @At("RETURN"), cancellable = true)
    private void canPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState floor = world.getBlockState(pos.down());
        boolean isOnClayPot = floor.isIn(Blocks.CLAY_POTS_TAG) && state.isIn(Blocks.CLAY_POT_PLANTABLE_TAG);

        cir.setReturnValue(cir.getReturnValue() || isOnClayPot);
	}

}
