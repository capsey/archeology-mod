package net.capsey.archeology;

import me.shedaniel.autoconfig.AutoConfig;
import net.capsey.archeology.blocks.clay_pot.ClayPotBlockEntity;
import net.capsey.archeology.blocks.clay_pot.ClayPotBlockEntityRenderer;
import net.capsey.archeology.blocks.clay_pot.RawClayPotBlockEntity;
import net.capsey.archeology.blocks.clay_pot.RawClayPotBlockEntityRenderer;
import net.capsey.archeology.blocks.clay_pot.ShardsContainer;
import net.capsey.archeology.blocks.clay_pot.ShardsContainerRenderer;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ArcheologyClientMod implements ClientModInitializer {

    public static final EntityModelLayer CLAY_POT_SHARDS_MODEL_LAYER = new EntityModelLayer(new Identifier("archeology", "clay_pot_block_entity"), "shards");
    public static final EntityModelLayer RAW_CLAY_POT_SHARDS_MODEL_LAYER = new EntityModelLayer(new Identifier("archeology", "raw_clay_pot_block_entity"), "shards");

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ArcheologyMod.EXCAVATION_BLOCK_ENTITY, ExcavationBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ArcheologyMod.RAW_CLAY_POT_BLOCK_ENTITY, RawClayPotBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ArcheologyMod.CLAY_POT_BLOCK_ENTITY, ClayPotBlockEntityRenderer::new);

        FabricModelPredicateProviderRegistry.register(ArcheologyMod.COPPER_BRUSH, new Identifier("damage"), (itemStack, clientWorld, livingEntity, i) -> {
            return itemStack.getDamage() / itemStack.getMaxDamage();
        });

        EntityModelLayerRegistry.registerModelLayer(CLAY_POT_SHARDS_MODEL_LAYER, ShardsContainerRenderer::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(RAW_CLAY_POT_SHARDS_MODEL_LAYER, ShardsContainerRenderer::getTexturedModelData);

        ClientPlayNetworking.registerGlobalReceiver(ArcheologyMod.STOPPED_BRUSHING_PACKET_ID, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                client.player.resetLastAttackedTicks();
                client.player.stopUsingItem();
	
                ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).get();
    
                if (config.stopUsingAfterBrushing) {
                    client.options.keyUse.setPressed(false);
                }
            });
        });
    }

    public static EntityModelLayer getModelLayer(Class<? extends ShardsContainer> c) {
        if (c.isAssignableFrom(ClayPotBlockEntity.class)) {
            return CLAY_POT_SHARDS_MODEL_LAYER;
        } else if (c.isAssignableFrom(RawClayPotBlockEntity.class)) {
            return RAW_CLAY_POT_SHARDS_MODEL_LAYER;
        }

        throw new UnsupportedOperationException();
    }

}