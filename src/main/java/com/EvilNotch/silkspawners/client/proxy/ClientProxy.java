package com.EvilNotch.silkspawners.client.proxy;

import com.EvilNotch.silkspawners.client.ToolTipEvent;

import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends ServerProxy{
	
	@Override
	public void preinit()
	{
	}
	@Override
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(new ToolTipEvent());
	}
	@Override
	public void postinit()
	{
	}

}
