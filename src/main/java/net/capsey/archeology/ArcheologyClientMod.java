package net.capsey.archeology;

import net.capsey.archeology.blocks.clay_pot.ClayPotBlockEntity;
import net.capsey.archeology.blocks.clay_pot.RawClayPotBlockEntity;
import net.capsey.archeology.blocks.clay_pot.client.ClayPotBlockEntityRenderer;
import net.capsey.archeology.blocks.clay_pot.client.ShardsContainerRenderer;
import net.capsey.archeology.blocks.excavation_block.client.ExcavationBlockEntityRenderer;
import net.capsey.archeology.entity.BrushingPlayerEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ArcheologyClientMod implements ClientModInitializer {
    
    public static final EntityModelLayer CLAY_POT_MODEL_LAYER = new EntityModelLayer(new Identifier("archeology", "clay_pot_block_entity"), "model");
    public static final EntityModelLayer CLAY_POT_SHARDS_MODEL_LAYER = new EntityModelLayer(new Identifier("archeology", "clay_pot_block_entity"), "shards");

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ArcheologyMod.BlockEntities.EXCAVATION_BLOCK_ENTITY, ExcavationBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ArcheologyMod.BlockEntities.CLAY_POT_BLOCK_ENTITY, ctx -> new ClayPotBlockEntityRenderer<ClayPotBlockEntity>(ctx, 0));
        BlockEntityRendererRegistry.register(ArcheologyMod.BlockEntities.RAW_CLAY_POT_BLOCK_ENTITY, ctx -> new ClayPotBlockEntityRenderer<RawClayPotBlockEntity>(ctx, 1));

        FabricModelPredicateProviderRegistry.register(ArcheologyMod.Items.COPPER_BRUSH, new Identifier("damage"), (itemStack, clientWorld, livingEntity, i) -> {
            return itemStack.getDamage() / itemStack.getMaxDamage();
        });

        EntityModelLayerRegistry.registerModelLayer(CLAY_POT_MODEL_LAYER, ClayPotBlockEntityRenderer::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(CLAY_POT_SHARDS_MODEL_LAYER, ShardsContainerRenderer::getTexturedModelData);

        ClientPlayNetworking.registerGlobalReceiver(ArcheologyMod.START_BRUSHING, (client, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();

            client.execute(() -> {
                ((BrushingPlayerEntity) client.player).startBrushing(pos);
            });
        });
    }

}