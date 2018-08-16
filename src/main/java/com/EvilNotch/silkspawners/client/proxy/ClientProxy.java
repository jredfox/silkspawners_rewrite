package com.EvilNotch.silkspawners.client.proxy;

import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.silkspawners.Config;
import com.EvilNotch.silkspawners.ItemSpawner;
import com.EvilNotch.silkspawners.MainJava;
import com.EvilNotch.silkspawners.client.ToolTipEvent;
import com.EvilNotch.silkspawners.client.render.item.MobSpawnerItemRender;
import com.EvilNotch.silkspawners.client.render.tileentity.MobSpawnerRender;
import com.EvilNotch.silkspawners.client.render.tileentity.MobSpawnerStackBase;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import zdoctor.lazymodder.client.render.itemrender.IItemRendererHandler;

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
			EntityUtil.slimeSize = Config.slimeSize;
			EntityUtil.cacheEnts();//gets data of bad entities and what can/can't be used as well as information
		}
		ItemSpawner.registerCreativeTabs();
		super.postinit();
	}
	@Override
	public void serverClose() 
	{
		ToolTipEvent.rainbows.clear();
		super.serverClose();
	}
	
	public static void changeTexture(ResourceLocation loc) 
	{
		 Minecraft.getMinecraft().renderEngine.bindTexture(loc);
	}

}
