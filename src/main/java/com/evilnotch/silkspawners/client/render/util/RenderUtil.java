package com.evilnotch.silkspawners.client.render.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import com.evilnotch.lib.main.capability.CapRegDefaultHandler;
import com.evilnotch.lib.main.eventhandler.LibEvents;
import com.evilnotch.lib.minecraft.capability.primitive.CapBoolean;
import com.evilnotch.lib.minecraft.capability.registry.CapabilityRegistry;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.minecraft.util.NBTUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.silkspawners.Config;
import com.evilnotch.silkspawners.EntityPos;
import com.evilnotch.silkspawners.client.ToolTipEvent;
import com.evilnotch.silkspawners.client.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

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
	 * since light map for entities is pre-enabled for TE's render enabling it would cause a slight scaling issue barley noticeable but, still a thing
	 */
    public static void setLightmapDisabledTE(boolean disabled)
    {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);

        if (disabled)
        {
            GlStateManager.disableTexture2D();
        }
        else
        {
            GlStateManager.enableTexture2D();
        }

        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
    
    /**
     * mc versions < 1.10.2 spawner scaling for rendering
     */
    public static float oldScale = 0.4375F;
    /**
     * mc versions 1.10.2+ spawner intial scaling it is dynamic though
     */
    public static float newScale = 0.53125F;
    
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
			float scale = oldScale;
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
    	return buffer.isDrawing;
	}

	public static double getRenderTime() 
	{
		return ToolTipEvent.getRenderTime();
	}
	
	public static Entity getEntityJockey(NBTTagCompound compound, World worldIn, double x, double y, double z, boolean useInterface, boolean additionalMounts) 
	{
		boolean cachedSound = LibEvents.disableSound.get();
		boolean cachedMsg = LibEvents.disableMsg.get();
		boolean cachedSpawn = LibEvents.disableSpawn.get();
		
		LibEvents.setSpawnDisable(true);
		LibEvents.setSoundDisable(true);
		LibEvents.setMsgDisable(true);
		Entity e = getEntityStack(compound, worldIn, x, y, z, useInterface, additionalMounts);
		LibEvents.setMsgDisable(cachedMsg);
		LibEvents.setSoundDisable(cachedSound);
		LibEvents.setSpawnDisable(cachedSpawn);
		
		if(e == null)
			return null;
		List<Entity> li = EntityUtil.getEntList(e);
		EntityUtil.updateJockeyPos(li, 0, 0, 0, e.rotationYaw, e.rotationPitch, true);
		EntityUtil.updateJockey(li);
		return e;
	}

	/**
	 * Doesn't force nbt on anything unlike vanilla's methods.
	 * Supports silkspawners rendering for skeleton traps
	 */
	public static Entity getEntityStack(NBTTagCompound compound, World worldIn, double x, double y, double z, boolean useInterface, boolean additionalMounts) 
	{	
        Entity entity = getEntity(compound, worldIn, x, y, z, useInterface, additionalMounts);
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
        
        if (compound.hasKey("Passengers", 9))
        {
             NBTTagList nbttaglist = compound.getTagList("Passengers", 10);
             for (int i = 0; i < nbttaglist.tagCount(); ++i)
             {
                 Entity entity1 = getEntityStack(nbttaglist.getCompoundTagAt(i), worldIn, x, y, z, useInterface, additionalMounts);
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
	public static Entity getEntity(NBTTagCompound nbt, World world, double x, double y, double z, boolean useInterface, boolean additionalMounts) 
	{
		long worldTime = world.getWorldTime();
		long worldTotalTime = world.getTotalWorldTime();
		Entity e = null;
		if(getEntityProps(nbt) > 0)
		{
			e = createEntityFromNBTRender(nbt, world);
			
			if(e == null)
				return null;
			
			if(!additionalMounts)
			{
				e.removePassengers();
				e.dismountRidingEntity();
			}
			else
			{
				e = e.getLowestRidingEntity();
			}
		}
		else
		{
			e = EntityUtil.createEntityByNameQuietly(new ResourceLocation(nbt.getString("id")), world);
			
			if(e == null)
				return null;
			
			NBTTagCompound tag = EntityUtil.getEntityNBT(e);
			e.readFromNBT(tag);
			
			if(useInterface && e instanceof EntityLiving)
			{
				e.setLocationAndAngles(x, y, z, e.rotationYaw, e.rotationPitch);
				if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn((EntityLiving)e, world, JavaUtil.castFloat(e.posX), JavaUtil.castFloat(e.posY), JavaUtil.castFloat(e.posZ), null))
				{
					((EntityLiving) e).onInitialSpawn(world.getDifficultyForLocation(e.getPosition()), (IEntityLivingData)null);
				}
			}
			
			if(!additionalMounts)
			{
				e.removePassengers();
				e.dismountRidingEntity();
			}
			else
			{
				e = e.getLowestRidingEntity();
			}
			
			if(useInterface)
			{
				EntityUtil.setInitSpawned(e);
			}
		}
		world.setWorldTime(worldTime);
		world.setTotalWorldTime(worldTotalTime);
		return e;
	}
	
	/**
	 * create an entity from nbt with full rendering capabilities
	 */
	public static Entity createEntityFromNBTRender(NBTTagCompound nbt, World world) 
	{
		Entity e = EntityUtil.createEntityByNameQuietly(new ResourceLocation(nbt.getString("id")),world);
		if(e == null)
			return null;
		e.readFromNBT(nbt);
		return e;
	}

	public static int getEntityProps(NBTTagCompound nbt) 
	{
		if(nbt == null)
			return 0;
		int size = nbt.getSize();
		if(nbt.hasKey("Passengers"))
			size--;
		if(nbt.hasKey("id"))
			size--;
		return size;
	}

	/**
	 * render entities with control whether or not shadows are allowed to render
	 */
    public static void renderEntity(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean allowShadow)
    {
    	RenderManager rf = Minecraft.getMinecraft().getRenderManager();
    	boolean shadowCached = rf.isRenderShadow();
    	rf.setRenderShadow(allowShadow);
    	rf.renderEntity(entityIn, x, y, z, yaw, partialTicks, false);
    	rf.setRenderShadow(shadowCached);
    }

    /**
     * update an entity equivalent to calling onInitialSpawn() over and over again just without breaking
     */
	public static void onInitialSpawnUpdate(Entity ent, int currentTicks, int ticks)
	{
		if(currentTicks % ticks == 0 && currentTicks != 0 && ent instanceof EntityLiving)
		{
            CapBoolean cap = (CapBoolean) CapabilityRegistry.getCapability(ent, CapRegDefaultHandler.initSpawned);
            if(cap.value)
            {
            	EntityPos pos = new EntityPos(ent);
            	Entity base = MobSpawnerCache.getSilkEnt(EntityUtil.getEntityResourceLocation(ent));
            	NBTTagCompound nbt = EntityUtil.getEntityNBT(base);
            	ent.readFromNBT(nbt);
            	pos.applyPos(ent);
            }
		}
	}
	
	public static Field getField(Object instance, Class clazz, String strfeild)
	{
		try
		{
			Field field = ReflectionHelper.findField(clazz,strfeild);
			field.setAccessible(true);
		
			Field modifiersField = Field.class.getDeclaredField("modifiers");
	    	modifiersField.setAccessible(true);
	    	modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	    	return field;
		}
		catch(Throwable t)
		{
			
		}
		return null;
	}

}
