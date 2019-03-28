package com.evilnotch.silkspawners.client.render.util;

import java.util.List;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.minecraft.util.MinecraftUtil;
import com.evilnotch.lib.minecraft.util.NBTUtil;
import com.evilnotch.silkspawners.Config;
import com.evilnotch.silkspawners.client.ToolTipEvent;
import com.evilnotch.silkspawners.client.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class RenderUtil {
	
	/**
	 * the default brightness to render
	 */
	public static int defaultLighting = 15728880;
	public static void setLightMap(Entity e) 
	{
	    int i = e.getBrightnessForRender();

        if (e.isBurning())
        {
            i = 15728880;
        }

       setLightMap(i);
	}
	
	public static void setLightMap(int i) 
	{
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
	}
	
    public static void setLightmapDisabled(boolean disabled)
    {
        if (disabled)
        {
            Minecraft.getMinecraft().entityRenderer.disableLightmap();
        }
        else
        {
        	Minecraft.getMinecraft().entityRenderer.enableLightmap();
        }
    }
    
    /**
     * mc versions < 1.10.2 spawner scaling for rendering
     */
    public static final float oldScale = 0.4375F;
    /**
     * mc versions 1.10.2+ spawner intial scaling it is dynamic though
     */
    public static final float newScale = 0.53125F;
    
	/**
	 * get the smallest scale based upon a mob stack for rendering so they are all at the same scaling
	 */
	public static float getBlockScale(List<Entity> ents, boolean old)
	{
		if(ents.isEmpty())
		{
			return -1;
		}
		else if(old && !Config.mergeOldNewScaling)
		{
			return oldScale;
		}
		float scale = getBlockScale(ents.get(0), old);
		for(Entity e : ents)
		{
			float compare = getBlockScale(e, old);
			if(compare < scale)
				scale = compare;
		}
		return scale;
	}

	public static float getDynamicScale(Entity entity) 
	{
	    float scale = newScale;
	    float max = Math.max(entity.width, entity.height);

	    if ((double)max > 1.0D)
	    {
	    	scale /= max;
	    }
	    return scale;
	}
	
	public static float getBlockScale(Entity entity,boolean old) 
	{
		if(old)
		{
			if(Config.mergeOldNewScaling)
			{
				 float max = Math.max(entity.width, entity.height);
				 if((double)max <= 1.0D)
				 {
					 return newScale;
				 }
			}
			return oldScale;
		}
		
		return getDynamicScale(entity);
	}
	
	/**
	 * get the smallest scale based upon a mob stack for rendering
	 */
	public static float getItemScale(List<Entity> ents, boolean old)
	{
		if(ents.isEmpty())
		{
			return -1;
		}
		
		float scale = getItemScale(ents.get(0),old);
		for(Entity e : ents)
		{
			float compare = getItemScale(e,old);
			if(compare < scale)
			{
				scale = compare;
			}
		}
		return scale;
	}
	
	/**
	 * old config option is equal to nei's scale exactly
	 */
	public static float getItemScale(Entity e,boolean old)
	{
		if(old)
		{
			float scale = 0.4375F;
			if(EntityUtil.getShadowSize(e) > 1.5)
			{
				scale = 0.1F;
			}
			else if(Config.mergeOldNewScaling)
			{
				 float max = Math.max(e.width, e.height);
				 if((double)max <= 1.0D)
				 {
					 scale = newScale;
				 }
			}
	        return scale;
		}
		return getDynamicScale(e);
	}
	
    public static boolean isDrawing(BufferBuilder buffer) 
	{
		return (Boolean)ReflectionUtil.getObject(buffer, BufferBuilder.class, ClientProxy.isDrawing);
	}

	public static double getRenderTime() 
	{
		return ToolTipEvent.getRenderTime();
	}
	
    
	public static Entity getEntityStackFixed(NBTTagCompound compound,World worldIn, double x, double y, double z,boolean useInterface,boolean attemptSpawn) 
	{
		Entity e = getEntityJockey(compound, worldIn, x, y, z, useInterface, attemptSpawn);
		EntityUtil.fixJocksRender(e);
		return e;
	}

	/**
	 * Doesn't force nbt on anything unlike vanilla's methods.
	 * Supports silkspawners rendering for skeleton traps
	 */
	private static Entity getEntityJockey(NBTTagCompound compound,World worldIn, double x, double y, double z,boolean useInterface,boolean attemptSpawn) 
	{	
        Entity entity = getEntity(compound,worldIn,new BlockPos(x,y,z),useInterface);
        if(entity == null)
        	return null;
		
        Entity toMount = entity;
		if(new ResourceLocation(compound.getString("id")).toString().equals("minecraft:skeleton_horse"))
		{
			if(compound.hasKey("SkeletonTrap"))
			{
				Entity e2 = EntityUtil.createEntityFromNBTQuietly(new ResourceLocation("skeleton"), NBTUtil.getNBTFromString("{ArmorItems:[{},{},{},{id:iron_helmet,Count:1,tag:{ench:[{lvl:1s,id:33s}]} }],HandItems:[{id:bow,Count:1,tag:{ench:[{lvl:1s,id:33s}]} },{}] }"), worldIn);
				e2.startRiding(entity, true);
				return entity;
			}
		}
        
        if(attemptSpawn)
        {
            entity.forceSpawn = true;
        	if(!worldIn.spawnEntity(entity))
        		return null;
        }
        
        if (compound.hasKey("Passengers", 9))
        {
             NBTTagList nbttaglist = compound.getTagList("Passengers", 10);
             for (int i = 0; i < nbttaglist.tagCount(); ++i)
             {
                 Entity entity1 = getEntityJockey(nbttaglist.getCompoundTagAt(i), worldIn, x, y, z,useInterface,attemptSpawn);
                  if (entity1 != null)
                  {
                      entity1.startRiding(toMount, true);
                  }
             }
        }

       return entity;
	}

	/**
	 * first index is to determine if your on the first part of the opening of the nbt if so treat nbt like normal
	 */
	public static Entity getEntity(NBTTagCompound nbt,World world,BlockPos pos,boolean useInterface) 
	{
		Entity e = null;
		if(getEntityProps(nbt).getSize() > 0)
		{
			e = createEntityFromNBTRender(nbt,pos,world);
		}
		else
		{
			e = EntityUtil.createEntityByNameQuietly(new ResourceLocation(nbt.getString("id")),world);
			if(e == null)
				return null;
			e.setLocationAndAngles(0, 0, 0, 0.0F, 0.0F);
			NBTTagCompound tag = EntityUtil.getEntityNBT(e);
			
			e.readFromNBT(tag);
			
			if(e instanceof EntityLiving && useInterface || e instanceof EntityShulker)
			{
				((EntityLiving) e).onInitialSpawn(world.getDifficultyForLocation(pos), (IEntityLivingData)null);
			}
		}
		return e;
	}
	/**
	 * create an entity from nbt with full rendering capabilities
	 */
	public static Entity createEntityFromNBTRender(NBTTagCompound nbt,BlockPos pos, World world) 
	{
		Entity e = EntityUtil.createEntityByNameQuietly(new ResourceLocation(nbt.getString("id")),world);
		if(e == null)
			return null;
		e.setLocationAndAngles(0, 0, 0, 0.0F, 0.0F);
		e.readFromNBT(nbt);
		//hard coded dynamic fix for a shitty thing now it doesn't change it's type only it's render stuffs
		if(e instanceof EntityShulker)
		{
			((EntityLiving) e).onInitialSpawn(world.getDifficultyForLocation(pos), (IEntityLivingData)null);
		}
		return e;
	}

	private static NBTTagCompound getEntityProps(NBTTagCompound nbt) {
		if(nbt == null)
			return null;
		nbt = nbt.copy();
		nbt.removeTag("Passengers");
		nbt.removeTag("id");
		return nbt;
	}

	/**
	 * render enitites with control whether or not shadows are allowed to render
	 */
    public static void renderEntity(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean allowShadow)
    {
    	RenderManager rf = Minecraft.getMinecraft().getRenderManager();
    	boolean shadowCached = rf.options.entityShadows;
    	if(!allowShadow)
    	{
    		rf.options.entityShadows = false;
    	}
    	rf.renderEntity(entityIn, x, y, z, yaw, partialTicks, false);
    	rf.options.entityShadows = shadowCached;
    }

}
