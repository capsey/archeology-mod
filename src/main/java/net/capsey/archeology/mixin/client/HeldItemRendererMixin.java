package net.capsey.archeology.mixin.client;

import net.capsey.archeology.ModConfig;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlock;
import net.capsey.archeology.items.CopperBrushItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    private static void applyBrushingTransformations(AbstractClientPlayerEntity player, float tickDelta, CopperBrushItem item, MatrixStack matrices) {
        // Aligning item to the center
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-60.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45.0F));
        matrices.translate(-0.3D, 0.3D, -1.0D);

        // Calculating periodic motion
        int max = item.getBrushTicks() * ExcavationBlock.MAX_BRUSHING_LEVELS;
        float progress = ((float) player.getItemUseTime() + tickDelta) / max;
        float angleCoefficient = MathHelper.sin(ExcavationBlock.MAX_BRUSHING_LEVELS * progress * MathHelper.PI);

        // Applying calculated angle along upward axis
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(40.0F * angleCoefficient));
        matrices.translate(-0.2D, 0.2D, 0.0D);
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (!ModConfig.disableBrushingAnimation && player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
            if (item.getItem() instanceof CopperBrushItem brushItem) {
                matrices.push();

                applyBrushingTransformations(player, tickDelta, brushItem, matrices);
                ((HeldItemRenderer) (Object) this).renderItem(player, item, ModelTransformation.Mode.FIXED, false, matrices, vertexConsumers, light);

                matrices.pop();
                info.cancel();
            }
        }
    }

}
