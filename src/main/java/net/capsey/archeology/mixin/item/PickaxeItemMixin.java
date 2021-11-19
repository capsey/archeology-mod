package net.capsey.archeology.mixin.item;

import org.spongepowered.asm.mixin.Mixin;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.tag.Tag;

@Mixin(PickaxeItem.class)
public abstract class PickaxeItemMixin extends MiningToolItem {
    
    protected PickaxeItemMixin(float attackDamage, float attackSpeed, ToolMaterial material, Tag<Block> effectiveBlocks, Settings settings) {
        super(attackDamage, attackSpeed, material, effectiveBlocks, settings);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		if (state.isIn(ArcheologyMod.CLAY_POTS_TAG)) {
			return 5.0F;
		} else {
			return super.getMiningSpeedMultiplier(stack, state);
		}
	}

}
