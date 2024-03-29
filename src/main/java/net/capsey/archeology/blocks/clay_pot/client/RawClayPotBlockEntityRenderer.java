package net.capsey.archeology.blocks.clay_pot.client;

import net.capsey.archeology.blocks.clay_pot.RawClayPotBlockEntity;
import net.capsey.archeology.blocks.clay_pot.ShardsContainer;
import net.capsey.archeology.items.CeramicShard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class RawClayPotBlockEntityRenderer extends AbstractClayPotBlockEntityRenderer<RawClayPotBlockEntity> {

    public static final SpriteIdentifier MODEL_TEXTURE = spriteId("entity/raw_clay_pot");

    public RawClayPotBlockEntityRenderer(Context ctx) {
        super(ctx);
    }

    @Override
    protected SpriteIdentifier getSpriteId(RawClayPotBlockEntity entity) {
        return MODEL_TEXTURE;
    }

    @Override
    protected void renderShard(CeramicShard shard, ShardsContainer.Side side, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        super.renderShard(shard, side, matrices, vertexConsumers, light, overlay);

        VertexConsumer spriteConsumer;
        final Vector3f posRawScale = new Vector3f(-zOffset / 2, 0.0F, -zOffset / 2);
        final Vector3f negRawScale = new Vector3f(zOffset / 2, 0.0F, zOffset / 2);

        if (side.straight) {
            spriteConsumer = EMPTY_SHARD_SPRITE_ID.getVertexConsumer(vertexConsumers, RenderLayer::getEntityTranslucentCull);

            straight.scale(posRawScale);
            straight.render(matrices, spriteConsumer, light, overlay);
            straight.scale(negRawScale);
        } else {
            spriteConsumer = EMPTY_SHARD_SPRITE_ID.getVertexConsumer(vertexConsumers, RenderLayer::getEntityTranslucentCull);

            corners[0].scale(posRawScale);
            corners[0].render(matrices, spriteConsumer, light, overlay);
            corners[0].scale(negRawScale);

            corners[1].scale(negRawScale);
            corners[1].render(matrices, spriteConsumer, light, overlay);
            corners[1].scale(posRawScale);
        }
    }
}
