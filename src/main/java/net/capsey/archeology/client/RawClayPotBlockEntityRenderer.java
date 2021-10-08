package net.capsey.archeology.client;

import net.capsey.archeology.blocks.clay_pot.RawClayPotBlockEntity;
import net.capsey.archeology.blocks.clay_pot.ShardsContainer.Side;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class RawClayPotBlockEntityRenderer implements BlockEntityRenderer<RawClayPotBlockEntity> {

	public static final Identifier ATLAS_TEXTURE = new Identifier("textures/atlas/shards.png");
	public static final EntityModelLayer SHARDS_MODEL_LAYER = new EntityModelLayer(new Identifier("archeology", "raw_clay_pot_block_entity"), "shards");

    private final ModelPart base;

	public RawClayPotBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		ModelPart modelPart = ctx.getLayerModelPart(SHARDS_MODEL_LAYER);
		this.base = modelPart.getChild("base");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartBuilder builder = ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 16.0F, 10.0F, 16.0F);
		modelPartData.addChild("base", builder, ModelTransform.NONE);

		return TexturedModelData.of(modelData, 64, 26);
	}

    @Override
    public void render(RawClayPotBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!entity.isEmpty()) {
			matrices.push();

			// Z-fighting fix
			matrices.scale(1.02F, 1.0F, 1.02F);
			matrices.translate(-0.01F, 0.0F, -0.01F);
			
			// Upside down fix
			matrices.scale(-1.0F, -1.0F, 1.0F);
			matrices.translate(-1.0F, -(10.0F / 16), 0.0F);
			
			for (Side side : Side.straightValues()) {
				ItemStack shard = entity.getShard(side);

				if (shard != null) {
					renderShard(shard, side, matrices, vertexConsumers, light, overlay);
				}

				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
				matrices.translate(-1.0F, 0.0F, 0.0F);
			}

			matrices.pop();
		}
    }

	private void renderShard(ItemStack shard, Side side, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		switch (side) {
			case North:
			case South:
			case West:
			case East:
				renderStraightShard(shard, matrices, vertexConsumers, light, overlay);
				break;
		
			default:
				break;
		}
	}

	private void renderStraightShard(ItemStack shard, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		SpriteIdentifier spriteIdentifier = new SpriteIdentifier(ATLAS_TEXTURE, new Identifier("archeology", "shard/ender_dragon"));
		base.render(matrices, spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityNoOutline), light, overlay);
	}
    
}
