package net.capsey.archeology.mixin.entity;

import java.lang.ref.WeakReference;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.capsey.archeology.entity.ExcavatorPlayerEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements ExcavatorPlayerEntity {

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

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(entity.getPos());
        ServerPlayNetworking.send((ServerPlayerEntity)(Object) this, ArcheologyMod.START_BRUSHING, buf);
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
