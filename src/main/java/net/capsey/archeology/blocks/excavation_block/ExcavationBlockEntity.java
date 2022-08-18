package net.capsey.archeology.blocks.excavation_block;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.BlockEntities;
import net.capsey.archeology.Items;
import net.capsey.archeology.items.CopperBrushItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;

import java.util.Objects;

public class ExcavationBlockEntity extends FossilContainerBlockEntity {

    public static final Identifier DEFAULT_LOOT_TABLE = new Identifier(ArcheologyMod.MOD_ID, "excavation/ancient_ruins");

    private PlayerEntity brushingPlayer;
    private int brushTicks;

    public ExcavationBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.EXCAVATION_BLOCK_ENTITY, pos, state, DEFAULT_LOOT_TABLE);
    }

    public void setLootTable(Identifier id) {
        loot.clear();
        lootTableId = id;
    }

    public void startBrushing(PlayerEntity player, ItemStack stack) {
        if (stack.isOf(Items.COPPER_BRUSH)) {
            brushingPlayer = player;
            brushTicks = CopperBrushItem.getBrushTicks(stack);
            generateLoot(player, stack);
        }
    }

    public boolean isTime(Difficulty difficulty) {
        return brushingPlayer.getItemUseTime() % (brushTicks * ExcavationBlock.getBrushTicksPerLayer(difficulty)) == 0;
    }

    public boolean brushingCheck() {
        if (brushingPlayer != null && brushingPlayer.isUsingItem() && brushingPlayer.getItemUseTimeLeft() > 0) {
            ItemStack activeStack = brushingPlayer.getActiveItem();

            return !activeStack.isEmpty() && activeStack.isOf(Items.COPPER_BRUSH);
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
