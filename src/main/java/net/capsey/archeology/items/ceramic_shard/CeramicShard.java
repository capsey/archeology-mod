package net.capsey.archeology.items.ceramic_shard;

import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class CeramicShard {
    
    public static final Identifier SHARDS_ATLAS_TEXTURE = new Identifier("textures/atlas/shards.png");
    public static final Identifier RAW_SHARDS_ATLAS_TEXTURE = new Identifier("textures/atlas/raw_shards.png");

    private final Item item;
    private final Identifier itemId;
    private final SpriteIdentifier[] spriteIds = new SpriteIdentifier[2];

    public CeramicShard(Item item, Identifier itemId, Identifier shardId) {
        this.item = item;
        this.itemId = itemId;
        spriteIds[0] = new SpriteIdentifier(SHARDS_ATLAS_TEXTURE, new Identifier(shardId.getNamespace(), "entity/shard/" + shardId.getPath()));
        spriteIds[1] = new SpriteIdentifier(RAW_SHARDS_ATLAS_TEXTURE, new Identifier(shardId.getNamespace(), "entity/raw_shard/" + shardId.getPath()));
    }

    public SpriteIdentifier getSpriteId(int type) {
        return spriteIds[type];
    }

    public ItemStack getStack() {
        return new ItemStack(item);
    }

    public static CeramicShard fromNbt(NbtCompound nbt) {
        String itemIdNamespace = nbt.getString("ItemIdNamespace");
        String itemIdPath = nbt.getString("ItemIdPath");

        return CeramicShardRegistry.getShard(new Identifier(itemIdNamespace, itemIdPath));
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putString("ItemIdNamespace", itemId.getNamespace());
        nbt.putString("ItemIdPath", itemId.getPath());

        return nbt;
    }

}
