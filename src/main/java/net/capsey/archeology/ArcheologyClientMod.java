package net.capsey.archeology;

import net.capsey.archeology.blocks.clay_pot.RawClayPotBlockEntityRenderer;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class ArcheologyClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ArcheologyMod.EXCAVATION_BLOCK_ENTITY, ExcavationBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ArcheologyMod.RAW_CLAY_POT_BLOCK_ENTITY, RawClayPotBlockEntityRenderer::new);

        FabricModelPredicateProviderRegistry.register(ArcheologyMod.COPPER_BRUSH, new Identifier("damage"), (itemStack, clientWorld, livingEntity, i) -> {
            return itemStack.getDamage() / itemStack.getMaxDamage();
        });

        EntityModelLayerRegistry.registerModelLayer(RawClayPotBlockEntityRenderer.SHARDS_MODEL_LAYER, RawClayPotBlockEntityRenderer::getTexturedModelData);
    }

}
