package net.capsey.archeology.network;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class ExcavationBreakingC2SPacket implements Packet<ServerPlayPacketListener> {

	private final int newStage;

	public ExcavationBreakingC2SPacket(int stage) {
		this.newStage = stage;
	}

	public ExcavationBreakingC2SPacket(PacketByteBuf buf) {
		this.newStage = buf.readInt();
	}

	public void write(PacketByteBuf buf) {
		buf.writeInt(newStage);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		((ServerPlayPacketListenerMixinInterface) serverPlayPacketListener).onExcavationBreakingStageChanged(this);
	}

	public int getNewStage() {
		return newStage;
	}
    
}