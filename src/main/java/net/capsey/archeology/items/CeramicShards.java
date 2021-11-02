package net.capsey.archeology.items;

import net.capsey.archeology.items.ceramic_shard.CeramicShardRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class CeramicShards {
    
    public static Item ENDER_DRAGON;
    public static Item OVERWORLD;
    public static Item DIAMOND;
    public static Item EMERALD;

    public static void registerDefaultShards() {
        ENDER_DRAGON = CeramicShardRegistry.registerShard(
            new Identifier("archeology", "ender_dragon_shard"), 
            new Identifier("archeology", "ender_dragon")
        );

        OVERWORLD = CeramicShardRegistry.registerShard(
            new Identifier("archeology", "overworld_shard"),
            new Identifier("archeology", "overworld")
        );

        DIAMOND = CeramicShardRegistry.registerShard(
            new Identifier("archeology", "diamond_shard"),
            new Identifier("archeology", "diamond")
        );

        EMERALD = CeramicShardRegistry.registerShard(
            new Identifier("archeology", "emerald_shard"),
            new Identifier("archeology", "emerald")
        );
    }

}
