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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
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
                if (!world.isClient) {
                    brushingBlocks.put(context.getPlayer(), excavationEntity);
                }
                else {
                    brushingBlock = excavationEntity;
                }

                context.getPlayer().setCurrentHand(context.getHand());
                return ActionResult.CONSUME;
            }
        }

		return ActionResult.FAIL;
	}

    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        // TODO: Remove hardcoded player reach value
        HitResult result = user.raycast(4.5F, 1, false);

        if (result.getClass() == BlockHitResult.class) {
            BlockHitResult blockHitResult = (BlockHitResult) result;
            BlockEntity blockEntity = world.getBlockEntity(blockHitResult.getBlockPos());
            
            if (blockEntity instanceof ExcavationBlockEntity) {
                ExcavationBlockEntity excavationEntity = (ExcavationBlockEntity) blockEntity;

                if (excavationEntity.isBrushingPlayer(user)) {
                    excavationEntity.brushingTick(getProgress(stack, remainingUseTicks), remainingUseTicks, stack, blockHitResult.getPos());
                    return;
                }
            }
        }

        unpressUseButton(world.isClient);
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        stack.damage(1, user, (p) -> { p.sendToolBreakStatus(user.getActiveHand()); });
        unpressUseButton(world.isClient);

        if (!world.isClient) {
            brushingBlocks.get(user).finishedBrushing();
        }
        else {
            brushingBlock.finishedBrushing();
        }

		return stack;
	}

	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        stack.damage(3, user, (p) -> { p.sendToolBreakStatus(user.getActiveHand()); });

        if (!world.isClient) {
            brushingBlocks.get(user).breakBlock();
        }
        else {
            brushingBlock.breakBlock();
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

    private void unpressUseButton(boolean isClient) {
        if (isClient) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.options.keyUse.setPressed(false);
        }
    }

    private float getProgress(ItemStack stack, int remainingUseTicks) {
        return (float) (getMaxUseTime(stack) - remainingUseTicks) / getMaxUseTime(stack);
    }

}
