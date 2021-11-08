package net.capsey.archeology.items.ceramic_shard;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import net.capsey.archeology.items.CeramicShardItem;
import net.capsey.archeology.items.CeramicShards;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class CeramicShardRegistry {
    
    public static final ItemGroup SHARDS_ITEM_GROUP = FabricItemGroupBuilder.build(
		new Identifier("archeology", "shards"),
		() -> new ItemStack(CeramicShards.ENDER_DRAGON)
    );

    private static final Map<Identifier, CeramicShard> SHARDS = new HashMap<>();
    private static final Map<Identifier, Item> ITEMS = new HashMap<>();

    private CeramicShardRegistry() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Registering Ceramic Shard item to special Creative Menu {@link net.minecraft.item.ItemGroup ItemGroup}
     * and pattern for {@link net.capsey.archeology.blocks.clay_pot.ShardsContainer ShardsContainer}.
     * Items are added to ItemGroup in order of registering!
     * 
     * @param itemId is Identifier of shard item
     * @param shardId is Identifier of shard texture for the Clay Pot (e.g. "archeology:ender_dragon")
     * 
     * @return Returns registered {@link Item} object of the shard
     */
    public static Item registerShard(Identifier itemId, Identifier shardId) {
        if (SHARDS.containsKey(itemId)) {
            throw new IllegalArgumentException(itemId + " is already a registered shard!");
        }

        CeramicShard shard = new CeramicShard(shardId);
        Item shardItem = new CeramicShardItem(shard, new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON).group(SHARDS_ITEM_GROUP));
        Registry.register(Registry.ITEM, itemId, shardItem);

        SHARDS.put(shardId, shard);
        ITEMS.put(shardId, shardItem);
        return shardItem;
    }

    public static Stream<SpriteIdentifier> getSpriteIds() {
        return SHARDS.values().stream().map(s -> s.getSpriteId(0));
    }

    public static Stream<SpriteIdentifier> getRawSpriteIds() {
        return SHARDS.values().stream().map(s -> s.getSpriteId(1));
    }

    public static CeramicShard getShard(Identifier id) {
        return SHARDS.get(id);
    }

    public static Item getShardItem(Identifier id) {
        return ITEMS.get(id);
    }

}
