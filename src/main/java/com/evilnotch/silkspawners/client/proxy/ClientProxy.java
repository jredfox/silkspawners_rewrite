package com.evilnotch.silkspawners.client.proxy;

import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.evilnotch.lib.api.mcp.MCPSidedString;
import com.evilnotch.silkspawners.Config;
import com.evilnotch.silkspawners.MainJava;
import com.evilnotch.silkspawners.client.ToolTipEvent;
import com.evilnotch.silkspawners.client.render.item.MobSpawnerItemRender;
import com.evilnotch.silkspawners.client.render.tileentity.MobSpawnerStackBase;
import com.evilnotch.silkspawners.client.render.util.MobSpawnerCache;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends ServerProxy{
	public static String isDrawing;
	
	@Override
	public void preinit()
	{
		super.preinit();
		isDrawing = new MCPSidedString("isDrawing","field_179010_r").toString();
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
	public void onLoadComplete()
	{
		super.onLoadComplete();
	}
	
	@Override
	public void clear() 
	{
		ToolTipEvent.rainbows.clear();
		System.out.print("spawner render clearing data:" + MobSpawnerCache.entsNBT.size() + " regular:" + MobSpawnerCache.ents.size() + "\n");
		MobSpawnerCache.ents.clear();
		MobSpawnerCache.entsNBT.clear();
	}
	
	public static void changeTexture(ResourceLocation loc) 
	{
		 Minecraft.getMinecraft().renderEngine.bindTexture(loc);
	}

}
