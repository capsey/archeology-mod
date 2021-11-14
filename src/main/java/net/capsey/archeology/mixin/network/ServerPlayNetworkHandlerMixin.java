package net.capsey.archeology.mixin.network;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;

import net.capsey.archeology.ArcheologyMod;
import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.capsey.archeology.network.ExcavationBreakingC2SPacket;
import net.capsey.archeology.network.ServerPlayPacketListenerMixinInterface;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin implements ServerPlayPacketListenerMixinInterface {

    @Override
    public void onExcavationBreakingStageChanged(ExcavationBreakingC2SPacket packet) {
        ServerPlayNetworkHandler self = (ServerPlayNetworkHandler)(Object) this;
        NetworkThreadUtils.forceMainThread(packet, self, self.getPlayer().getServerWorld());

        ServerPlayerEntity player = self.getPlayer();
        ServerWorld world = player.getServerWorld();
        BlockPos pos = packet.getBlockPos();
        
        Optional<ExcavationBlockEntity> entity = world.getBlockEntity(pos, ArcheologyMod.BlockEntities.EXCAVATION_BLOCK_ENTITY);
        
        if (entity.isPresent() && entity.get().isCorrectPlayer(player)) {
            int newStage = packet.getNewStage();

            if (newStage >= 0 && newStage < 9) {
                world.setBlockBreakingInfo(0, pos, newStage);
            } else {
                world.breakBlock(pos, false);
            }
        }
    }
    
}
