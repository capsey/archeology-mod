package net.capsey.archeology.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

@Mixin(SaplingBlock.class)
public class SaplingBlockMixin {

    @Inject(method = "randomTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V", at = @At("HEAD"), cancellable = true)
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (world.getBlockState(pos.down()).isIn(ArcheologyMod.CLAY_POTS_TAG)) {
			ci.cancel();
		}
	}

}
