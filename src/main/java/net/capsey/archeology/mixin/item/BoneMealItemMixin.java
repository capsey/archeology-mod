package net.capsey.archeology.mixin.item;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.BoneMealItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {
    
    @Redirect(method = "useOnFertilizable(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Fertilizable;isFertilizable(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)Z"))
    private static boolean isFertilizable(Fertilizable fertilizable, BlockView world, BlockPos pos, BlockState state, boolean isClient) {
        BlockState floor = world.getBlockState(pos.down());
        boolean isOnClayPot = floor.isIn(ArcheologyMod.CLAY_POTS_TAG) && state.isIn(ArcheologyMod.CLAY_POT_PLANTABLE_TAG);

        return fertilizable.isFertilizable(world, pos, state, isClient) && !isOnClayPot;
    }

    @Redirect(method = "useOnFertilizable(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Fertilizable;canGrow(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
    private static boolean canGrow(Fertilizable fertilizable, World world, Random random, BlockPos pos, BlockState state) {
        BlockState floor = world.getBlockState(pos.down());
        boolean isOnClayPot = floor.isIn(ArcheologyMod.CLAY_POTS_TAG) && state.isIn(ArcheologyMod.CLAY_POT_PLANTABLE_TAG);

        return fertilizable.canGrow(world, random, pos, state) && !isOnClayPot;
    }

}
