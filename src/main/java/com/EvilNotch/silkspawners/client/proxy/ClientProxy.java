package com.evilnotch.silkspawners.client.proxy;

import com.evilnotch.silkspawners.client.ToolTipEvent;
import com.evilnotch.silkspawners.client.render.item.MobSpawnerItemRender;
import com.evilnotch.silkspawners.client.render.tileentity.MobSpawnerStackBase;
import com.elix_x.itemrender.IItemRendererHandler;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.silkspawners.Config;
import com.evilnotch.silkspawners.ItemSpawner;
import com.evilnotch.silkspawners.MainJava;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends ServerProxy{
	
	@Override
	public void preinit()
	{
		super.preinit();
	}
	@Override
	public void init()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMobSpawner.class, new MobSpawnerStackBase());
		if(Config.mobItemRender)
			IItemRendererHandler.registerIItemRenderer(MainJava.mob_spawner, new MobSpawnerItemRender());
		MinecraftForge.EVENT_BUS.register(new ToolTipEvent());
		super.init();
	}
	@Override
	public void postinit()
	{
		if(Config.mobItemRender || Config.creativeTabSpawners)
		{
			EntityUtil.cacheEnts();//gets data of bad entities and what can/can't be used as well as information
		}
		ItemSpawner.registerCreativeTabs();
		super.postinit();
	}
	@Override
	public void onLoadComplete()
	{
		super.onLoadComplete();
	}
	@Override
	public void clientClose() 
	{
		ToolTipEvent.rainbows.clear();
	}
	
	public static void changeTexture(ResourceLocation loc) 
	{
		 Minecraft.getMinecraft().renderEngine.bindTexture(loc);
	}

}
