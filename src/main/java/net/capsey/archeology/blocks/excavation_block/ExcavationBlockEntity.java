package net.capsey.archeology.blocks.excavation_block;

import java.util.Optional;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ExcavationBlockEntity extends FossilContainer {

    private static float getBreakingDelta(ItemStack stack, double magnitude) {
        if (!stack.isOf(ArcheologyMod.COPPER_BRUSH)) {
            return 1.0F;
        }

        // Only for Debug purposes
        if (stack.getNbt().contains("Debug") && stack.getNbt().getBoolean("Debug")) {
            return 0;
        }

        int index = (int) Math.floor(4 * stack.getDamage() / stack.getMaxDamage());
        
        switch (index) {
            case 0: return (float) (-0.75F * magnitude) + 0.04F;
            case 1: return (float) (-0.72F * magnitude) + 0.04F;
            case 2: return (float) (-0.67F * magnitude) + 0.05F;
            case 3: return (float) (-0.65F * magnitude) + 0.06F;
            default: return 1.0F;
        }
    }

    private PlayerEntity brushingPlayer;
    private float breakingProgress = -1.0F;
    private Vec3d prevLookPoint;

    public ExcavationBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public boolean startBrushing(PlayerEntity player, ItemStack stack) {
        if (brushingPlayer == null && stack.isOf(ArcheologyMod.COPPER_BRUSH)) {
            BlockState state = getCachedState();

            if (state.getBlock() instanceof ExcavationBlock) {
                if (state.get(ExcavationBlock.BRUSHING_LEVEL) == 0) {
                    brushingPlayer = player;
                    generateLoot(player, stack);
                    return true;
                }
            }
        }

        return false;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, ExcavationBlockEntity be) {
        if (be.isAlreadyBrushing()) {
            Optional<BlockHitResult> raycast = getRaycast(be.brushingPlayer);
            ItemStack stack = be.brushingPlayer.getActiveItem();
    
            if (be.brushingCheck(raycast, stack)) {
                int time = be.brushingPlayer.getItemUseTime();
    
                be.aesteticTick(time, stack);
                be.brushingTick(time, stack);
                be.breakingTick(time, stack, raycast.get());
            }
        } else if (be.getCachedState().get(ExcavationBlock.BRUSHING_LEVEL) != 0) {
            be.breakBlock();
        }
    }

    public static Optional<BlockHitResult> getRaycast(LivingEntity user) {
        // TODO: Remove hardcoded player reach value
        HitResult result = user.raycast(4.5F, 1, false);
        return Optional.of(result instanceof BlockHitResult ? (BlockHitResult) result : null);
    }

    private boolean brushingCheck(Optional<BlockHitResult> raycast, ItemStack stack) {
        if (raycast.isPresent() && pos.equals(raycast.get().getBlockPos())) {
            if (brushingPlayer.isUsingItem() && stack.isOf(ArcheologyMod.COPPER_BRUSH)) {
                return true;
            }
        }

        breakBlock();
        return false;
    }

    private void aesteticTick(int useTime, ItemStack stack) {
        if (useTime % (ExcavationBlock.getBrushTicks(stack) / 6) == 0) {
            BlockSoundGroup soundGroup = getCachedState().getSoundGroup();
            world.playSound(null, pos, soundGroup.getBreakSound(), SoundCategory.BLOCKS, 0.3F * soundGroup.getVolume(), soundGroup.getPitch());
            world.addBlockBreakParticles(pos, getCachedState());

            world.playSound(null, brushingPlayer.getBlockPos(), ArcheologyMod.BRUSHING_SOUND_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        }
    }

    public void brushingTick(int useTime, ItemStack stack) {
        if (!world.isClient && useTime % ExcavationBlock.getBrushTicks(stack) == 0) {
            int damage = world.getRandom().nextInt(1);
            EquipmentSlot slot = brushingPlayer.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            stack.damage(damage, brushingPlayer, p -> p.sendEquipmentBreakStatus(slot));

            int newState = getCachedState().get(ExcavationBlock.BRUSHING_LEVEL) + 1;

            if (newState <= ExcavationBlock.MAX_BRUSHING_LEVELS) {
                world.setBlockState(pos, getCachedState().with(ExcavationBlock.BRUSHING_LEVEL, newState));
            } else {
                finishedBrushing();
            }
        }
    }

    public void breakingTick(int useTime, ItemStack stack, BlockHitResult hitResult) {
        if (prevLookPoint != null) {
            double magnitude = prevLookPoint.distanceTo(hitResult.getPos());
            
            float delta = getBreakingDelta(stack, magnitude);
            updateBlockBreakingProgress(Math.max(-0.05F, delta));
        }

        prevLookPoint = hitResult.getPos();
    }

    public void updateBlockBreakingProgress(float delta) {
        if (breakingProgress < 0.0F) {
            getCachedState().onBlockBreakStart(world, pos, brushingPlayer);
            world.setBlockBreakingInfo(0, pos, (int) (breakingProgress * 10.0F) - 1);
            breakingProgress = 0.0F;
            return;
        }

        breakingProgress += delta;

        if (breakingProgress >= 1.0F) {
            breakBlock();
            return;
        }

        world.setBlockBreakingInfo(0, pos, (int) (breakingProgress * 10.0F) - 1);
    }

    public void finishedBrushing() {
        for (ItemStack stack : loot) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        }

        breakBlock();
    }

    public void breakBlock() {
        if (brushingPlayer != null) {
            brushingPlayer.stopUsingItem();
            brushingPlayer.resetLastAttackedTicks();
        }

        if (!world.isClient) {
            world.setBlockBreakingInfo(0, pos, -1);
            breakingProgress = -1.0F;
    
            if (getCachedState().getBlock() instanceof ExcavationBlock) {
                world.breakBlock(pos, true);
            }
        }
    }

    public boolean isAlreadyBrushing() {
        return brushingPlayer != null;
    }
    
}
