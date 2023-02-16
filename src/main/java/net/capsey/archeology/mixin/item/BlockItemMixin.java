package net.capsey.archeology.mixin.item;

import net.capsey.archeology.blocks.clay_pot.ClayPotBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Final
    @Shadow
    private Block block;

    @Inject(at = @At("HEAD"), method = "onItemEntityDestroyed(Lnet/minecraft/entity/ItemEntity;)V")
    public void onItemEntityDestroyed(ItemEntity entity, CallbackInfo ci) {
        if (this.block instanceof ClayPotBlock) {
            NbtCompound nbt = BlockItem.getBlockEntityNbt(entity.getStack());

            if (nbt != null && nbt.contains("Items", NbtElement.LIST_TYPE)) {
                NbtList nbtList = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
                ItemUsage.spawnItemContents(entity, nbtList.stream().map(NbtCompound.class::cast).map(ItemStack::fromNbt));
            }
        }
    }

}
