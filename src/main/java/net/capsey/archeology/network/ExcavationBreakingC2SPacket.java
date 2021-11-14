package net.capsey.archeology.network;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class ExcavationBreakingC2SPacket implements Packet<ServerPlayPacketListener> {

    private final BlockPos pos;
	private final int newStage;

	public ExcavationBreakingC2SPacket(BlockPos pos, int stage) {
		this.pos = pos;
		this.newStage = stage;
	}

	public ExcavationBreakingC2SPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
		this.newStage = buf.readInt();
	}

	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(newStage);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		((ServerPlayPacketListenerMixinInterface) serverPlayPacketListener).onExcavationBreakingStageChanged(this);
	}

    public BlockPos getBlockPos() {
        return pos;
    }

	public int getNewStage() {
		return newStage;
	}
    
}