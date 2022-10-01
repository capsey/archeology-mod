package net.capsey.archeology.items;

import net.capsey.archeology.ModConfig;
import net.capsey.archeology.main.Items;
import net.capsey.archeology.main.Sounds;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlock;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.capsey.archeology.entity.BrushingPlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CopperBrushItem extends Item {

    private final int brushTicks;
    private final float luckPoints;

    public CopperBrushItem(Oxidizable.OxidationLevel oxidation, Settings settings) {
        super(settings);
        this.brushTicks = switch (oxidation) {
            case UNAFFECTED -> 8;
            case EXPOSED -> 7;
            case WEATHERED -> 6;
            case OXIDIZED -> 5;
        };
        this.luckPoints = switch (oxidation) {
            case UNAFFECTED -> 0.0F;
            case EXPOSED -> 1.0F;
            case WEATHERED -> 2.0F;
            case OXIDIZED -> 3.0F;
        };
    }

    public int getBrushTicks() {
        return brushTicks;
    }

    public float getLuckPoints() {
        return luckPoints;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getSide() != Direction.DOWN) {
            World world = context.getWorld();
            PlayerEntity player = context.getPlayer();

            if (player != null && player.getAbilities().allowModifyWorld) {
                BlockPos pos = context.getBlockPos();
                BlockState state = world.getBlockState(pos);

                if (state.getBlock() instanceof ExcavationBlock) {
                    player.setCurrentHand(context.getHand());
                    ((BrushingPlayerEntity) player).startBrushing(pos);
                    return ActionResult.CONSUME;
                }
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        BrushingPlayerEntity player = (BrushingPlayerEntity) user;
        ExcavationBlockEntity entity = player.getBrushingEntity();

        if (!world.isClient) {
            if (entity == null) {
                user.stopUsingItem();
                return;
            }

            if (remainingUseTicks % getBrushTicks() == 0) {
                BlockState state = entity.getCachedState();
                int i = state.get(ExcavationBlock.BRUSHING_LEVEL) + 1;

                world.playSound(null, user.getBlockPos(), Sounds.BRUSHING_SOUND_EVENT, SoundCategory.PLAYERS, 0.5F, 1.0F);

                // Chance to brush off layer
                if (world.random.nextFloat() > ModConfig.brushingLayerChance) {
                    return;
                }

                if (i > ExcavationBlock.MAX_BRUSHING_LEVELS) {
                    // Successfully finish brushing
                    entity.dropLoot((ServerPlayerEntity) user);
                    user.stopUsingItem();
                    oxidizeStack(user, stack);
                } else {
                    // Remove layer
                    world.setBlockState(entity.getPos(), state.with(ExcavationBlock.BRUSHING_LEVEL, i));
                    BlockSoundGroup soundGroup = entity.getCachedState().getSoundGroup();
                    world.playSound(null, entity.getPos(), soundGroup.getBreakSound(), SoundCategory.BLOCKS, (soundGroup.getVolume() + 1.0F) / 4.0F, soundGroup.getPitch() * 0.8F);
                }
            }
        } else {
            // Check if still looking at correct block
            MinecraftClient client = MinecraftClient.getInstance();

            if (entity != null && !entity.isRemoved() && client.crosshairTarget instanceof BlockHitResult target) {
                BlockPos pos = target.getBlockPos();

                if (entity == world.getBlockEntity(pos) && ((BrushingPlayerEntity.Client) player).tick()) {
                    client.particleManager.addBlockBreakingParticles(pos, Direction.UP);
                    return;
                }
            }

            client.interactionManager.stopUsingItem((PlayerEntity) user);
        }
    }

    private static void oxidizeStack(LivingEntity user, ItemStack stack) {
        int halfMaxDamage = stack.getMaxDamage() / 2;
        boolean bl = stack.getDamage() < halfMaxDamage;

        stack.damage(1, user, p -> {
            p.stopUsingItem();
            p.sendEquipmentBreakStatus(user.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        });

        if (bl && stack.getDamage() >= halfMaxDamage) {
            ItemStack newStack;

            if (stack.isOf(Items.COPPER_BRUSH)) {
                newStack = new ItemStack(Items.EXPOSED_COPPER_BRUSH);
            } else if (stack.isOf(Items.EXPOSED_COPPER_BRUSH)) {
                newStack = new ItemStack(Items.WEATHERED_COPPER_BRUSH);
            } else if (stack.isOf(Items.WEATHERED_COPPER_BRUSH)) {
                newStack = new ItemStack(Items.OXIDIZED_COPPER_BRUSH);
            } else {
                return;
            }

            newStack.setNbt(stack.getNbt());
            user.setStackInHand(user.getActiveHand(), newStack);
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient) {
            ((BrushingPlayerEntity.Server) user).onStopBrushing();
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 6000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(net.minecraft.item.Items.COPPER_INGOT);
    }

}
