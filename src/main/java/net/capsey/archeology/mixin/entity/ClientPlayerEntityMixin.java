package net.capsey.archeology.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.capsey.archeology.entity.BrushingPlayerEntity;
import net.capsey.archeology.network.ExcavationBreakingC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements BrushingPlayerEntity {

    private float breakingProgress = 0.0F;
	private int currentStage = 0;
    private BlockPos brushingPos;

    @Inject(method = "tick()V", at = @At("TAIL"))
    public void tick(CallbackInfo info) {
        if (brushingPos != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            
            if (client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                BlockHitResult raycast = (BlockHitResult) client.crosshairTarget;

                if (raycast.getBlockPos().equals(brushingPos)) {
                    breakingProgress += 0.02F;
                    int stage = (int) (breakingProgress * 10);
        
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
        this.brushingPos = null;
        this.breakingProgress = 0.0F;
        this.currentStage = -1;
    }
    
}
