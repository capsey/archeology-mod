package net.capsey.archeology;

import eu.midnightdust.lib.config.MidnightConfig;
import net.capsey.archeology.blocks.chiseled.ChiseledBlock;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.capsey.archeology.entity.ExcavatorPlayerEntity;
import net.capsey.archeology.items.ceramic_shard.CeramicShards;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class ArcheologyMod implements ModInitializer {

    public static final String MOD_ID = "archeology";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    // Loot context type for Fossil Container
    public static final LootContextType EXCAVATION_LOOT_CONTEXT_TYPE = createLootContextType(builder ->
            builder.allow(LootContextParameters.TOOL)
                    .allow(LootContextParameters.THIS_ENTITY)
                    .allow(LootContextParameters.BLOCK_ENTITY)
    );

    // Player Statistics
    public static final Identifier EXCAVATED = new Identifier(MOD_ID, "excavated");

    // Network Packet IDs
    public static final Identifier START_BRUSHING = new Identifier(MOD_ID, "start_brushing");
    public static final Identifier EXCAVATION_BREAKING_INFO = new Identifier(MOD_ID, "excavation_breaking_info");
    public static final Identifier EXCAVATION_STOP_BRUSHING = new Identifier(MOD_ID, "excavation_stop_brushing");
    public static final Identifier CHISEL_BLOCK_SEGMENT = new Identifier(MOD_ID, "chisel_block_segment");

    @Override
    public void onInitialize() {
        // Initializing Config
        // TODO: Separate configs for server and client
        MidnightConfig.init(MOD_ID, ModConfig.class);

        // Registering all stuff
        Blocks.onInitialize();
        BlockEntities.onInitialize();
        Items.onInitialize();
        CeramicShards.registerDefaultShards();

        // Registering other stuff
        Registry.register(Registry.SOUND_EVENT, Sounds.BRUSHING_SOUND_ID, Sounds.BRUSHING_SOUND_EVENT);
        Registry.register(Registry.CUSTOM_STAT, "excavated", EXCAVATED);
        Stats.CUSTOM.getOrCreateStat(EXCAVATED, StatFormatter.DEFAULT);

        // Networking
        ServerPlayNetworking.registerGlobalReceiver(ArcheologyMod.EXCAVATION_BREAKING_INFO, (server, player, handler, buf, sender) -> {
            if (player.getAbilities().allowModifyWorld) {
                int newStage = buf.readInt();
                server.execute(() -> {
                    ExcavationBlockEntity entity = ((ExcavatorPlayerEntity) player).getExcavatingBlock();

                    if (entity != null && !entity.isRemoved() && entity.isCorrectPlayer(player)) {
                        ServerWorld world = Objects.requireNonNull((ServerWorld) entity.getWorld());
                        world.setBlockBreakingInfo(0, entity.getPos(), MathHelper.clamp(newStage, 0, 9));
                    }
                });
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(ArcheologyMod.EXCAVATION_STOP_BRUSHING, (server, player, handler, buf, sender) -> {
            server.execute(() -> {
                ExcavationBlockEntity entity = ((ExcavatorPlayerEntity) player).getExcavatingBlock();

                if (entity != null && !entity.isRemoved() && entity.isCorrectPlayer(player)) {
                    player.stopUsingItem();
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ArcheologyMod.CHISEL_BLOCK_SEGMENT, (server, player, handler, buf, sender) -> {
            if (player.getAbilities().allowModifyWorld) {
                BlockPos pos = buf.readBlockPos();
                String segment = buf.readString();

                ChiseledBlock.Segment.get(segment).ifPresent(value -> server.execute(() -> {
                    ServerWorld world = player.getWorld();
                    ChiseledBlock.chiselSegment(world, pos, value, player);
                }));
            }
        });
    }

    private static LootContextType createLootContextType(Consumer<LootContextType.Builder> type) {
        LootContextType.Builder builder = new LootContextType.Builder();
        type.accept(builder);
        return builder.build();
    }

}
