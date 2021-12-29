package net.capsey.archeology.blocks.clay_pot.client;

import java.util.HashMap;
import java.util.Map;

import net.capsey.archeology.ArcheologyClientMod;
import net.capsey.archeology.blocks.clay_pot.ShardsContainer;
import net.capsey.archeology.blocks.clay_pot.ShardsContainer.Side;
import net.capsey.archeology.items.ceramic_shard.CeramicShard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public abstract class ShardsContainerRenderer<T extends ShardsContainer> implements BlockEntityRenderer<T> {

	public static final Map<Identifier, SpriteIdentifier> SHARD_SPRITE_IDS = new HashMap<>();
	public static final Map<Identifier, SpriteIdentifier> RAW_SHARD_SPRITE_IDS = new HashMap<>();

	protected final Map<Identifier, SpriteIdentifier> spriteIds;

    private final ModelPart straight;
	private final ModelPart[] corners = new ModelPart[2];

	protected ShardsContainerRenderer(BlockEntityRendererFactory.Context ctx, Map<Identifier, SpriteIdentifier> spriteIds) {
		this.spriteIds = spriteIds;

		ModelPart modelPart = ctx.getLayerModelPart(ArcheologyClientMod.CLAY_POT_SHARDS_MODEL_LAYER);
		this.straight = modelPart.getChild("straight");
		this.corners[0] = modelPart.getChild("corner-0");
		this.corners[1] = modelPart.getChild("corner-1");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartBuilder straightBuilder = ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 16.0F, 10.0F, 0.0F);
		modelPartData.addChild("straight", straightBuilder, ModelTransform.NONE);

		ModelPartBuilder corner0Builder = ModelPartBuilder.create().uv(0, 0).cuboid(8.0F, 0.0F, 0.0F, 8.0F, 10.0F, 0.0F);
		modelPartData.addChild("corner-0", corner0Builder, ModelTransform.NONE);

		ModelPartBuilder corner1Builder = ModelPartBuilder.create().uv(0, -8).cuboid(16.0F, 0.0F, 0.0F, 0.0F, 10.0F, 8.0F);
		modelPartData.addChild("corner-1", corner1Builder, ModelTransform.NONE);

		return TexturedModelData.of(modelData, 32, 10);
	}

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.hasShards()) {
			matrices.push();

			// Z-fighting fix
			matrices.scale(1.0002F, 1.0F, 1.0002F);
			matrices.translate(-0.0001F, 0.0F, -0.0001F);
			
			// Upside down fix
			matrices.scale(-1.0F, -1.0F, 1.0F);
			matrices.translate(-1.0F, -(10.0F / 16), 0.0F);
			
			for (Side side : Side.values()) {
				CeramicShard shard = entity.getShard(side);

				if (shard != null) {
					renderShard(shard, side, matrices, vertexConsumers, light, overlay);
				}

				if (side.straight) {
					matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
					matrices.translate(-1.0F, 0.0F, 0.0F);
				}
			}

			matrices.pop();
		}
    }

	private void renderShard(CeramicShard shard, Side side, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		SpriteIdentifier spriteIdentifier = this.spriteIds.get(shard.shardId());
		VertexConsumer spriteConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityTranslucentCull);

		if (side.straight) {
			this.straight.render(matrices, spriteConsumer, light, overlay);
		} else {
			this.corners[0].render(matrices, spriteConsumer, light, overlay);
			this.corners[1].render(matrices, spriteConsumer, light, overlay);
		}
	}

}
