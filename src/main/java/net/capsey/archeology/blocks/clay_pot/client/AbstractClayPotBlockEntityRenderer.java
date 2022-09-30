package net.capsey.archeology.blocks.clay_pot.client;

import net.capsey.archeology.ArcheologyClientMod;
import net.capsey.archeology.blocks.clay_pot.ShardsContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Comparator;

@Environment(EnvType.CLIENT)
public abstract class AbstractClayPotBlockEntityRenderer<T extends ShardsContainer> extends ShardsContainerRenderer<T> {

    public static final Identifier CLAY_POTS_ATLAS_TEXTURE = new Identifier("textures/atlas/shards.png");

    private final ModelPart base;
    private final ModelPart neck;
    private final ModelPart head;

    public AbstractClayPotBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);

        ModelPart modelPart = ctx.getLayerModelPart(ArcheologyClientMod.CLAY_POT_MODEL_LAYER);
        base = modelPart.getChild("base");
        neck = modelPart.getChild("neck");
        head = modelPart.getChild("head");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartBuilder baseBuilder = ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 16.0F, 10.0F, 16.0F);
        modelPartData.addChild("base", baseBuilder, ModelTransform.NONE);

        ModelPartBuilder neckBuilder = ModelPartBuilder.create().uv(64, 0).cuboid(3.0F, 10.0F, 3.0F, 10.0F, 4.0F, 10.0F);
        modelPartData.addChild("neck", neckBuilder, ModelTransform.NONE);

        ModelPartBuilder headBuilder = ModelPartBuilder.create().uv(0, 26).cuboid(2.0F, 14.0F, 2.0F, 12.0F, 2.0F, 12.0F);
        modelPartData.addChild("head", headBuilder, ModelTransform.NONE);

        return TexturedModelData.of(modelData, 104, 40);
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        VertexConsumer consumer = getTextureId(entity).getVertexConsumer(vertexConsumers, RenderLayer::getEntityTranslucentCull);

        base.render(matrices, consumer, light, overlay);
        neck.render(matrices, consumer, light, overlay);
        head.render(matrices, consumer, light, overlay);

        super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay);
    }

    protected abstract SpriteIdentifier getTextureId(T entity);

}
