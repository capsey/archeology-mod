package net.capsey.archeology.items;

import net.capsey.archeology.blocks.excavation_block.ExcavationBlock;
import net.capsey.archeology.entity.PlayerEntityMixinInterface;
import net.capsey.archeology.network.ExcavationBreakingC2SPacket;
import net.minecraft.block.Block;
import net.minecraft.block.Oxidizable.OxidizationLevel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CopperBrushItem extends Item {

	public static OxidizationLevel getOxidizationLevel(ItemStack item) {
		int index = 4 * item.getDamage() / item.getMaxDamage();

        switch (index) {
            case 0: return OxidizationLevel.UNAFFECTED;
            case 1: return OxidizationLevel.EXPOSED;
            case 2: return OxidizationLevel.WEATHERED;
            default: return OxidizationLevel.OXIDIZED;
        }
	}

	private static final int[] BRUSH_TICKS = { 8, 7, 6, 5 };

	private float breakingProgress = 0.0F;
	private int currentStage = 0;

	public static int getBrushTicks(OxidizationLevel level) {
		switch (level) {
            case UNAFFECTED: return BRUSH_TICKS[0];
            case EXPOSED: return BRUSH_TICKS[1];
            case WEATHERED: return BRUSH_TICKS[2];
            case OXIDIZED: return BRUSH_TICKS[3];
            default: throw new IllegalArgumentException();
        }
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
				float cooldown = ((PlayerEntityMixinInterface) player).getBrushCooldownProgress();

				if (cooldown >= 1.0F) {
					ItemStack stack = context.getStack();

					if (excBlock.startBrushing(world, pos, player, stack)) {
						player.setCurrentHand(context.getHand());
						return ActionResult.CONSUME;
					}
				}
			}
		}

		return ActionResult.PASS;
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		int brushTicks = CopperBrushItem.getBrushTicks(getOxidizationLevel(user.getActiveItem()));

		if (world.isClient) {
			MinecraftClient client = MinecraftClient.getInstance();
			HitResult raycast = user.raycast(client.interactionManager.getReachDistance(), 1, false);
			
			if (remainingUseTicks == 6000) {
				breakingProgress = 0.0F;
				currentStage = -1;
			}
			
			if (raycast instanceof BlockHitResult blockResult) {
				breakingProgress += 0.02F;
				int stage = (int) (breakingProgress * 10);

				if (currentStage != stage) {
					world.sendPacket(new ExcavationBreakingC2SPacket(blockResult.getBlockPos(), stage));
					currentStage = stage;
				}
			}
		} else if (remainingUseTicks % brushTicks * ExcavationBlock.getBrushTicksPerLayer(world.getDifficulty()) == 0) {
			int damage = world.getRandom().nextInt(2);
			stack.damage(damage, user, p -> 
				p.sendEquipmentBreakStatus(user.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND)
			);
		}
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		if (user instanceof PlayerEntityMixinInterface player) {
			player.resetLastBrushedTicks();
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 6000;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return CustomUseAction.BRUSH;
	}

	@Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.getItem() == Items.COPPER_INGOT;
	}

}
