package net.capsey.archeology.mixin.entity;

import net.capsey.archeology.BlockEntities;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.capsey.archeology.entity.BrushingPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements BrushingPlayerEntity, BrushingPlayerEntity.Server {

    private WeakReference<ExcavationBlockEntity> brushingEntity;

    @Override
    public void startBrushing(BlockPos pos) {
        World world = ((Entity)(Object) this).world;
        ExcavationBlockEntity entity = world.getBlockEntity(pos, BlockEntities.EXCAVATION_BLOCK_ENTITY).orElse(null);
        brushingEntity = new WeakReference<>(entity);

        if (entity != null) {
            PlayerEntity player = (PlayerEntity)(Object) this;
            entity.generateLoot(player, player.getActiveItem());
        }
    }

    @Inject(method = "onDisconnect()V", at = @At("TAIL"))
    public void onDisconnect(CallbackInfo ctx) {
        onStopBrushing();
    }

    @Override
    public @Nullable ExcavationBlockEntity getBrushingEntity() {
        ExcavationBlockEntity entity = brushingEntity.get();
        return entity == null || entity.isRemoved() ? null : entity;
    }

    @Override
    public void onStopBrushing() {
        ExcavationBlockEntity entity = brushingEntity.get();

        if (entity != null && !entity.isRemoved()) {
            World world = ((Entity)(Object) this).world;
            world.breakBlock(entity.getPos(), true, (PlayerEntity)(Object) this);
        }
    }
}
