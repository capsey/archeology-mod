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

        if (block == null || !block.sameAs(brushingBlocks.get(user))) {
            unpressUseButton(world.isClient);
        }
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // Durability decreases if succeded
        stack.damage(1, user, (p) -> { p.sendToolBreakStatus(user.getActiveHand()); });
        
        // TODO: Change to valid behaviour!
        // printInChat("Finished!", user.getUuid(), world.isClient);
        world.breakBlock(brushingBlocks.get(user).getPosition(), true);
        unpressUseButton(world.isClient);

		return stack;
	}

	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // printInChat("Stopped!", user.getUuid(), world.isClient);
        // world.breakBlock(brushingBlocks.get(user).getPosition(), false);
	}

	public int getMaxUseTime(ItemStack stack) {
		return 2 * 20;
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

    // private void printInChat(String message, UUID sender, boolean isClient) {
    //     if (!isClient) return;

    //     MinecraftClient client = MinecraftClient.getInstance();
    //     client.inGameHud.addChatMessage(MessageType.SYSTEM, Text.of(message), sender);
    // }

}
