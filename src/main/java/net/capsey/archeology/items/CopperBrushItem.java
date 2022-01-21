package net.capsey.archeology.items;

import me.shedaniel.autoconfig.AutoConfig;
import net.capsey.archeology.ModConfig;
import net.capsey.archeology.Sounds;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CopperBrushItem extends Item {

	private static final int[] BRUSH_TICKS = { 8, 7, 6, 5 };

	public static int getBrushTicks(ItemStack stack) {
		return BRUSH_TICKS[getOxidizationIndex(stack)];
	}

	public static int getOxidizationIndex(ItemStack item) {
		return item.isDamaged() ? (4 * item.getDamage() / item.getMaxDamage()) % 4 : 0;
	}

    public CopperBrushItem(Settings settings) {
        super(settings);
    }

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		PlayerEntity player = context.getPlayer();

		if (!world.isClient && player.getAbilities().allowModifyWorld) {
			BlockPos pos = context.getBlockPos();
			Block block = world.getBlockState(pos).getBlock();

			if (block instanceof ExcavationBlock excBlock) {
				ItemStack stack = context.getStack();

				if (excBlock.startBrushing(world, pos, player, stack)) {
					player.setCurrentHand(context.getHand());
					return ActionResult.CONSUME;
				}
			}
		}

		return ActionResult.PASS;
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (!world.isClient) {
			int brushTicks = CopperBrushItem.getBrushTicks(stack);

			if (remainingUseTicks % (brushTicks * ExcavationBlock.getBrushTicksPerLayer(world.getDifficulty())) == 0) {
				int damage = world.getRandom().nextInt(2);
				stack.damage(damage, user, p -> 
					p.sendEquipmentBreakStatus(user.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND)
				);
			}

			if (remainingUseTicks % brushTicks == 0) {
				world.playSound(null, user.getBlockPos(), Sounds.BRUSHING_SOUND_EVENT, SoundCategory.PLAYERS, 1f, 1f);
			}
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 6000;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		// Temporary solution?
		ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
		return config.disableBrushingAnimation ? UseAction.BOW : CustomUseAction.BRUSH;
	}

	@Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.getItem() == Items.COPPER_INGOT;
	}

}
