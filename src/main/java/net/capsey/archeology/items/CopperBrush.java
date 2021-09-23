package net.capsey.archeology.items;

import java.util.HashMap;

import net.capsey.archeology.CustomUseAction;
import net.capsey.archeology.blocks.ExcavationBlock;
import net.capsey.archeology.blocks.ExcavationBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class CopperBrush extends Item {

    @Environment(EnvType.SERVER)
    private HashMap<LivingEntity, ExcavationBlockEntity> brushingBlocks = new HashMap<LivingEntity, ExcavationBlockEntity>();

    @Environment(EnvType.CLIENT)
    private ExcavationBlockEntity brushingBlock;

    public CopperBrush(Settings settings) {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        // TODO: Add multiplayer check
        World world = context.getWorld();
        BlockEntity blockEntity = world.getBlockEntity(context.getBlockPos());

        if (blockEntity instanceof ExcavationBlockEntity) {
            ExcavationBlockEntity excavationEntity = (ExcavationBlockEntity) blockEntity;

            if (excavationEntity.startBrushing(context.getPlayer(), context.getStack())) {
                if (world.isClient) {
                    brushingBlock = excavationEntity;
                } else {
                    brushingBlocks.put(context.getPlayer(), excavationEntity);
                }

                context.getPlayer().setCurrentHand(context.getHand());
                return ActionResult.CONSUME;
            }
        }

		return ActionResult.FAIL;
	}

    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient) {
            if (brushingBlock != null) {
                if (brushingBlock.brushingTick(user, stack, getProgress(stack, remainingUseTicks), remainingUseTicks)) {
                    return;
                }
            }
        } else {
            if (brushingBlocks.containsKey(user)) {
                if (brushingBlocks.get(user).brushingTick(user, stack, getProgress(stack, remainingUseTicks), remainingUseTicks)) {
                    return;
                }
            }
        }

        unpressUseButton(user, world.isClient);
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        stack.damage(1, user, (p) -> { p.sendToolBreakStatus(user.getActiveHand()); });
        unpressUseButton(user, world.isClient);
        if (world.isClient) {
            brushingBlock.finishedBrushing();
        } else {
            brushingBlocks.get(user).finishedBrushing();
        }

		return stack;
	}

	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        stack.damage(3, user, (p) -> { p.sendToolBreakStatus(user.getActiveHand()); });
        if (world.isClient) {
            brushingBlock.breakBlock();
        } else {
            brushingBlocks.get(user).breakBlock();
        }
	}

	public int getMaxUseTime(ItemStack stack) {
		return ExcavationBlock.getBrushTicks(stack) * ExcavationBlock.MAX_BRUSHING_LEVELS;
	}

	public UseAction getUseAction(ItemStack stack) {
		return CustomUseAction.BRUSH;
	}

    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.getItem() == Items.COPPER_INGOT;
	}

    private void unpressUseButton(LivingEntity player, boolean isClient) {
        if (isClient) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.options.keyUse.setPressed(false);
        }

        player.stopUsingItem();
    }

    private float getProgress(ItemStack stack, int remainingUseTicks) {
        return (float) (getMaxUseTime(stack) - remainingUseTicks) / getMaxUseTime(stack);
    }

}
