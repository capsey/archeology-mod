package net.capsey.archeology.mixin.network;

import org.spongepowered.asm.mixin.Mixin;

import net.capsey.archeology.blocks.excavation_block.ExcavationBlockEntity;
import net.capsey.archeology.entity.ExcavatorPlayerEntity;
import net.capsey.archeology.network.ExcavationBreakingC2SPacket;
import net.capsey.archeology.network.ServerPlayPacketListenerMixinInterface;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin implements ServerPlayPacketListenerMixinInterface {

    @Override
    public void onExcavationBreakingStageChanged(ExcavationBreakingC2SPacket packet) {
        ServerPlayNetworkHandler self = (ServerPlayNetworkHandler)(Object) this;
        NetworkThreadUtils.forceMainThread(packet, self, self.getPlayer().getWorld());

        ServerPlayerEntity player = self.getPlayer();
        ExcavationBlockEntity entity = ((ExcavatorPlayerEntity) player).getExcavatingBlock();

        if (entity != null){
            ServerWorld world = (ServerWorld) entity.getWorld();
            
            if (!entity.isRemoved() && entity.isCorrectPlayer(player)) {
                int newStage = packet.getNewStage();
    
                if (newStage < 9) {
                    world.setBlockBreakingInfo(0, entity.getPos(), newStage);
                } else {
                    // world.breakBlock(entity.getPos(), false);
                    player.stopUsingItem();
                }
            }
        }
    }
    
}
