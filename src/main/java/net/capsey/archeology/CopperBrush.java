package net.capsey.archeology;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class CopperBrush extends Item {

    // TODO: Change to interface
    private static final Block[] acceptingBlocks = { Blocks.DIRT, Blocks.GRAVEL };

    private Map<LivingEntity, PlacedBlock> brushingBlocks = new HashMap<LivingEntity, PlacedBlock>();

    public CopperBrush(Settings settings) {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {        
        World world = context.getWorld();
        Block block = world.getBlockState(context.getBlockPos()).getBlock();
        PlacedBlock placedBlock = new PlacedBlock(block, context.getBlockPos());

        if (isValidBlock(placedBlock.getBlock())) {
            brushingBlocks.put(context.getPlayer(), placedBlock);

            context.getPlayer().setCurrentHand(context.getHand());
            return ActionResult.CONSUME;
        }

		return ActionResult.FAIL;
	}

    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        PlacedBlock block = PlacedBlock.getBlockEntityLookingAt(user, world);

        if (block == null || !block.sameAs(brushingBlocks.get(user))) {
            user.stopUsingItem();
        }
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // Durability decreases if succeded
        stack.damage(1, user, (p) -> { p.sendToolBreakStatus(user.getActiveHand()); });
        
        // TODO: Change to valid behaviour!
        printInChat("Finished!", user.getUuid(), world.isClient);
        world.breakBlock(brushingBlocks.get(user).getPosition(), true);

		return stack;
	}

	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        printInChat("Stopped!", user.getUuid(), world.isClient);
        world.breakBlock(brushingBlocks.get(user).getPosition(), false);
	}

	public int getMaxUseTime(ItemStack stack) {
		return 2 * 20;
	}

	public UseAction getUseAction(ItemStack stack) {
        // TODO: Add custom UseAction
		return UseAction.BOW;
	}

    private boolean isValidBlock(Block block) {
        // 'acceptingBlocks' contains 'block'?
        for (Block b : acceptingBlocks) {
            if (block == b) {
                return true;
            }
        }

        return false;
    }

    private void printInChat(String message, UUID sender, boolean isClient) {
        if (!isClient) return;

        MinecraftClient client = MinecraftClient.getInstance();
        client.inGameHud.addChatMessage(MessageType.SYSTEM, Text.of(message), sender);
    }

}
