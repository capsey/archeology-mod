package net.capsey.archeology.items.ceramic_shard;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class CeramicShard {
    
    public static final Identifier SHARDS_ATLAS_TEXTURE = new Identifier("textures/atlas/shards.png");
    public static final Identifier RAW_SHARDS_ATLAS_TEXTURE = new Identifier("textures/atlas/raw_shards.png");

    private final Identifier shardId;
    private final SpriteIdentifier[] spriteIds = new SpriteIdentifier[2];

    public CeramicShard(Identifier shardId) {
        this.shardId = shardId;
        spriteIds[0] = new SpriteIdentifier(SHARDS_ATLAS_TEXTURE, new Identifier(shardId.getNamespace(), "entity/shard/" + shardId.getPath()));
        spriteIds[1] = new SpriteIdentifier(RAW_SHARDS_ATLAS_TEXTURE, new Identifier(shardId.getNamespace(), "entity/raw_shard/" + shardId.getPath()));
    }

    public SpriteIdentifier getSpriteId(int type) {
        return spriteIds[type];
    }

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
