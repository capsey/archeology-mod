package net.capsey.archeology.items.ceramic_shard;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class CeramicShards {

    public static Item ENDER_DRAGON;
    public static Item OVERWORLD;
    public static Item DIAMOND;
    public static Item EMERALD;

    public static void registerDefaultShards() {
        ENDER_DRAGON = CeramicShardRegistry.registerShard(
            new Identifier(ArcheologyMod.MODID, "ender_dragon_shard"), 
            new Identifier(ArcheologyMod.MODID, "ender_dragon")
        );

        OVERWORLD = CeramicShardRegistry.registerShard(
            new Identifier(ArcheologyMod.MODID, "overworld_shard"),
            new Identifier(ArcheologyMod.MODID, "overworld")
        );

        DIAMOND = CeramicShardRegistry.registerShard(
            new Identifier(ArcheologyMod.MODID, "diamond_shard"),
            new Identifier(ArcheologyMod.MODID, "diamond")
        );

        EMERALD = CeramicShardRegistry.registerShard(
            new Identifier(ArcheologyMod.MODID, "emerald_shard"),
            new Identifier(ArcheologyMod.MODID, "emerald")
        );
    }

}
