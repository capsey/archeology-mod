package net.capsey.archeology.items;

import java.util.HashMap;
import java.util.Map;

import net.capsey.archeology.PlacedBlock;
import net.capsey.archeology.blocks.ExcavationBlock;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class CopperBrush extends Item {

    private Map<LivingEntity, PlacedBlock> brushingBlocks = new HashMap<LivingEntity, PlacedBlock>();

    public CopperBrush(Settings settings) {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        // TODO: Add check if block already started brushing, because two players can't both brush same block
        Block block = context.getWorld().getBlockState(context.getBlockPos()).getBlock();
        PlacedBlock placedBlock = new PlacedBlock(block, context.getBlockPos());

        if (block instanceof ExcavationBlock) {
            brushingBlocks.put(context.getPlayer(), placedBlock);

            context.getPlayer().setCurrentHand(context.getHand());
            return ActionResult.CONSUME;
        }

		return ActionResult.FAIL;
	}

    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        PlacedBlock block = PlacedBlock.getBlockEntityLookingAt(user, world);

        boolean lookedAway = block == null || !block.sameAs(brushingBlocks.get(user));
        boolean correctBlock = block.getBlock() instanceof ExcavationBlock;

        if (lookedAway || !correctBlock) {
            unpressUseButton(world.isClient);
            return;
        }

        ExcavationBlock obj = (ExcavationBlock) block.getBlock();
        obj.brushingTick(world, block.getPosition(), getProgress(stack, remainingUseTicks), remainingUseTicks);
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        stack.damage(1, user, (p) -> { p.sendToolBreakStatus(user.getActiveHand()); });
        
        // printInChat("Finished!", user.getUuid(), world.isClient);
        unpressUseButton(world.isClient);

        ExcavationBlock block = (ExcavationBlock) brushingBlocks.get(user).getBlock();
        block.finishedBrushing(world, brushingBlocks.get(user).getPosition());
        
		return stack;
	}

	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // printInChat("Stopped!", user.getUuid(), world.isClient);
        stack.damage(2, user, (p) -> { p.sendToolBreakStatus(user.getActiveHand()); });

        ExcavationBlock block = (ExcavationBlock) brushingBlocks.get(user).getBlock();
        block.stoppedBrushing(world, brushingBlocks.get(user).getPosition());
	}

	public int getMaxUseTime(ItemStack stack) {
		return 2 * ExcavationBlock.CHECK_TICKS * ExcavationBlock.MAX_BRUSHING_LEVELS;
	}

	public UseAction getUseAction(ItemStack stack) {
        // TODO: Add custom UseAction
		return UseAction.BOW;
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

    // private void printInChat(String message, UUID sender, boolean isClient) {
    //     if (!isClient) return;

    //     MinecraftClient client = MinecraftClient.getInstance();
    //     client.inGameHud.addChatMessage(MessageType.SYSTEM, Text.of(message), sender);
    // }

}
