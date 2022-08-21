package net.capsey.archeology.items;

import net.capsey.archeology.ModConfig;
import net.capsey.archeology.Sounds;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlock;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.capsey.archeology.entity.BrushingPlayerEntity;
import net.capsey.archeology.mixin.client.MinecraftClientAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CopperBrushItem extends Item {

    private static final int[] BRUSH_TICKS = {8, 7, 6, 5};

    public CopperBrushItem(Settings settings) {
        super(settings);
    }

    public static int getBrushTicks(ItemStack stack) {
        return BRUSH_TICKS[getOxidizationIndex(stack)];
    }

    public static int getOxidizationIndex(ItemStack item) {
        return item.isDamaged() ? (4 * item.getDamage() / item.getMaxDamage()) % 4 : 0;
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
        int brushTicks = CopperBrushItem.getBrushTicks(stack);

        if (!world.isClient) {

            if (entity == null) {
                user.stopUsingItem();
                return;
            }

            if (remainingUseTicks % brushTicks == 0) {
                BlockState state = entity.getCachedState();
                int i = state.get(ExcavationBlock.BRUSHING_LEVEL) + 1;

                world.playSound(null, user.getBlockPos(), Sounds.BRUSHING_SOUND_EVENT, SoundCategory.PLAYERS, 0.5F, 1.0F);

                if (world.random.nextFloat() > 0.25F) {
                    return;
                }

                if (i > ExcavationBlock.MAX_BRUSHING_LEVELS) {
                    entity.dropLoot((PlayerEntity) user);
                    user.stopUsingItem();
                } else {
                    BlockSoundGroup soundGroup = entity.getCachedState().getSoundGroup();
                    world.setBlockState(entity.getPos(), state.with(ExcavationBlock.BRUSHING_LEVEL, i));
                    world.playSound(null, entity.getPos(), soundGroup.getBreakSound(), SoundCategory.BLOCKS, (soundGroup.getVolume() + 1.0F) / 4.0F, soundGroup.getPitch() * 0.8F);
                }
            }
        } else {
            MinecraftClient client = MinecraftClient.getInstance();

            if (entity != null && !entity.isRemoved()) {
                if (client.crosshairTarget instanceof BlockHitResult target && entity == world.getBlockEntity(target.getBlockPos())) {
                    if (((BrushingPlayerEntity.Client) player).tick()) {
                        if (remainingUseTicks % brushTicks == 0) {
                            addBrushingParticles(client.particleManager, target.getBlockPos());
                        }

                        return;
                    }
                }
            }

            client.interactionManager.stopUsingItem((PlayerEntity) user);
            if (ModConfig.releaseUseKeyAfterBrushing) {
                client.options.useKey.setPressed(false);
            } else {
                ((MinecraftClientAccessor) client).setItemUseCooldown(16);
            }
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
        return ingredient.isOf(Items.COPPER_INGOT);
    }

    // Particles
    public static void addBrushingParticles(ParticleManager particleManager, BlockPos pos) {
        for (int i = 0; i < 10; i++) {
            particleManager.addBlockBreakingParticles(pos, Direction.UP);
        }
    }

}
