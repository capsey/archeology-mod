package net.capsey.archeology.items;

import java.util.HashMap;

import me.shedaniel.autoconfig.AutoConfig;
import net.capsey.archeology.ModConfig;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlock;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class CopperBrushItem extends Item {

    @Environment(EnvType.SERVER)
    private HashMap<LivingEntity, ExcavationBlockEntity> brushingBlocks = new HashMap<LivingEntity, ExcavationBlockEntity>();

    @Environment(EnvType.CLIENT)
    private ExcavationBlockEntity brushingBlock;

    public CopperBrushItem(Settings settings) {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        // TODO: Add multiplayer check
        World world = context.getWorld();
        PlayerEntity user = context.getPlayer();

        if (!isCurrentlyBrushing(user, world.isClient)) {
            BlockEntity blockEntity = world.getBlockEntity(context.getBlockPos());
    
            if (blockEntity instanceof ExcavationBlockEntity) {
                ExcavationBlockEntity excavationEntity = (ExcavationBlockEntity) blockEntity;
                
                if (excavationEntity.startBrushing(user, context.getStack())) {
                    addBrushingBlock(user, excavationEntity, world.isClient);
    
                    user.setCurrentHand(context.getHand());
                    return ActionResult.CONSUME;
                }
            }
        }

		return ActionResult.FAIL;
	}

    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (isCurrentlyBrushing(user, world.isClient)) {
            if (getBrushingBlock(user, world.isClient).brushingTick(user, stack, getProgress(stack, remainingUseTicks), remainingUseTicks)) {
                return;
            }
        }

        unpressUseButton(user, world.isClient);
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        stack.damage(1, user, (p) -> { p.sendToolBreakStatus(user.getActiveHand()); });
        
        if (isCurrentlyBrushing(user, world.isClient)) {
            if (world.isClient) {
                brushingBlock.finishedBrushing();
                brushingBlock = null;
            } else {
                brushingBlocks.get(user).finishedBrushing();
                brushingBlocks.remove(user);
            }
        }
        
        unpressUseButton(user, world.isClient);
		return stack;
	}

	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        stack.damage(3, user, (p) -> { p.sendToolBreakStatus(user.getActiveHand()); });
        if (isCurrentlyBrushing(user, world.isClient)) {
            if (world.isClient) {
                brushingBlock.breakBlock();
                brushingBlock = null;
            } else {
                brushingBlocks.get(user).breakBlock();
                brushingBlocks.remove(user);
            }
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

    private void unpressUseButton(LivingEntity user, boolean isClient) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        
        if (isClient && config.stopUsingAfterBrushing) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.options.keyUse.setPressed(false);
        }
        
        user.stopUsingItem();
    }

    private float getProgress(ItemStack stack, int remainingUseTicks) {
        return (float) (getMaxUseTime(stack) - remainingUseTicks) / getMaxUseTime(stack);
    }

    private boolean isCurrentlyBrushing(LivingEntity user, boolean isClient) {
        if (isClient) {
            return brushingBlock != null;
        } else {
            return brushingBlocks.containsKey(user);
        }
    }

    private ExcavationBlockEntity getBrushingBlock(LivingEntity user, boolean isClient) {
        if (isClient) {
            return brushingBlock;
        } else {
            return brushingBlocks.get(user);
        }
    }

    private void addBrushingBlock(LivingEntity user, ExcavationBlockEntity block, boolean isClient) {
        if (isClient) {
            brushingBlock = block;
        } else {
            brushingBlocks.put(user, block);
        }
    }

}
