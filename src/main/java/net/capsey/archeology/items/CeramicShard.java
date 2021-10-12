package net.capsey.archeology.items;

import net.capsey.archeology.blocks.clay_pot.ShardsContainerRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class CeramicShard {
    
    private final Item item;
    private final Identifier itemId;
    private final SpriteIdentifier spriteId;

    public CeramicShard(Item item, Identifier itemId, Identifier shardId) {
        this.item = item;
        this.itemId = itemId;
        this.spriteId = new SpriteIdentifier(ShardsContainerRenderer.ATLAS_TEXTURE, shardId);
    }

    public SpriteIdentifier getSpriteId() {
        return spriteId;
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
