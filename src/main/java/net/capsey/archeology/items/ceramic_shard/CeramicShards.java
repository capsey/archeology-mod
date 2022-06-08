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
                new Identifier(ArcheologyMod.MOD_ID, "ender_dragon_shard"),
                new Identifier(ArcheologyMod.MOD_ID, "ender_dragon")
        );

        OVERWORLD = CeramicShardRegistry.registerShard(
                new Identifier(ArcheologyMod.MOD_ID, "overworld_shard"),
                new Identifier(ArcheologyMod.MOD_ID, "overworld")
        );

        DIAMOND = CeramicShardRegistry.registerShard(
                new Identifier(ArcheologyMod.MOD_ID, "diamond_shard"),
                new Identifier(ArcheologyMod.MOD_ID, "diamond")
        );

        EMERALD = CeramicShardRegistry.registerShard(
                new Identifier(ArcheologyMod.MOD_ID, "emerald_shard"),
                new Identifier(ArcheologyMod.MOD_ID, "emerald")
        );
    }

}
