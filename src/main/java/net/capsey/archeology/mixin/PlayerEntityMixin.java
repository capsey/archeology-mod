package net.capsey.archeology.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.capsey.archeology.PlayerEntityMixinInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityMixinInterface {

    private int lastBrushedTicks;

    @Inject(method = "tick()V", at = @At("TAIL"))
    public void tick(CallbackInfo info) {
        if (lastBrushedTicks < 10) {
            lastBrushedTicks++;
        }
    }

    @Override
    public void resetLastBrushedTicks() {
        lastBrushedTicks = 0;
    }

    @Override
    public float getBrushCooldownProgress() {
		return MathHelper.clamp((float) this.lastBrushedTicks / 10.0F, 0.0F, 1.0F);
	}

}
