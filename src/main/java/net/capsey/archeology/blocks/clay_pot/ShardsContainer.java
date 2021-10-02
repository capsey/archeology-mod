package net.capsey.archeology.blocks.clay_pot;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

public interface ShardsContainer {
    
    public ItemStack[] getShards();

    default boolean containsShards() {
        for (ItemStack itemStack : getShards()) {
            if (!itemStack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    default NbtCompound writeShardsNbt(NbtCompound nbt, DefaultedList<ItemStack> shards) {
		NbtList nbtList = new NbtList();

		for (int i = 0; i < shards.size(); ++i) {
			ItemStack itemStack = shards.get(i);

			if (!itemStack.isEmpty()) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putByte("Position", (byte) i);
				itemStack.writeNbt(nbtCompound);
				nbtList.add(nbtCompound);
			}
		}

        nbt.put("Shards", nbtList);
		return nbt;
	}

	default void readShardsNbt(NbtCompound nbt, DefaultedList<ItemStack> shards) {
		NbtList nbtList = nbt.getList("Shards", 10);

		for (int i = 0; i < nbtList.size(); ++i) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			int pos = nbtCompound.getByte("Position") & 255;

			if (pos >= 0 && pos < shards.size()) {
				shards.set(pos, ItemStack.fromNbt(nbtCompound));
			}
		}
	}

}
