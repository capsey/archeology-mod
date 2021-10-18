package net.capsey.archeology.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@Mixin(SaplingBlock.class)
public abstract class SaplingBlockMixin extends PlantBlock {

    protected SaplingBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "randomTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)Z", at = @At("HEAD"), cancellable = true)
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
		if (world.getBlockState(pos.down()).isOf(ArcheologyMod.CLAY_POT)) {
			ci.cancel();
		}
	}

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return super.canPlantOnTop(floor, world, pos) || floor.isOf(ArcheologyMod.CLAY_POT);
    }

    @Inject(method = "isFertilizable(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)Z", at = @At("RETURN"), cancellable = true)
    public void isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() && !world.getBlockState(pos.down()).isOf(ArcheologyMod.CLAY_POT));
	}

    @Inject(method = "canGrow(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", at = @At("RETURN"), cancellable = true)
    public void canGrow(World world, Random random, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() && !world.getBlockState(pos.down()).isOf(ArcheologyMod.CLAY_POT));
	}

}
