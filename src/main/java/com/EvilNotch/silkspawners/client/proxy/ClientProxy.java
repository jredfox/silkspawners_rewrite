package com.EvilNotch.silkspawners.client.proxy;

import java.util.Map;

import com.EvilNotch.lib.Api.FieldAcess;
import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.silkspawners.Config;
import com.EvilNotch.silkspawners.ItemSpawner;
import com.EvilNotch.silkspawners.MainJava;
import com.EvilNotch.silkspawners.client.ToolTipEvent;
import com.EvilNotch.silkspawners.client.render.item.MobSpawnerItemRender;
import com.EvilNotch.silkspawners.client.render.tileentity.MobSpawnerStackBase;
import com.EvilNotch.silkspawners.commands.CommandMTHand;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
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
		if(Config.mobItemRender)
			IItemRendererHandler.registerIItemRenderer(MainJava.mob_spawner, new MobSpawnerItemRender());
		MinecraftForge.EVENT_BUS.register(new ToolTipEvent());
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
	}
	@Override
	public void serverClose() {
		ToolTipEvent.rainbows.clear();
	}
	
	public static void changeTexture(ResourceLocation loc) 
	{
		 Minecraft.getMinecraft().renderEngine.bindTexture(loc);
	}

}
