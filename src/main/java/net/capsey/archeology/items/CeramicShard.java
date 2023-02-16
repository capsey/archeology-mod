package net.capsey.archeology.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record CeramicShard(Identifier id, Identifier textureId) {

    public static final String SHARD_ID_TAG = "ShardId";

    @Nullable
    public static CeramicShard fromNbt(NbtCompound nbt) {
        String id = nbt.getString(SHARD_ID_TAG);
        if (Identifier.isValid(id)) {
            return CeramicShardRegistry.getShard(new Identifier(id));
        } else {
            return null;
        }
    }

    public ItemStack getStack() {
        return new ItemStack(CeramicShardRegistry.getShardItem(id));
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putString(SHARD_ID_TAG, id.toString());
    }

}
