package net.capsey.archeology.blocks.excavation_block;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.entity.PlayerEntityMixinInterface;
import net.capsey.archeology.items.CopperBrushItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable.OxidizationLevel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;

public class ExcavationBlockEntity extends FossilContainerBlockEntity {

    private PlayerEntity brushingPlayer;
    private OxidizationLevel oxidizationLevel;

    public ExcavationBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state, new Identifier("archeology", "excavation/excavation_site"));
    }

    public void startBrushing(PlayerEntity player, ItemStack stack) {
        if (stack.isOf(ArcheologyMod.Items.COPPER_BRUSH)) {
            this.brushingPlayer = player;
            this.oxidizationLevel = CopperBrushItem.getOxidizationLevel(stack);
            generateLoot(player, stack);
        }
    }

    public boolean isTime(Difficulty difficulty) {
        return (brushingPlayer.getItemUseTime() + 1) % (CopperBrushItem.getBrushTicks(oxidizationLevel) * ExcavationBlock.getBrushTicksPerLayer(difficulty)) == 0;
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
        if (brushingPlayer != null) {
            ((PlayerEntityMixinInterface) brushingPlayer).resetLastBrushedTicks();
            brushingPlayer.stopUsingItem();
        }

        world.setBlockBreakingInfo(0, pos, -1);
    }

}
