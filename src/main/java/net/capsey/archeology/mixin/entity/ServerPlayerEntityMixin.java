package net.capsey.archeology.mixin.entity;

import java.lang.ref.WeakReference;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.capsey.archeology.entity.ExcavatorPlayerEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements ExcavatorPlayerEntity {

    private WeakReference<ExcavationBlockEntity> brushingBlockReference;

    @Override
    public void startBrushing(ExcavationBlockEntity entity) {
        brushingBlockReference = new WeakReference<>(entity);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(entity.getPos());
        ServerPlayNetworking.send((ServerPlayerEntity)(Object) this, ArcheologyMod.START_BRUSHING, buf);
    }

    @Override @Nullable
    public ExcavationBlockEntity getExcavatingBlock() {
        return brushingBlockReference.get();
    }

}
