package net.capsey.archeology.blocks.excavation_block;

import java.util.Objects;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.items.CopperBrushItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable.OxidizationLevel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
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
        return brushingPlayer.getItemUseTime() % (CopperBrushItem.getBrushTicks(oxidizationLevel) * ExcavationBlock.getBrushTicksPerLayer(difficulty)) == 0;
    }

    public void aesteticTick() {
        if (brushingPlayer.getItemUseTime() % CopperBrushItem.getBrushTicks(oxidizationLevel) == 0) {
            BlockSoundGroup soundGroup = getCachedState().getSoundGroup();
            world.playSound(null, pos, soundGroup.getBreakSound(), SoundCategory.BLOCKS, 0.3F * soundGroup.getVolume(), soundGroup.getPitch());
            world.playSound(null, pos, ArcheologyMod.BRUSHING_SOUND_EVENT, SoundCategory.PLAYERS, 1f, 1f);
        }
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

    public boolean isCorrectPlayer(PlayerEntity entity) {
        return Objects.equals(brushingPlayer, entity);
    }

}
