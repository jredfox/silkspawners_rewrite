package com.evilnotch.silkspawners.client.render.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.evilnotch.lib.main.loader.LoaderFields;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.PairObj;
import com.evilnotch.lib.util.simple.PointId;
import com.evilnotch.silkspawners.Config;
import com.evilnotch.silkspawners.EntityPos;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ResourceLocation;

public class MobSpawnerCache {
	
	public static boolean entCached = false;
	public static HashMap<ResourceLocation,Entity> ents = new LinkedHashMap();
	public static HashMap<NBTTagCompound,PairObj<List<Entity>,EntityPos[]>> entsNBT = new HashMap();
	
	public static final List<Entity> li = new ArrayList();
	public static final PairObj<List<Entity>,EntityPos[]> defaultPair = new PairObj<List<Entity>,EntityPos[]>(li,new EntityPos[]{new EntityPos(0D,0.0D,0.0D)});
	
	public static PairObj<List<Entity>,EntityPos[]> getCachedList(ResourceLocation loc, NBTTagCompound data) 
	{
		if(data.getSize() > 1)
		{
			return getCachedData(data);
		}
			
		Entity e = getCachedEntity(loc);
		
		if(e == null)
		{
			return null;
		}
		
		if(!e.getPassengers().isEmpty())
		{
			return getCachedData(data);
		}
		
		li.clear();
		li.add(e);
		return defaultPair;
	}
	
	private static PairObj<List<Entity>, EntityPos[]> getCachedData(NBTTagCompound data) 
	{
		PairObj<List<Entity>,EntityPos[]> ents = entsNBT.get(data);
		if(ents == null)
		{
			Entity e = RenderUtil.getEntityJockey(data, Minecraft.getMinecraft().world, IItemRendererHandler.lastX, IItemRendererHandler.lastY, IItemRendererHandler.lastZ, Config.renderUseInitSpawn, Config.additionalPassengers);
			if(e == null)
				return null;
			ents = getMounts(e);
			entsNBT.put(data, ents);
		}
		return ents;
	}

	/**
	 * instead of cacheing all entities cache them one at a time for memory heap space optimization
	 * it takes a little more cpu initially but, I think it's worth it
	 */
	public static Entity getCachedEntity(ResourceLocation loc) 
	{
		Entity e = ents.get(loc);
		if(e == null)
		{
			if(EntityUtil.living.containsKey(loc))
				cacheEnt(loc, EntityUtil.living);
			else if(EntityUtil.livingbase.containsKey(loc))
				cacheEnt(loc, EntityUtil.livingbase);
			else if(EntityUtil.nonliving.containsKey(loc))
				cacheEnt(loc, EntityUtil.nonliving);
			else
				return null;//if it doesn't contain the location it's assumed to be blacklisted
			e = ents.get(loc);
		}
		return e;
	}
	
	protected static void cacheEnt(ResourceLocation entity, HashMap<ResourceLocation, String[]> map) 
	{
		Entity e = getSilkEnt(entity);
		if(e == null)
		{
			System.out.println("error caching entity to silkspawners render:" + entity);
			return;
		}
		ents.put(entity, e);
	}
	
	public static Entity getSilkEnt(ResourceLocation entity)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("id", entity.toString());
		Entity e = RenderUtil.getEntityJockey(nbt, Minecraft.getMinecraft().world, IItemRendererHandler.lastX, IItemRendererHandler.lastY, IItemRendererHandler.lastZ, Config.renderUseInitSpawn, Config.additionalPassengers);
		if(e instanceof EntityLiving)
		{
			EntityLiving living = (EntityLiving)e;
			try
			{
				living.isChild();
			}
			catch(Throwable t)
			{
				System.out.println("error cacheing entity to find out if it's a child:" + entity);
				return null;
			}
			if(!Config.renderInitSpawnRnd && e instanceof EntitySlime)
			{
				try 
				{
					LoaderFields.method_setSlimeSize.setAccessible(true);
					LoaderFields.method_setSlimeSize.invoke(e,Config.slimeSize+1,true);
				} 
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) 
				{
					e1.printStackTrace();
				}
			}
		}
		return e;
	}
	
	/**
	 * returns a pair of List<Entity>(passengers and entity base) as well as offsets array
	 */
	public static PairObj<List<Entity>,EntityPos[]> getMounts(Entity entity) 
	{
		List<Entity> toRender = EntityUtil.getEntList(entity);
        
		EntityPos[] offsets = new EntityPos[toRender.size()];
    	for(int i=0;i<toRender.size();i++)
    	{
    		Entity e = toRender.get(i);
    		offsets[i] = new EntityPos(e.posX, e.posY, e.posZ);
    	}
    	
		return new PairObj<List<Entity>,EntityPos[]>(toRender, offsets);
	}
}
