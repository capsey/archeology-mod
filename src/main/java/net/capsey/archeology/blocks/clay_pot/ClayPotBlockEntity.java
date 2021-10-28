package net.capsey.archeology.blocks.clay_pot;

import java.util.EnumMap;
import java.util.stream.IntStream;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.items.CeramicShard;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ClayPotBlockEntity extends ShardsContainer implements SidedInventory {

    private static final int[] AVAILABLE_SLOTS = IntStream.range(0, 9).toArray();

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

    public ClayPotBlockEntity(BlockPos pos, BlockState state, EnumMap<Side, CeramicShard> shards) {
        super(ArcheologyMod.BlockEntities.CLAY_POT_BLOCK_ENTITY, pos, state);
        replaceShards(shards);
    }

    public ClayPotBlockEntity(BlockPos pos, BlockState state) {
        super(ArcheologyMod.BlockEntities.CLAY_POT_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, items);
    }
 
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);

        return nbt;
    }
    
    @Override
    public int size() {
        return items.size();
    }
    
    @Override
    public boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            if (!getStack(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }
    
    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }
    
    @Override
    public ItemStack removeStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(items, slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }

        return result;
    }
    
    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(items, slot);
    }
    
    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
    }
    
    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return AVAILABLE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

}
