package net.capsey.archeology.mixin.item;

import net.capsey.archeology.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PickaxeItem.class)
public abstract class PickaxeItemMixin extends MiningToolItem {

    protected PickaxeItemMixin(float attackDamage, float attackSpeed, ToolMaterial material, TagKey<Block> effectiveBlocks, Settings settings) {
        super(attackDamage, attackSpeed, material, effectiveBlocks, settings);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (state.isIn(Blocks.CLAY_POTS_TAG)) {
            return 5.0F;
        } else {
            return super.getMiningSpeedMultiplier(stack, state);
        }
    }

}
