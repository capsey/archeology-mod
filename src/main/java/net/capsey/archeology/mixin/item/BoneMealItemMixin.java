package net.capsey.archeology.mixin.item;

import net.capsey.archeology.main.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.BoneMealItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {

    @Redirect(method = "useOnFertilizable(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Fertilizable;isFertilizable(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)Z"))
    private static boolean isFertilizable(Fertilizable fertilizable, WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        BlockState floor = world.getBlockState(pos.down());
        boolean isOnClayPot = floor.isIn(Blocks.CLAY_POTS_TAG) && state.isIn(Blocks.CLAY_POT_PLANTABLE_TAG);

        return fertilizable.isFertilizable(world, pos, state, isClient) && !isOnClayPot;
    }

    @Redirect(method = "useOnFertilizable(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Fertilizable;canGrow(Lnet/minecraft/world/World;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
    private static boolean canGrow(Fertilizable fertilizable, World world, Random random, BlockPos pos, BlockState state) {
        BlockState floor = world.getBlockState(pos.down());
        boolean isOnClayPot = floor.isIn(Blocks.CLAY_POTS_TAG) && state.isIn(Blocks.CLAY_POT_PLANTABLE_TAG);

        return fertilizable.canGrow(world, random, pos, state) && !isOnClayPot;
    }

}
