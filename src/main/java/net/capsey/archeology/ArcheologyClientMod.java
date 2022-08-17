package net.capsey.archeology;

import net.capsey.archeology.blocks.clay_pot.client.ClayPotBlockEntityRenderer;
import net.capsey.archeology.blocks.clay_pot.client.ShardsContainerRenderer;
import net.capsey.archeology.blocks.excavation_block.client.ExcavationBlockEntityRenderer;
import net.capsey.archeology.entity.BrushingPlayerEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

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
        BlockEntityRendererRegistry.register(BlockEntities.CLAY_POT_BLOCK_ENTITY, ctx -> new ClayPotBlockEntityRenderer<>(ctx, ShardsContainerRenderer.SHARD_SPRITE_IDS, x -> x.getColor() == null ? ClayPotBlockEntityRenderer.MODEL_TEXTURE : ClayPotBlockEntityRenderer.MODEL_COLORED_TEXTURES[x.getColor().getId()]));
        BlockEntityRendererRegistry.register(BlockEntities.RAW_CLAY_POT_BLOCK_ENTITY, ctx -> new ClayPotBlockEntityRenderer<>(ctx, ShardsContainerRenderer.RAW_SHARD_SPRITE_IDS, x -> ClayPotBlockEntityRenderer.RAW_MODEL_TEXTURE));

        // Model Predicate for Copper Brush to change texture (oxidization)
        ModelPredicateProviderRegistry.register(Items.COPPER_BRUSH, new Identifier("damage"), (itemStack, clientWorld, livingEntity, i) -> (float) itemStack.getDamage() / itemStack.getMaxDamage());

        // Network
        ClientPlayNetworking.registerGlobalReceiver(ArcheologyMod.START_BRUSHING, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            client.execute(() -> ((BrushingPlayerEntity) client.player).startBrushing(pos));
        });
    }

}