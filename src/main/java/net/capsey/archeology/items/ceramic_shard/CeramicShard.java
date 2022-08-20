package net.capsey.archeology.items.ceramic_shard;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record CeramicShard(Identifier shardId) {

    @Nullable
    public static CeramicShard fromNbt(NbtCompound nbt) {
        String id = nbt.getString("ShardId");
        if (Identifier.isValid(id)) {
            return CeramicShardRegistry.getShard(new Identifier(id));
        } else {
            return null;
        }
    }

    public ItemStack getStack() {
        return new ItemStack(CeramicShardRegistry.getShardItem(shardId));
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putString("ShardId", shardId.toString());
    }

}
