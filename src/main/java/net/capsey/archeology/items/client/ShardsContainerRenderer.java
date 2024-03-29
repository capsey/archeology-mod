package net.capsey.archeology.items.client;

import net.capsey.archeology.ArcheologyClientMod;
import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.clay_pot.AbstractClayPotBlock;
import net.capsey.archeology.blocks.clay_pot.ShardsContainer;
import net.capsey.archeology.blocks.clay_pot.ShardsContainer.Side;
import net.capsey.archeology.items.CeramicShard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public abstract class ShardsContainerRenderer<T extends ShardsContainer> implements BlockEntityRenderer<T> {

    public static final Identifier ATLAS_TEXTURE_ID = new Identifier(ArcheologyMod.MOD_ID, "textures/atlas/shards.png");
    public static final SpriteIdentifier EMPTY_SHARD_SPRITE_ID = spriteId("entity/empty_shard");
    public static final Map<Identifier, SpriteIdentifier> SHARD_SPRITE_IDS = new HashMap<>();
    protected static final float zOffset = 0.0001F;
    protected final ModelPart straight;
    protected final ModelPart[] corners = new ModelPart[2];

    protected ShardsContainerRenderer(BlockEntityRendererFactory.Context ctx) {
        ModelPart modelPart = ctx.getLayerModelPart(ArcheologyClientMod.CLAY_POT_SHARDS_MODEL_LAYER);
        straight = modelPart.getChild("straight");
        corners[0] = modelPart.getChild("corner-0");
        corners[1] = modelPart.getChild("corner-1");

        Vector3f scale = new Vector3f(-2.0F - zOffset, -2.0F, zOffset);
        straight.scale(scale);
        corners[0].scale(scale);
        corners[1].scale(scale);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartBuilder straightBuilder = ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -5.0F, -8.0F, 16.0F, 10.0F, 0.0F);
        modelPartData.addChild("straight", straightBuilder, ModelTransform.pivot(8.0F, 5.0F, 8.0F));

        ModelPartBuilder corner0Builder = ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -5.0F, -8.0F, 8.0F, 10.0F, 0.0F);
        modelPartData.addChild("corner-0", corner0Builder, ModelTransform.pivot(8.0F, 5.0F, 8.0F));

        ModelPartBuilder corner1Builder = ModelPartBuilder.create().uv(0, -8).cuboid(8.0F, -5.0F, -8.0F, 0.0F, 10.0F, 8.0F);
        modelPartData.addChild("corner-1", corner1Builder, ModelTransform.pivot(8.0F, 5.0F, 8.0F));

        return TexturedModelData.of(modelData, 32, 10);
    }

    private static SpriteIdentifier spriteId(String id) {
        return spriteId(new Identifier(ArcheologyMod.MOD_ID, id));
    }

    private static SpriteIdentifier spriteId(Identifier id) {
        return new SpriteIdentifier(ATLAS_TEXTURE_ID, id);
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.hasShards()) {
            Direction facing = entity.getCachedState().get(AbstractClayPotBlock.FACING);
            matrices.push();

            for (Side side : Side.values()) {
                CeramicShard shard = entity.getShard(side.rotate(facing));

                if (shard != null) {
                    renderShard(shard, side, matrices, vertexConsumers, light, overlay);
                }
            }

            matrices.pop();
        }
    }

    protected void renderShard(CeramicShard shard, Side side, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SpriteIdentifier spriteIdentifier = SHARD_SPRITE_IDS.computeIfAbsent(shard.textureId(), ShardsContainerRenderer::spriteId);
        VertexConsumer spriteConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityTranslucentCull);

        if (side.straight) {
            straight.setAngles(0.0F, side.id * MathHelper.PI / 4, 0.0F);
            straight.render(matrices, spriteConsumer, light, overlay);
        } else {
            float yaw = (side.id - 1) * MathHelper.PI / 4;

            corners[0].setAngles(0.0F, yaw, 0.0F);
            corners[0].render(matrices, spriteConsumer, light, overlay);

            corners[1].setAngles(0.0F, yaw, 0.0F);
            corners[1].render(matrices, spriteConsumer, light, overlay);
        }
    }

}
