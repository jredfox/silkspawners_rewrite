package com.EvilNotch.silkspawners.client.proxy;

import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.silkspawners.CommandMTHand;
import com.EvilNotch.silkspawners.MainJava;
import com.EvilNotch.silkspawners.client.ToolTipEvent;
import com.EvilNotch.silkspawners.client.render.item.MobSpawnerItemRender;
import com.EvilNotch.silkspawners.client.render.tileentity.MobSpawnerStackBase;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import zdoctor.lazymodder.client.render.itemrender.IItemRendererHandler;

public class ClientProxy extends ServerProxy{
	
	@Override
	public void preinit()
	{
		
	}
	@Override
	public void init()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMobSpawner.class, new MobSpawnerStackBase());
	    IItemRendererHandler.registerIItemRenderer(MainJava.mob_spawner, new MobSpawnerItemRender());
	    
		ClientCommandHandler.instance.registerCommand(new CommandMTHand());
		MinecraftForge.EVENT_BUS.register(new ToolTipEvent());
	}
	@Override
	public void postinit()
	{
		EntityUtil.cacheEnts();
	}
	public static void changeTexture(ResourceLocation loc) 
	{
		 Minecraft.getMinecraft().renderEngine.bindTexture(loc);
	}

}
