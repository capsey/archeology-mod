package net.capsey.archeology.mixin.network;

import org.spongepowered.asm.mixin.Mixin;

import net.capsey.archeology.network.ServerPlayPacketListenerMixinInterface;
import net.minecraft.network.listener.ServerPlayPacketListener;

@Mixin(ServerPlayPacketListener.class)
public interface ServerPlayPacketListenerMixin extends ServerPlayPacketListenerMixinInterface {    
}
