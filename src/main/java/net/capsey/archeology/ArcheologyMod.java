package net.capsey.archeology;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.capsey.archeology.items.ceramic_shard.CeramicShards;
import net.fabricmc.api.ModInitializer;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ArcheologyMod implements ModInitializer {

	public static final String MODID = "archeology";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	// Loot context type for Fossil Container
	public static final LootContextType EXCAVATION_LOOT_CONTEXT_TYPE = createLootContextType(builder ->
		builder.allow(LootContextParameters.TOOL)
				.allow(LootContextParameters.THIS_ENTITY)
				.allow(LootContextParameters.BLOCK_ENTITY)
	);

	// Player Statistics
	public static final Identifier EXCAVATED = new Identifier(MODID, "excavated");

	// S2C Network Packet ID
	public static final Identifier START_BRUSHING = new Identifier(MODID, "start_brushing");

	@Override
	public void onInitialize() {
		// Adding Config
		// TODO: Separate configs for server and client
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);

		// Registering all stuff
		Blocks.onInitialize();
		BlockEntities.onInitialize();
		Items.onInitialize();
		CeramicShards.registerDefaultShards();
		Features.onInitialize();

		// Registering other stuff
		Registry.register(Registry.SOUND_EVENT, Sounds.BRUSHING_SOUND_ID, Sounds.BRUSHING_SOUND_EVENT);
		Registry.register(Registry.CUSTOM_STAT, "excavated", EXCAVATED);
		Stats.CUSTOM.getOrCreateStat(EXCAVATED, StatFormatter.DEFAULT);
	}

	private static LootContextType createLootContextType(Consumer<LootContextType.Builder> type) {
		LootContextType.Builder builder = new LootContextType.Builder();
		type.accept(builder);
		return builder.build();
	}
	
}
