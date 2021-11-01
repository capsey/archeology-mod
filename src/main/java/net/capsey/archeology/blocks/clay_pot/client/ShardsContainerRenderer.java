package net.capsey.archeology.blocks.clay_pot.client;

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
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public abstract class ShardsContainerRenderer<T extends ShardsContainer> implements BlockEntityRenderer<T> {

	private final Class<T> entityClass;

    private final ModelPart straight;
	private final ModelPart corner;

	public ShardsContainerRenderer(BlockEntityRendererFactory.Context ctx, Class<T> entityClass) {
		this.entityClass = entityClass;

		ModelPart modelPart = ctx.getLayerModelPart(ArcheologyClientMod.getModelLayer(this.entityClass));
		this.straight = modelPart.getChild("straight");
		this.corner = modelPart.getChild("corner");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartBuilder straightBuilder = ModelPartBuilder.create().uv(8, 0).cuboid(0.0F, 0.0F, 0.0F, 16.0F, 10.0F, 16.0F);
		modelPartData.addChild("straight", straightBuilder, ModelTransform.NONE);

		ModelPartBuilder cornerBuilder = ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 16.0F, 10.0F, 16.0F);
		modelPartData.addChild("corner", cornerBuilder, ModelTransform.NONE);

		return TexturedModelData.of(modelData, 72, 26);
	}

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.hasShards()) {
			matrices.push();

			// Z-fighting fix
			matrices.scale(1.002F, 1.0F, 1.002F);
			matrices.translate(-0.001F, 0.0F, -0.001F);
			
			// Upside down fix
			matrices.scale(-1.0F, -1.0F, 1.0F);
			matrices.translate(-1.0F, -(10.0F / 16), 0.0F);
			
			for (Side side : Side.values()) {
				CeramicShard shard = entity.getShard(side);

				if (shard != null) {
					renderShard(shard, side, matrices, vertexConsumers, light, overlay);
				}

				if (side.isStraight()) {
					matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
					matrices.translate(-1.0F, 0.0F, 0.0F);
				}
			}

			matrices.pop();
		}
    }

	private void renderShard(CeramicShard shard, Side side, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		SpriteIdentifier spriteIdentifier = shard.getSpriteId(entityClass);
		VertexConsumer spriteConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityNoOutline);

		(side.isStraight() ? straight : corner).render(matrices, spriteConsumer, light, overlay);
	}

}
