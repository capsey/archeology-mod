package net.capsey.archeology.items.ceramic_shard;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public record CeramicShard(Identifier shardId) {

    public ItemStack getStack() {
        return new ItemStack(CeramicShardRegistry.getShardItem(shardId));
    }

    @Nullable
    public static CeramicShard fromNbt(NbtCompound nbt) {
        String id = nbt.getString("ShardId");
        if (Identifier.isValid(id)) {
            return CeramicShardRegistry.getShard(new Identifier(id));
        } else {
            return null;
        }
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putString("ShardId", shardId.toString());
        return nbt;
    }

}
