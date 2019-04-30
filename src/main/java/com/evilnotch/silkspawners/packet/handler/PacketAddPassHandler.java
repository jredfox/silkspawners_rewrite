package com.evilnotch.silkspawners.packet.handler;

import com.evilnotch.lib.minecraft.network.MessegeBase;
import com.evilnotch.silkspawners.Config;
import com.evilnotch.silkspawners.packet.PacketAddPass;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PacketAddPassHandler extends MessegeBase<PacketAddPass>{

	@Override
	public void handleClientSide(PacketAddPass msg, EntityPlayer arg1) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			Config.additionalPassengers = msg.additionalPassengers;
		});
	}

	@Override
	public void handleServerSide(PacketAddPass arg0, EntityPlayer arg1) 
	{
		
	}

}
