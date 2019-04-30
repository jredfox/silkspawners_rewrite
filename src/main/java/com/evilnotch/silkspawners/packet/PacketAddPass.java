package com.evilnotch.silkspawners.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketAddPass implements IMessage{
	
	public boolean additionalPassengers;

	public PacketAddPass()
	{
		
	}
	
	public PacketAddPass(boolean additionalPassengers)
	{
		this.additionalPassengers = additionalPassengers;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.additionalPassengers = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(this.additionalPassengers);
	}

}
