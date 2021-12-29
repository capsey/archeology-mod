package net.capsey.archeology.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shedaniel.autoconfig.AutoConfig;
import net.capsey.archeology.ModConfig;
import net.capsey.archeology.entity.BrushingPlayerEntity;
import net.capsey.archeology.items.CopperBrushItem;
import net.capsey.archeology.network.ExcavationBreakingC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements BrushingPlayerEntity {

    private float breakingProgress = 0.0F;
	private int currentStage = 0;
    private BlockPos brushingPos;

    private static final float[] REGULAR_BREAK_DELTAS = { 0.3F, 0.3F, 0.4F, 0.5F };
    private static final float[] REGULAR_REPAIR_DELTAS = { -0.15F, -0.15F, -0.1F, -0.07F };

    private static final double[] BREAK_THRESHOLD = { 5.0E-6, 1.0E-5, 2.0E-5, 5.0E-5 };

    private static float getBreakDelta(double change, ItemStack item, boolean mojang) {
        ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        float value;
        boolean moved;

        if (!mojang) {
            int i = CopperBrushItem.getOxidizationIndex(item);
            moved = change > (BREAK_THRESHOLD[i] * config.getThresholdCoef());
            value = (moved ? REGULAR_REPAIR_DELTAS : REGULAR_BREAK_DELTAS)[i];
        } else {
            moved = change > 0;
            value = moved ? 0.7F : -0.04F;
        }

        return value * config.getBreakDeltaCoef(moved);
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    public void tick(CallbackInfo info) {
        if (brushingPos != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            
            if (client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                BlockHitResult raycast = (BlockHitResult) client.crosshairTarget;
                
                if (raycast.getBlockPos().equals(brushingPos)) {
                    ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
                    ClientPlayerEntity player = (ClientPlayerEntity)(Object) this;

                    Vec3d lookDir = Vec3d.fromPolar(player.getPitch(), player.getHeadYaw());
                    Vec3d prevLookDir = Vec3d.fromPolar(player.prevPitch, player.prevHeadYaw);
                    double change = prevLookDir.squaredDistanceTo(lookDir);

                    breakingProgress += getBreakDelta(change, player.getActiveItem(), config.brushing.mojangExcavationBreaking);

                    if (breakingProgress >= 1.0F) {
                        client.world.sendPacket(new ExcavationBreakingC2SPacket(++currentStage));
                        breakingProgress = 0.0F;
                    } else if (breakingProgress <= -1.0F) {
                        client.world.sendPacket(new ExcavationBreakingC2SPacket(--currentStage));
                        breakingProgress = 0.0F;
                    }

                    client.particleManager.addBlockBreakingParticles(raycast.getBlockPos(), Direction.UP);
                    return;
                }
            }

            client.world.sendPacket(new ExcavationBreakingC2SPacket(10));
            this.reset();
        }
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
