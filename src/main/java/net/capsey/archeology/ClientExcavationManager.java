package net.capsey.archeology;

import java.util.Optional;

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
    
    private final ClientWorld world;
    private ExcavationBlockEntity entity;
    private BlockPos pos;
    private int stage = 0;

    public ClientExcavationManager(ClientWorld world) {
        this.world = world;
    }

    public void startBrushing(ExcavationBlockEntity entity) {
        if (entity != null) {
            stage = 0;
            this.entity = entity;
            this.pos = entity.getPos();
        }
    }

    public void excavationTick(LivingEntity user, BlockHitResult result, int usageTick) {
        Optional<ExcavationBlockEntity> blockEntity = world.getBlockEntity(result.getBlockPos(), ArcheologyMod.BlockEntities.EXCAVATION_BLOCK_ENTITY);

        if (blockEntity.isPresent() && blockEntity.get().equals(entity)) {
            if (usageTick % 10 == 0) {
                if (stage++ < 10) {
                    world.setBlockBreakingInfo(0, pos, stage);
                } else {
                    user.stopUsingItem();
                }
            }
        } else {
            user.stopUsingItem();
        }
    }
    
    public void stopBrushing() {
        if (pos != null) {
            world.setBlockBreakingInfo(0, pos, -1);
            world.breakBlock(pos, true);
            world.sendPacket(new ExcavationBreakingC2SPacket(pos));
        }
    }

}
