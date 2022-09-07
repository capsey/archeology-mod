package net.capsey.archeology.items.ceramic_shard;

import net.capsey.archeology.ArcheologyMod;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class CeramicShards {

    public static Item ENDER_DRAGON;

    // Desert
    public static Item CREEPER;
    public static Item SHOVEL;
    public static Item TEMPLE;

    // Mangrove
    public static Item HOE;
    public static Item PROPAGULE;
    public static Item SLIME;

    // Plains
    public static Item DIAMOND;
    public static Item SUN;
    public static Item SWORD;

    // Snow
    public static Item AXE;
    public static Item EMERALD;
    public static Item SNOWFLAKE;

    public static void registerDefaultShards() {
        ENDER_DRAGON = register("ender_dragon", true);

        CREEPER = register("creeper");
        SHOVEL = register("shovel");
        TEMPLE = register("temple");

        HOE = register("hoe");
        PROPAGULE = register("propagule");
        SLIME = register("slime");

        DIAMOND = register("diamond");
        SUN = register("sun");
        SWORD = register("sword");

        AXE = register("axe");
        EMERALD = register("emerald");
        SNOWFLAKE = register("snowflake");
    }

    public static Item register(String id) {
        return register(id, false);
    }

    public static Item register(String id, boolean rare) {
        return CeramicShardRegistry.registerShard(
                new Identifier(ArcheologyMod.MOD_ID, id + "_shard"),
                new Identifier(ArcheologyMod.MOD_ID, id),
                rare ? Rarity.RARE : Rarity.UNCOMMON
        );
    }

}
