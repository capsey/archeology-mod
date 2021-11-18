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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements BrushingPlayerEntity {

    private float breakingProgress = 0.0F;
	private int currentStage = 0;
    private BlockPos brushingPos;

    private static final float[] REGULAR_BREAK_DELTAS = { 0.02F, 0.03F, 0.04F, 0.05F, 0.06F };
    private static final float[] REGULAR_REPAIR_DELTAS = { -0.02F, -0.02F, -0.01F, -0.01F, -0.005F };

    private static float getBreakDelta(double change, ItemStack item, Difficulty difficulty, boolean mojang) {
        if (!mojang) {
            int i = CopperBrushItem.getOxidizationIndex(item) + (difficulty.getId() / 2);
            return (change > 0.000001D ? REGULAR_REPAIR_DELTAS : REGULAR_BREAK_DELTAS)[i];
        } else {
            return change > 0.000001D ? 0.05F : -0.002F;
        }
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

                    float breakDelta = getBreakDelta(change, player.getActiveItem(), client.world.getDifficulty(), config.mojangExcavationBreaking);
                    breakingProgress = Math.max(breakingProgress + breakDelta, 0);
                    int stage = (int) (breakingProgress * 10) - 1;

                    if (currentStage != stage) {
                        client.world.sendPacket(new ExcavationBreakingC2SPacket(stage));
                        currentStage = stage;
                    }

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
