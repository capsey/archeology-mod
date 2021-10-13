package net.capsey.archeology.items;

import net.capsey.archeology.blocks.clay_pot.ClayPotBlockEntity;
import net.capsey.archeology.blocks.clay_pot.RawClayPotBlockEntity;
import net.capsey.archeology.blocks.clay_pot.ShardsContainer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class CeramicShard {
    
    public static final Identifier RAW_SHARDS_ATLAS_TEXTURE = new Identifier("textures/atlas/raw_shards.png");
    public static final Identifier SHARDS_ATLAS_TEXTURE = new Identifier("textures/atlas/shards.png");

    private final Item item;
    private final Identifier itemId;
    private final SpriteIdentifier rawSpriteId;
    private final SpriteIdentifier spriteId;

    public CeramicShard(Item item, Identifier itemId, Identifier rawShardId, Identifier shardId) {
        this.item = item;
        this.itemId = itemId;
        this.rawSpriteId = new SpriteIdentifier(RAW_SHARDS_ATLAS_TEXTURE, rawShardId);
        this.spriteId = new SpriteIdentifier(SHARDS_ATLAS_TEXTURE, shardId);
    }

    public SpriteIdentifier getSpriteId(Class<? extends ShardsContainer> containerClass) {
        if (containerClass.isAssignableFrom(ClayPotBlockEntity.class)) {
            return spriteId;
        } else if (containerClass.isAssignableFrom(RawClayPotBlockEntity.class)) {
            return rawSpriteId;
        }

        throw new UnsupportedOperationException();
    }

    public SpriteIdentifier getSpriteId() {
        return spriteId;
    }

    public SpriteIdentifier getRawSpriteId() {
        return rawSpriteId;
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
