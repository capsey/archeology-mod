package net.capsey.archeology.items;

import net.capsey.archeology.ArcheologyMod;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.HashMap;
import java.util.Map;

public class CeramicShardRegistry {

    private static final Map<Identifier, CeramicShard> SHARDS = new HashMap<>();
    private static final Map<Identifier, Item> ITEMS = new HashMap<>();

    private CeramicShardRegistry() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Registering Ceramic Shard item to special Creative Menu {@link net.minecraft.item.ItemGroup ItemGroup}
     * and pattern for {@link net.capsey.archeology.blocks.clay_pot.ShardsContainer ShardsContainer}.
     * <p>
     * Items are added to shards items tab in order of registering!
     *
     * @param id        Identifier of the shard (e.g. "archeology:ender_dragon")
     * @param itemId    Identifier of the shard item (e.g. "archeology:ender_dragon_shard")
     * @param textureId Identifier of the shard texture on a texture atlas (e.g. "archeology:entity/shard/ender_dragon")
     * @return Returns registered {@link Item} object of the shard
     */
    public static Item registerShard(Identifier id, Identifier itemId, Identifier textureId, Rarity rarity) {
        if (SHARDS.containsKey(itemId)) {
            throw new IllegalArgumentException(itemId + " is already a registered shard!");
        }

        ArcheologyMod.LOGGER.info("Registering ceramic shard: {}", id);

        CeramicShard shard = new CeramicShard(id, textureId);
        Item shardItem = new CeramicShardItem(shard, new Item.Settings().maxCount(16).rarity(rarity));
        Registry.register(Registries.ITEM, itemId, shardItem);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
            content.add(new ItemStack(shardItem));
        });

        SHARDS.put(id, shard);
        ITEMS.put(id, shardItem);
        return shardItem;
    }

    public static CeramicShard getShard(Identifier id) {
        return SHARDS.get(id);
    }

    public static Item getShardItem(Identifier id) {
        return ITEMS.get(id);
    }

}
