package net.capsey.archeology.blocks.excavation_block.client;

import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ExcavationBlockEntityRenderer implements BlockEntityRenderer<ExcavationBlockEntity> {

    public ExcavationBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(ExcavationBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity.hasLoot()) {
            matrices.push();

            matrices.translate(0.5, 0.4, 0.5);
            MinecraftClient.getInstance().getItemRenderer().renderItem(blockEntity.getDisplayLootItem(), ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers, overlay);

            matrices.pop();
        }
    }

}