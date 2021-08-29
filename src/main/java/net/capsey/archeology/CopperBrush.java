package net.capsey.archeology;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class CopperBrush extends Item {

    // Blocks that you can brush
    // TODO: Change to interface
    private final Block[] acceptingBlocks = { Blocks.DIRT, Blocks.GRAVEL };

    public CopperBrush(Settings settings) {
        super(settings);
    }

	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // Getting item in hand
		ItemStack itemStack = user.getStackInHand(hand);

        if (isValidBlock(getBlockLookingAt(user, world))) {
            // If looking at brushable block, start brushing
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }

        // Otherwise don't
        return TypedActionResult.fail(itemStack);
	}

    private BlockHitResult raycastToBlock(Entity entity) {
        // TODO: Remove hardcoded player reach value
        HitResult result = entity.raycast(4.5, 1, false);

        if (result.getClass() == BlockHitResult.class) {
            return (BlockHitResult) result;
        }

        return null;
    }

    private Block getBlockLookingAt(Entity entity, World world) {
        BlockHitResult blockHitResult = raycastToBlock(entity);

        if (blockHitResult != null)
        {
            Block block = world.getBlockState(blockHitResult.getBlockPos()).getBlock();
            return block;
        }

        return null;
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

    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        Block block = getBlockLookingAt(user, world);
        
        if (!isValidBlock(block)) {
            // If player stopped looking at valid block, then stop brushing
            user.stopUsingItem();
            return;
        }
        
        // Every 20 ticks
        if (remainingUseTicks % 20 == 0) {
            // Make brushing block sound
            SoundEvent blockSoundEvent = block.getSoundGroup(block.getDefaultState()).getBreakSound();
            world.playSound(null, raycastToBlock(user).getBlockPos(), blockSoundEvent, SoundCategory.BLOCKS, 0.25F, 1.0F);
        }

        // Every 10 ticks
        if (remainingUseTicks % 10 == 0) {            
            // Make brush sound
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_PARROT_FLY, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // Durability decreases if succeded
        stack.damage(1, user, (p) -> { p.sendToolBreakStatus(user.getActiveHand()); });
        
        // TODO: Change to valid behaviour!
        world.breakBlock(raycastToBlock(user).getBlockPos(), true);

		return stack;
	}

	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // if (user instanceof PlayerEntity) {
        //     PlayerEntity playerEntity = (PlayerEntity) user;
        //     playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        // }
	}

	public int getMaxUseTime(ItemStack stack) {
		return 2 * 20;
	}

	public UseAction getUseAction(ItemStack stack) {
        // TODO: Add custom UseAction
		return UseAction.BOW;
	}

}
