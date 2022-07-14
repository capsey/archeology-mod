package net.capsey.archeology.mixin.render;

import net.capsey.archeology.ModConfig;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlock;
import net.capsey.archeology.items.ChiselItem;
import net.capsey.archeology.items.CopperBrushItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Inject(at = @At("HEAD"), cancellable = true, method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (!ModConfig.disableFirstPersonItemAnimations && player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
            if (item.getItem() instanceof CopperBrushItem) {
                matrices.push();

                applyBrushingTransformations(player, tickDelta, item, matrices);
                ((HeldItemRenderer) (Object) this).renderItem(player, item, ModelTransformation.Mode.FIXED, false, matrices, vertexConsumers, light);

                matrices.pop();
                info.cancel();
            } else if (item.getItem() instanceof ChiselItem) {
                matrices.push();

                applyChiselingTransformations(player, tickDelta, item, matrices);
                ((HeldItemRenderer) (Object) this).renderItem(player, item, ModelTransformation.Mode.FIXED, false, matrices, vertexConsumers, light);

                matrices.pop();
                info.cancel();
            }
        }
    }

    private static void applyBrushingTransformations(AbstractClientPlayerEntity player, float tickDelta, ItemStack item, MatrixStack matrices) {
        // Aligning item to the center
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-60.0F));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-45.0F));
        matrices.translate(-0.3D, 0.3D, -1.0D);

        // Calculating periodic motion
        int max = CopperBrushItem.getBrushTicks(item) * ExcavationBlock.MAX_BRUSHING_LEVELS;
        float progress = ((float) player.getItemUseTime() + tickDelta) / max;
        float angleCoef = MathHelper.sin(ExcavationBlock.MAX_BRUSHING_LEVELS * progress * MathHelper.PI);

        // Applying calculated angle along upward axis
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(40.0F * angleCoef));
        matrices.translate(-0.2D, 0.2D, 0.0D);
    }

    private static void applyChiselingTransformations(AbstractClientPlayerEntity player, float tickDelta, ItemStack item, MatrixStack matrices) {
        // Aligning item to the center
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-60.0F));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-45.0F));
        matrices.translate(-0.5D, 0.5D, -1.0D);

        // Calculating periodic motion
        float progress = player.getItemUseTime() + tickDelta;
        float angle = progress * MathHelper.PI / ChiselItem.HIT_PERIOD;
        float offset = (2.0F - MathHelper.abs(MathHelper.sin(angle)) * 2) * 0.2F;

        // Damp at the beginning of the animation
        offset *= smoothstep(progress / (float) ChiselItem.HIT_PERIOD, 0, 1);

        // Applying calculated offset along forward axis
        matrices.translate(-offset, offset, 0.0D);
    }

    private static float smoothstep(float x, float min, float max) {
        // Scale, and clamp x to [0..1] range
        x = MathHelper.clamp((x - min) / (max - min), 0.0F, 1.0F);
        // Evaluate polynomial
        return x * x * x * (x * (x * 6 - 15) + 10);
    }

}
