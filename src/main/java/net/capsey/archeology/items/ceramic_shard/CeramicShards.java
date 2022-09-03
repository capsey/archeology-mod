package net.capsey.archeology.items.ceramic_shard;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class CeramicShards {

    // Plains
    public static Item ENDER_DRAGON;
    public static Item OVERWORLD;
    public static Item DIAMOND;
    public static Item EMERALD;
    public static Item CREEPER;

    // Snow
    public static Item SNOWFLAKE;

    public static void registerDefaultShards() {
        ENDER_DRAGON = register("ender_dragon");
        OVERWORLD = register("overworld");
        DIAMOND = register("diamond");
        EMERALD = register("emerald");
        CREEPER = register("creeper");

        SNOWFLAKE = register("snowflake");
    }

    public static Item register(String id) {
        return CeramicShardRegistry.registerShard(
                new Identifier(ArcheologyMod.MOD_ID, id + "_shard"),
                new Identifier(ArcheologyMod.MOD_ID, id)
        );
    }

}
