package net.capsey.archeology.blocks.excavation_block;

import java.util.Optional;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.items.CopperBrushItem;
import net.capsey.archeology.PlayerEntityMixinInterface;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable.OxidizationLevel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;

public class ExcavationBlockEntity extends FossilContainerBlockEntity {

    private float getBreakingDelta(double magnitude) {
        switch (world.getDifficulty()) {
            case PEACEFUL: magnitude *= 1.2F; break;
            case EASY: magnitude *= 1.2F; break;
            case NORMAL: magnitude *= 1.0F; break;
            case HARD: magnitude *= 0.8F; break;
        }

        switch (oxidizationLevel) {
            case UNAFFECTED: return (float) (-0.75F * magnitude) + 0.04F;
            case EXPOSED: return (float) (-0.72F * magnitude) + 0.04F;
            case WEATHERED: return (float) (-0.67F * magnitude) + 0.05F;
            case OXIDIZED: return (float) (-0.65F * magnitude) + 0.06F;
            default: throw new IllegalStateException("Invalid Oxidization Level");
        }
    }

    private PlayerEntity brushingPlayer;
    // private ItemStack stack;
    private OxidizationLevel oxidizationLevel;
    private float breakingProgress = -1.0F;
    private Vec3d prevLookPoint;

    public ExcavationBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state, new Identifier("archeology", "excavation/excavation_site"));
    }

    public void startBrushing(PlayerEntity player, ItemStack stack) {
        if (stack.isOf(ArcheologyMod.Items.COPPER_BRUSH)) {
            this.brushingPlayer = player;
            // this.stack = stack;
            this.oxidizationLevel = CopperBrushItem.getOxidizationLevel(stack);
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

    public boolean isTime(Difficulty difficulty) {
        return (brushingPlayer.getItemUseTime() + 1) % (CopperBrushItem.getBrushTicks(oxidizationLevel) * ExcavationBlock.getBrushTicksPerLayer(difficulty)) == 0;
    }

    public boolean brushingCheck() {
        if (brushingPlayer != null && brushingPlayer.isUsingItem() && brushingPlayer.getItemUseTimeLeft() > 0) {
            ItemStack activeStack = brushingPlayer.getActiveItem();
            
            if (!activeStack.isEmpty()) { // activeStack == stack
                return true;
            }
        }
            
        return false;
    }

    public void breakingTick(BlockHitResult hitResult) {
        if (prevLookPoint != null) {
            double magnitude = prevLookPoint.distanceTo(hitResult.getPos());
            
            float delta = getBreakingDelta(magnitude);
            updateBlockBreakingProgress(Math.max(-0.05F, delta));
        }

        prevLookPoint = hitResult.getPos();
    }

    private void updateBlockBreakingProgress(float delta) {
        if (breakingProgress < 0.0F) {
            getCachedState().onBlockBreakStart(world, pos, null);
            world.setBlockBreakingInfo(0, pos, -1);
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

    public void successfullyBrushed() {
        if (brushingPlayer != null) {
            brushingPlayer.incrementStat(ArcheologyMod.EXCAVATED);
        }
    }

    public void onBlockBreak() {
        if (brushingPlayer != null) {
            ((PlayerEntityMixinInterface) brushingPlayer).resetLastBrushedTicks();
            brushingPlayer.stopUsingItem();
        }

        world.setBlockBreakingInfo(0, pos, -1);
    }

}
