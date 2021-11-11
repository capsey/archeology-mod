package net.capsey.archeology.network;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.math.BlockPos;

public class ExcavationBreakingC2SPacket implements Packet<ServerPlayPacketListener> {

    private final BlockPos pos;

	public ExcavationBreakingC2SPacket(BlockPos pos) {
		this.pos = pos;
	}

	public ExcavationBreakingC2SPacket(PacketByteBuf buf) {
		this.pos = buf.readBlockPos();
	}

	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(pos);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		((ServerPlayPacketListenerMixinInterface) serverPlayPacketListener).onExcavationFailed(this);
	}

    public BlockPos getBlockPos() {
        return pos;
    }
    
}