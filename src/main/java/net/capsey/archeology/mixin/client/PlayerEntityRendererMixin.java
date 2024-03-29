package net.capsey.archeology.mixin.client;

import net.capsey.archeology.items.CopperBrushItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(at = @At("HEAD"), cancellable = true, method = "getArmPose(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/client/render/entity/model/BipedEntityModel$ArmPose;")
    private static void getArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> info) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (!itemStack.isEmpty() && itemStack.getItem() instanceof CopperBrushItem) {
            if (player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                info.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
            }
        }
    }

}
