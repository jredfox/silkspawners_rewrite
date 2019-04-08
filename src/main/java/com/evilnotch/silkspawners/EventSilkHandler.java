package com.evilnotch.silkspawners;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventSilkHandler {
	
	@SubscribeEvent
	public void a(EntityJoinWorldEvent event)
	{
//		System.out.println(event.getEntity().getName());
//		event.setCanceled( !(event.getEntity() instanceof EntityPlayer));
	}

}
