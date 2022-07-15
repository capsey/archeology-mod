package net.capsey.archeology.mixin.entity;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.ModConfig;
import net.capsey.archeology.entity.BrushingPlayerEntity;
import net.capsey.archeology.items.CopperBrushItem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements BrushingPlayerEntity {

    private static final float[] REGULAR_BREAK_DELTAS = { 0.3F, 0.3F, 0.4F, 0.5F };
    private static final float[] REGULAR_REPAIR_DELTAS = { -0.15F, -0.15F, -0.1F, -0.07F };
    private static final double[] BREAK_THRESHOLD = { 5.0E-6, 1.0E-5, 2.0E-5, 5.0E-5 };
    private float breakingProgress = 0.0F;
    private int currentStage = 0;
    private BlockPos brushingPos;

    private static float getBreakDelta(double change, ClientPlayerEntity player) {
        // Do not break if creative
        if (player.isCreative()) {
            return 0;
        }

        float value;
        boolean moved;

        if (!ModConfig.mojangExcavationBreaking) {
            // Break/restore value depends on oxidization level
            int i = CopperBrushItem.getOxidizationIndex(player.getActiveItem());
            moved = change > (BREAK_THRESHOLD[i] * ModConfig.thresholdCoef);
            value = (moved ? REGULAR_REPAIR_DELTAS : REGULAR_BREAK_DELTAS)[i];
        } else {
            // With Mojang style it does not
            moved = change > 0;
            value = moved ? 0.7F : -0.04F;
        }

        return value * ModConfig.getBreakDeltaCoef(moved);
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    public void tick(CallbackInfo info) {
        if (brushingPos != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

            if (client.crosshairTarget instanceof BlockHitResult raycast) {
                BlockPos pos = raycast.getBlockPos();

                if (pos.equals(brushingPos)) {
                    // Calculating break delta (how much block breaks/restores)
                    Vec3d lookDir = Vec3d.fromPolar(player.getPitch(), player.getHeadYaw());
                    Vec3d prevLookDir = Vec3d.fromPolar(player.prevPitch, player.prevHeadYaw);
                    double change = prevLookDir.squaredDistanceTo(lookDir);

                    breakingProgress += getBreakDelta(change, player);

                    // Sending break packet
                    if (breakingProgress >= 1.0F) {
                        this.sendInfoPacket(currentStage + 1);
                    } else if (breakingProgress <= -1.0F) {
                        this.sendInfoPacket(currentStage - 1);
                    }

                    // Adding brushing particles
                    client.particleManager.addBlockBreakingParticles(pos, Direction.UP);
                    client.particleManager.addBlockBreakingParticles(pos, Direction.UP);
                    client.particleManager.addBlockBreakingParticles(pos, Direction.UP);
                    return;
                }
            }

            this.sendBreakPacket();
        }
    }

    private void sendInfoPacket(int stage) {
        stage = Math.max(stage, 0);

        if (stage > 9) {
            this.sendBreakPacket();
        } else if (stage != this.currentStage) {
            // Sending packet to the server
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(stage);
            ClientPlayNetworking.send(ArcheologyMod.EXCAVATION_BREAKING_INFO, buf);

            // Updating values
            this.currentStage = stage;
            this.breakingProgress = 0.0F;
        }
    }

    private void sendBreakPacket() {
        ClientPlayNetworking.send(ArcheologyMod.EXCAVATION_STOP_BRUSHING, PacketByteBufs.empty());
        this.reset();
    }

    @Override
    public void startBrushing(BlockPos pos) {
        this.reset();
        this.brushingPos = pos;
    }

    private void reset() {
        this.breakingProgress = 0.0F;
        this.currentStage = -1;
        this.brushingPos = null;
    }

}
