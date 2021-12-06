package net.capsey.archeology.blocks.excavation_block;

import java.util.Objects;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.items.CopperBrushItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;

public class ExcavationBlockEntity extends FossilContainerBlockEntity {

    private PlayerEntity brushingPlayer;
    private int brushTicks;

    public ExcavationBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state, new Identifier(ArcheologyMod.MODID, "excavation/excavation_site"));
    }

    public void startBrushing(PlayerEntity player, ItemStack stack) {
        if (stack.isOf(ArcheologyMod.Items.COPPER_BRUSH)) {
            this.brushingPlayer = player;
            this.brushTicks = CopperBrushItem.getBrushTicks(stack);
            generateLoot(player, stack);
        }
    }

    public boolean isTime(Difficulty difficulty) {
        return brushingPlayer.getItemUseTime() % (brushTicks * ExcavationBlock.getBrushTicksPerLayer(difficulty)) == 0;
    }

    public boolean brushingCheck() {
        if (brushingPlayer != null && brushingPlayer.isUsingItem() && brushingPlayer.getItemUseTimeLeft() > 0) {
            ItemStack activeStack = brushingPlayer.getActiveItem();
            
            if (!activeStack.isEmpty() && activeStack.isOf(ArcheologyMod.Items.COPPER_BRUSH)) {
                return true;
            }
        }
            
        return false;
    }

    public void successfullyBrushed() {
        if (brushingPlayer != null) {
            brushingPlayer.incrementStat(ArcheologyMod.EXCAVATED);
        }
    }

    public void onBlockBreak() {
        if (brushingPlayer != null && brushingPlayer.isUsingItem() && brushingPlayer.getItemUseTimeLeft() > 0) {
            brushingPlayer.stopUsingItem();
        }
    }

    public boolean isCorrectPlayer(PlayerEntity entity) {
        return Objects.equals(brushingPlayer, entity);
    }

}
