package net.capsey.archeology.items;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class CeramicShards {
    
    public static Item ENDER_DRAGON;

    public static void registerDefaultShards() {
        ENDER_DRAGON = CeramicShardRegistry.registerShard(
            new Identifier("archeology", "ender_dragon_shard"), 
            new Identifier("archeology", "shard/ender_dragon")
        );
    }

}
