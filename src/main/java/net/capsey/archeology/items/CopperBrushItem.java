package net.capsey.archeology.items;

import java.util.Optional;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlock;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CopperBrushItem extends Item {

    public CopperBrushItem(Settings settings) {
        super(settings);
    }

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();

		if (!world.isClient) {
			PlayerEntity player = context.getPlayer();
			ItemStack stack = player.getStackInHand(context.getHand());
			
			if (stack.isOf(ArcheologyMod.COPPER_BRUSH)) {
				BlockEntity be = world.getBlockEntity(context.getBlockPos());
	
				if (be instanceof ExcavationBlockEntity) {
					ExcavationBlockEntity entity = (ExcavationBlockEntity) be;
					if (entity.startBrushing(player, stack)) {
						player.setCurrentHand(context.getHand());
						return ActionResult.CONSUME;
					}
				}
			}
		}

		return ActionResult.PASS;
	}

	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		Optional<BlockHitResult> raycast = ExcavationBlockEntity.getRaycast(user);

		if (raycast.isPresent()) {
			BlockPos pos = raycast.get().getBlockPos();

			if (user.getItemUseTime() % (ExcavationBlock.getBrushTicks(user.getActiveItem()) / 6) == 0) {
				world.addBlockBreakParticles(pos, world.getBlockState(pos));
			}
		}
	}

	public int getMaxUseTime(ItemStack stack) {
		return 6000;
	}

	public UseAction getUseAction(ItemStack stack) {
		return CustomUseAction.BRUSH;
	}

    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.getItem() == Items.COPPER_INGOT;
	}

}
