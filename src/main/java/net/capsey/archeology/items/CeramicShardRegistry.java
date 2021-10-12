package net.capsey.archeology.items;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
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
     * @param shardId is Identifier of your shard texture (e.g. "archeology:shard/ender_dragon")
     * 
     * @return Returns registered {@link CeramicShard} object
     */
    public static Item registerShard(Identifier itemId, Identifier shardId) {
        if (SHARDS.containsKey(itemId)) {
            throw new IllegalArgumentException(itemId + " is already a registered shard!");
        }

        Item shardItem = new CeramicShardItem(new Item.Settings().maxCount(16).rarity(Rarity.UNCOMMON).group(SHARDS_ITEM_GROUP));
        Registry.register(Registry.ITEM, itemId, shardItem);
        CeramicShard shard = new CeramicShard(shardItem, itemId, shardId);

        SHARDS.put(itemId, shard);
        return shardItem;
    }

    public static boolean isItemShard(ItemStack item) {
        return Registry.ITEM.getKey(item.getItem()).isPresent();
    }

    public static CeramicShard getShard(ItemStack item) {
        Optional<RegistryKey<Item>> key = Registry.ITEM.getKey(item.getItem());
        
        if (!key.isPresent()) {
            throw new InvalidParameterException("This Item is not a registered shard!");
        }

        return getShard(key.get().getValue());
    }

    public static CeramicShard getShard(Identifier id) {
        return SHARDS.get(id);
    }

}
