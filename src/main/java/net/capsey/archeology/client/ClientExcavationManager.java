package net.capsey.archeology.client;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.capsey.archeology.network.ExcavationBreakingC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class ClientExcavationManager {
    
    private ClientWorld world;
    private ExcavationBlockEntity entity;
    private BlockPos pos;
    private int stage = 0;

    public void setWorld(@Nullable ClientWorld world) {
        this.world = world;
        reset();
    }

    public void startBrushing(ExcavationBlockEntity entity) {
        if (world != null && entity != null) {
            reset();
            this.entity = entity;
            this.pos = entity.getPos();
        }
    }

    public void excavationTick(LivingEntity user, BlockHitResult result, int usageTick) {
        if (world != null) {
            Optional<ExcavationBlockEntity> blockEntity = world.getBlockEntity(result.getBlockPos(), ArcheologyMod.BlockEntities.EXCAVATION_BLOCK_ENTITY);
    
            if (blockEntity.isPresent() && blockEntity.get().equals(entity)) {
                if (usageTick % 10 == 0) {
                    if (stage++ < 9) {
                        world.setBlockBreakingInfo(0, pos, stage);
                    } else {
                        user.stopUsingItem();
                    }
                }
            } else {
                user.stopUsingItem();
            }
        }
    }
    
    public void stopBrushing() {
        if (world != null && pos != null) {
            world.breakBlock(pos, true);
            world.sendPacket(new ExcavationBreakingC2SPacket(pos));
            reset();
        }
    }

    public void reset() {
        stage = 0;
        if (world != null && pos != null) {
            world.setBlockBreakingInfo(0, pos, -1);
            pos = null;
        }
    }

}
