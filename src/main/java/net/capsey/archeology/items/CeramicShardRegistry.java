package net.capsey.archeology.items;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class CeramicShardRegistry {
    
    public static final ItemGroup SHARDS_ITEM_GROUP = FabricItemGroupBuilder.build(
		new Identifier("archeology", "shards"),
		() -> new ItemStack(CeramicShards.ENDER_DRAGON)
    );

    private static final Map<Identifier, CeramicShard> SHARDS = new HashMap<Identifier, CeramicShard>();

    /**
     * Registering Ceramic Shard item to special Creative Menu {@link net.minecraft.item.ItemGroup ItemGroup}
     * and pattern for {@link net.capsey.archeology.blocks.clay_pot.ShardsContainer ShardsContainer}.
     * Items are added to ItemGroup in order of registering!
     * 
     * @param itemId is Identifier of shard item
     * @param rawShardId is Identifier of shard texture for the Raw Clay Pot (e.g. "archeology:raw_shard/ender_dragon")
     * @param shardId is Identifier of shard texture for the Clay Pot (e.g. "archeology:shard/ender_dragon")
     * 
     * @return Returns registered {@link CeramicShard} object
     */
    public static Item registerShard(Identifier itemId, Identifier rawShardId, Identifier shardId) {
        if (SHARDS.containsKey(itemId)) {
            throw new IllegalArgumentException(itemId + " is already a registered shard!");
        }

        Item shardItem = new CeramicShardItem(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON).group(SHARDS_ITEM_GROUP));
        Registry.register(Registry.ITEM, itemId, shardItem);
        CeramicShard shard = new CeramicShard(shardItem, itemId, rawShardId, shardId);

        SHARDS.put(itemId, shard);
        return shardItem;
    }

    public static Stream<SpriteIdentifier> getSpriteIds() {
        return SHARDS.values().stream().map(s -> s.getSpriteId());
    }

    public static Stream<SpriteIdentifier> getRawSpriteIds() {
        return SHARDS.values().stream().map(s -> s.getRawSpriteId());
    }

    public static Optional<CeramicShard> getShard(ItemStack item) {
        RegistryKey<Item> key = Registry.ITEM.getKey(item.getItem()).get();
        return Optional.ofNullable(getShard(key.getValue()));
    }

    public static CeramicShard getShard(Identifier id) {
        return SHARDS.get(id);
    }

}
