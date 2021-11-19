package net.capsey.archeology.mixin.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlock;
import net.capsey.archeology.items.CopperBrushItem;
import net.capsey.archeology.items.CustomUseAction;
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

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    // @Inject(at = @At("HEAD"), cancellable = true, method = "getUsingItemHandRenderType(Lnet/minecraft/client/network/ClientPlayerEntity;)Lnet/minecraft/client/render/item/HeldItemRenderer/HandRenderType;")
    // private static void getUsingItemHandRenderType(ClientPlayerEntity player, CallbackInfoReturnable<HeldItemRenderer.HandRenderType> info) {
    //     ItemStack itemStack = player.getActiveItem();
    //     Hand hand = player.getActiveHand();

    //     if (itemStack.isOf(ArcheologyMod.Items.COPPER_BRUSH)) {
    //         info.setReturnValue(hand == Hand.MAIN_HAND ? HeldItemRenderer.HandRenderType.RENDER_MAIN_HAND_ONLY : HeldItemRenderer.HandRenderType.RENDER_OFF_HAND_ONLY);
    //     }
	// }

    @Inject(at = @At("HEAD"), cancellable = true, method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (!player.isUsingSpyglass() && !item.isEmpty() && item.isOf(ArcheologyMod.Items.COPPER_BRUSH)) {
            if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                if (item.getUseAction() == CustomUseAction.BRUSH) {
                    matrices.push();
                    Arm arm = (hand == Hand.MAIN_HAND) ? player.getMainArm() : player.getMainArm().getOpposite();
                    boolean bl = arm == Arm.RIGHT;

                    int side = bl ? 1 : -1;
                    float max = CopperBrushItem.getBrushTicks(item) * ExcavationBlock.MAX_BRUSHING_LEVELS;
                    float progress = (float) player.getItemUseTime() / max;
                    float angle_coef = MathHelper.sin(ExcavationBlock.MAX_BRUSHING_LEVELS * progress * 3.1415927F);

                    matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-60.0F));
                    matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-45.0F));

                    matrices.translate(-0.3D, 0.3D, -1.0D);
                    matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(side * 40.0F * angle_coef));
                    matrices.translate(-0.2D, 0.2D, 0.0D);

                    ((HeldItemRenderer)(Object) this).renderItem(player, item, ModelTransformation.Mode.FIXED, !bl, matrices, vertexConsumers, light);
                    matrices.pop();
                    info.cancel();
                }
            }
		}
    }

}
