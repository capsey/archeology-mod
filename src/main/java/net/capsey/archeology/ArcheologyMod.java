package net.capsey.archeology;

import eu.midnightdust.lib.config.MidnightConfig;
import net.capsey.archeology.advancement.ExcavationCriterion;
import net.capsey.archeology.items.CeramicShards;
import net.capsey.archeology.main.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    // Advancements
    public static final ExcavationCriterion EXCAVATION_SUCCESS_CRITERION = new ExcavationCriterion("excavation_success");
    public static final ExcavationCriterion EXCAVATION_FAILURE_CRITERION = new ExcavationCriterion("excavation_failure");

    @Override
    public void onInitialize() {
        // Initializing Config
        MidnightConfig.init(MOD_ID, ModConfig.class);

        // Registering all stuff
        Blocks.onInitialize();
        BlockEntities.onInitialize();
        Items.onInitialize();
        Sounds.onInitialize();
        Structures.onInitialize();
        CeramicShards.registerDefaultShards();

        // Registering other stuff
        Registry.register(Registries.CUSTOM_STAT, "excavated", EXCAVATED);
        Stats.CUSTOM.getOrCreateStat(EXCAVATED, StatFormatter.DEFAULT);

        Criteria.register(EXCAVATION_SUCCESS_CRITERION);
        Criteria.register(EXCAVATION_FAILURE_CRITERION);
    }

    private static LootContextType createLootContextType(Consumer<LootContextType.Builder> type) {
        LootContextType.Builder builder = new LootContextType.Builder();
        type.accept(builder);
        return builder.build();
    }

}
