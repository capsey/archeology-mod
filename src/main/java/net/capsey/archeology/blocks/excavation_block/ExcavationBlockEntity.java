package net.capsey.archeology.blocks.excavation_block;

import java.util.Optional;

import net.capsey.archeology.ArcheologyMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ExcavationBlockEntity extends FossilContainerBlockEntity {

    private static float getBreakingDelta(ItemStack stack, double magnitude) {
        if (!stack.isOf(ArcheologyMod.Items.COPPER_BRUSH)) {
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

    private ServerPlayerEntity brushingPlayer;
    private ItemStack stack;
    private float breakingProgress = -1.0F;
    private Vec3d prevLookPoint;

    public ExcavationBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public void startBrushing(ServerPlayerEntity player, ItemStack stack) {
        if (stack.isOf(ArcheologyMod.Items.COPPER_BRUSH)) {
            this.brushingPlayer = player;
            this.stack = stack;
            generateLoot(player, stack);
        }
    }

    public Optional<BlockHitResult> getRaycast() {
        if (brushingPlayer != null) {
            // TODO: Remove hardcoded player reach value
            HitResult result = brushingPlayer.raycast(4.5F, 1, false);

            if (result instanceof BlockHitResult) {
                return Optional.of((BlockHitResult) result);
            }
        }

        return Optional.empty();
    }

    public ItemStack getStack() {
        return stack;
    }

    public boolean isTime() {
        return brushingPlayer.getItemUseTime() % ExcavationBlock.getBrushTicks(stack) == 1;
    }

    public boolean brushingCheck() {
        if (brushingPlayer != null && brushingPlayer.isUsingItem()) {
            ItemStack activeStack = brushingPlayer.getActiveItem();
            
            if (activeStack == stack) {
                return true;
            }
        }
            
        return false;
    }

    public void brushingTick() {
        int damage = world.getRandom().nextInt(1);
        EquipmentSlot slot = brushingPlayer.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        stack.damage(damage, brushingPlayer, p -> p.sendEquipmentBreakStatus(slot));
    }

    public void breakingTick(BlockHitResult hitResult) {
        if (prevLookPoint != null) {
            double magnitude = prevLookPoint.distanceTo(hitResult.getPos());
            
            float delta = getBreakingDelta(stack, magnitude);
            updateBlockBreakingProgress(Math.max(-0.05F, delta));
        }

        prevLookPoint = hitResult.getPos();
    }

    private void updateBlockBreakingProgress(float delta) {
        if (breakingProgress < 0.0F) {
            getCachedState().onBlockBreakStart(world, pos, brushingPlayer);
            world.setBlockBreakingInfo(0, pos, (int) (breakingProgress * 10.0F) - 1);
            breakingProgress = 0.0F;
            return;
        }

        breakingProgress += delta;

        if (breakingProgress >= 1.0F) {
            world.breakBlock(pos, true);
            return;
        }

        world.setBlockBreakingInfo(0, pos, (int) (breakingProgress * 10.0F) - 1);
    }

    public void onBlockBreak() {
        if (brushingPlayer != null) {
            brushingPlayer.resetLastAttackedTicks();
            brushingPlayer.stopUsingItem();

            ServerPlayNetworking.send(brushingPlayer, ArcheologyMod.STOPPED_BRUSHING_PACKET_ID, PacketByteBufs.empty());
        }

        world.setBlockBreakingInfo(0, pos, -1);
    }

}
