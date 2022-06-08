package net.capsey.archeology.mixin.render;

import net.capsey.archeology.Blocks;
import net.capsey.archeology.blocks.clay_pot.ClayPotBlockEntity;
import net.capsey.archeology.entity.FallingBlockEntityMixinInterface;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.FallingBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntityRenderer.class)
public abstract class FallingBlockEntityRendererMixin extends EntityRenderer<FallingBlockEntity> {

    private final ClayPotBlockEntity renderClayPot = new ClayPotBlockEntity(BlockPos.ORIGIN, Blocks.CLAY_POT.getDefaultState());

    protected FallingBlockEntityRendererMixin(Context ctx) {
        super(ctx);
    }

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/entity/FallingBlockEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    public void render(FallingBlockEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        BlockState blockState = entity.getBlockState();

        if (blockState.isOf(Blocks.CLAY_POT)) {
            World world = entity.getEntityWorld();

            if (blockState != world.getBlockState(entity.getBlockPos())) {
                matrices.push();
                matrices.translate(-0.5D, 0.0D, -0.5D);

                renderClayPot.readShards(((FallingBlockEntityMixinInterface) entity).getClientBlockEntityData());
                MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(renderClayPot, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);

                matrices.pop();
                super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
            }
        }
    }

}
