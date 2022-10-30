package net.capsey.archeology;

import net.capsey.archeology.blocks.clay_pot.client.ClayPotBlockEntityRenderer;
import net.capsey.archeology.blocks.clay_pot.client.RawClayPotBlockEntityRenderer;
import net.capsey.archeology.blocks.clay_pot.client.ShardsContainerRenderer;
import net.capsey.archeology.blocks.excavation_block.client.ExcavationBlockEntityRenderer;
import net.capsey.archeology.main.BlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ArcheologyClientMod implements ClientModInitializer {

    public static final EntityModelLayer CLAY_POT_MODEL_LAYER = new EntityModelLayer(new Identifier(ArcheologyMod.MOD_ID, "clay_pot_block_entity"), "model");
    public static final EntityModelLayer CLAY_POT_SHARDS_MODEL_LAYER = new EntityModelLayer(new Identifier(ArcheologyMod.MOD_ID, "clay_pot_block_entity"), "shards");

    @Override
    public void onInitializeClient() {
        // Registering Model Layers
        EntityModelLayerRegistry.registerModelLayer(CLAY_POT_MODEL_LAYER, ClayPotBlockEntityRenderer::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(CLAY_POT_SHARDS_MODEL_LAYER, ShardsContainerRenderer::getTexturedModelData);

        // Renderers registration
        BlockEntityRendererRegistry.register(BlockEntities.EXCAVATION_BLOCK_ENTITY, ExcavationBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.CLAY_POT_BLOCK_ENTITY, ClayPotBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(BlockEntities.RAW_CLAY_POT_BLOCK_ENTITY, RawClayPotBlockEntityRenderer::new);
    }

}