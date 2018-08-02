package com.EvilNotch.silkspawners.client.proxy;

import com.EvilNotch.silkspawners.CommandMTHand;
import com.EvilNotch.silkspawners.client.ToolTipEvent;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends ServerProxy{
	
	@Override
	public void preinit()
	{
	}
	@Override
	public void init()
	{
		ClientCommandHandler.instance.registerCommand(new CommandMTHand());
		MinecraftForge.EVENT_BUS.register(new ToolTipEvent());
	}
	@Override
	public void postinit()
	{
	}

}
