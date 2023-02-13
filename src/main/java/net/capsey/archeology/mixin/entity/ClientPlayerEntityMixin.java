package net.capsey.archeology.mixin.entity;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.ModConfig;
import net.capsey.archeology.main.BlockEntities;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.capsey.archeology.entity.BrushingPlayerEntity;
import net.capsey.archeology.items.CopperBrushItem;
import net.capsey.archeology.mixin.client.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements BrushingPlayerEntity, BrushingPlayerEntity.Client {

    private WeakReference<ExcavationBlockEntity> brushingEntity = new WeakReference<>(null);
    @Nullable private BlockPos breakingPos;
    private float breakingProgress;
    private float facingDelta;

    @Override
    public void startBrushing(BlockPos pos) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ExcavationBlockEntity entity = player.world.getBlockEntity(pos, BlockEntities.EXCAVATION_BLOCK_ENTITY).orElse(null);
        brushingEntity = new WeakReference<>(entity);
        breakingPos = entity != null ? entity.getPos() : null;
        breakingProgress = 0;
    }

    @Override
    public @Nullable ExcavationBlockEntity getBrushingEntity() {
        ExcavationBlockEntity entity = brushingEntity.get();
        return entity == null || entity.isRemoved() ? null : entity;
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    public void tick(CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        Vec3d lookDir = Vec3d.fromPolar(player.getPitch(), player.getHeadYaw());
        Vec3d prevLookDir = Vec3d.fromPolar(player.prevPitch, player.prevHeadYaw);
        facingDelta = (float) prevLookDir.distanceTo(lookDir);
    }

    @Inject(method = "clearActiveItem()V", at = @At("HEAD"))
    private void clearActiveItem(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (breakingPos != null && player.getActiveItem().getItem() instanceof CopperBrushItem) {
            // Fixing persistent breaking overlay
            player.world.setBlockBreakingInfo(player.getId(), breakingPos, -1);
            breakingPos = null;

            // Prevent accidental breaking
            if (ModConfig.releaseUseKeyAfterBrushing) {
                client.options.useKey.setPressed(false);
            } else {
                ((MinecraftClientAccessor) client).setItemUseCooldown(16);
            }
        }
    }

    @Override
    public boolean tick() {
        float delta = ModConfig.brushingLowerThreshold - facingDelta;
        delta *= delta < 0 ? ModConfig.brushingRepairSpeed : ModConfig.brushingBreakingSpeed;

        breakingProgress = Math.max(breakingProgress + delta, 0);
        int i = Math.round(breakingProgress * 10) - 2;

        if (breakingProgress > 1.0F || breakingPos == null) {
            return false;
        }

        PlayerEntity player = (PlayerEntity) (Object) this;
        player.world.setBlockBreakingInfo(player.getId(), breakingPos, i);
        return true;
    }
}
