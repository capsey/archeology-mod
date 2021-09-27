package net.capsey.archeology.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.ExcavationBlock;
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

    @Inject(at = @At("HEAD"), cancellable = true, method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (!player.isUsingSpyglass() && !item.isEmpty() && item.isOf(ArcheologyMod.COPPER_BRUSH)) {
            if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                if (item.getUseAction() == CustomUseAction.BRUSH) {
                    matrices.push();
                    Arm arm = (hand == Hand.MAIN_HAND) ? player.getMainArm() : player.getMainArm().getOpposite();
                    boolean bl = arm == Arm.RIGHT;

                    int side = bl ? 1 : -1;
                    float progress = (float) player.getItemUseTime() / item.getMaxUseTime();
                    float angle_coef = MathHelper.cos(3 * ExcavationBlock.MAX_BRUSHING_LEVELS * progress * 6.2831855F);

                    applyEquipOffsetMixin(matrices, arm, equipProgress);
                    applySwingOffsetMixin(matrices, arm, swingProgress);

                    matrices.translate(side * -0.3D, 0.0D, 0.0D);
                    matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(side * 90.0F));
                    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(side * 5.0F));
                    matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-110.0F));
                    matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(side * -20.0F));

                    matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(side * 40.0F * angle_coef));

                    ((HeldItemRenderer)(Object) this).renderItem(player, item, bl ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !bl, matrices, vertexConsumers, light);
                    matrices.pop();
                    info.cancel();
                }
            }
		}
    }

    @Invoker("applyEquipOffset")
    protected abstract void applyEquipOffsetMixin(MatrixStack matrices, Arm arm, float equipProgress);

    @Invoker("applySwingOffset")
    protected abstract void applySwingOffsetMixin(MatrixStack matrices, Arm arm, float equipProgress);

}
