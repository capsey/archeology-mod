package net.capsey.archeology.mixin.entity;

import java.lang.ref.WeakReference;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.capsey.archeology.entity.ExcavatorPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements ExcavatorPlayerEntity {

    private int lastBrushedTicks;
    private WeakReference<ExcavationBlockEntity> brushingBlockReference;

    @Inject(method = "tick()V", at = @At("TAIL"))
    public void tick(CallbackInfo info) {
        if (lastBrushedTicks < 10) {
            lastBrushedTicks++;
        }
    }

    @Override
    public void startBrushing(ExcavationBlockEntity entity) {
        brushingBlockReference = new WeakReference<>(entity);
    }

    @Override
    public @Nullable ExcavationBlockEntity getExcavatingBlock() {
        return brushingBlockReference.get();
    }

    @Override
    public void resetLastBrushedTicks() {
        lastBrushedTicks = 0;
    }

    @Override
    public float getBrushCooldownProgress() {
		return MathHelper.clamp(this.lastBrushedTicks / 10.0F, 0.0F, 1.0F);
	}

}
