package net.capsey.archeology.blocks;

import java.util.List;

import net.capsey.archeology.ArcheologyMod;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ExcavationBlockEntity extends BlockEntity implements BlockEntityClientSerializable  {

    private static final float[] LUCK_POINTS = { 1.0F, 2.0F, 3.0F, 4.0F };

    private static float getLuckPoints(ItemStack stack) {
        if (stack.getItem() != ArcheologyMod.COPPER_BRUSH) {
            return -1;
        }

        int index = (int) Math.floor(4 * stack.getDamage() / stack.getMaxDamage());
        return LUCK_POINTS[index];
    }

    private Identifier lootTableId;

    private ItemStack loot = ItemStack.EMPTY;
    private LivingEntity brushingEntity;

    public ExcavationBlockEntity(BlockPos pos, BlockState state) {
        super(ArcheologyMod.EXCAVATION_BLOCK_ENTITY, pos, state);
        lootTableId = new Identifier("archeology", "excavation/excavation_site");
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        
        if (tag.contains("Loot")){
            loot = ItemStack.fromNbt(tag.getCompound("Loot"));
        }

        if (tag.contains("LootTable")) {
            String id = tag.getString("LootTable");
            if (!id.isBlank()) {
                lootTableId = new Identifier(id);
            }
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        if (!loot.isEmpty()) {
            NbtCompound nbtCompound = new NbtCompound();
            loot.writeNbt(nbtCompound);
            tag.put("Loot", nbtCompound);
        }

        tag.putString("LootTable", lootTableId.toString());
 
        return tag;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return writeNbt(tag);
    }

    // Brushing
    public boolean isBrushingPlayer(LivingEntity player) {
        return brushingEntity == null || brushingEntity.equals(player);
    }

    public boolean startBrushing(PlayerEntity player, ItemStack stack) {
        if (isBrushingPlayer(player)) {
            BlockState state = world.getBlockState(pos);
            brushingEntity = player;
    
            if (state.getBlock() instanceof ExcavationBlock) {
                if (state.get(ExcavationBlock.BRUSHING_LEVEL) == 0) {
                    ((ExcavationBlockEntity) world.getBlockEntity(pos)).generateLoot(player, stack);
                    return true;
                }

                stoppedBrushing();
            }
        }

        return false;
    }

    public void brushingTick(float progress, int remainingUseTicks, ItemStack stack) {
        BlockState state = world.getBlockState(pos);
        ((ExcavationBlock) state.getBlock()).visualsTick(world, pos, remainingUseTicks, stack);

        if (remainingUseTicks % ExcavationBlock.getBrushTicks(stack) == 0) {
            int num = (int) Math.floor(progress * ExcavationBlock.MAX_BRUSHING_LEVELS) + 1;

            if (num < ExcavationBlock.MAX_BRUSHING_LEVELS + 1) {
                world.setBlockState(pos, state.with(ExcavationBlock.BRUSHING_LEVEL, num));
            }
        }
    }

    public void finishedBrushing() {
        ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), loot);
        world.spawnEntity(item);
        world.breakBlock(pos, true);
    }

    public void stoppedBrushing() {
        world.breakBlock(pos, false);
    }

    // Loot
    public void generateLoot(PlayerEntity player, ItemStack stack) {
        if (this.world.isClient) {
            return;
        }
        
        LootContext.Builder builder = (new LootContext.Builder((ServerWorld)this.world))
            .parameter(LootContextParameters.TOOL, stack)
            .parameter(LootContextParameters.THIS_ENTITY, player)
            .parameter(LootContextParameters.BLOCK_ENTITY, this)
            .random(this.world.getRandom()).luck(player.getLuck() + getLuckPoints(stack));
        
        LootTable lootTable = this.world.getServer().getLootManager().getTable(lootTableId);
        List<ItemStack> list = lootTable.generateLoot(builder.build(ArcheologyMod.EXCAVATION));
        
        if (list.size() > 0) {
            loot = list.get(0);
            this.markDirty();
        }
    }
    
    public boolean isLootGenerated() {
        return !loot.isEmpty();
    }

    public ItemStack getLoot() {
        return loot;
    }
    
}
