package com.evilnotch.silkspawners.client.render.util;

import java.util.List;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.silkspawners.client.ToolTipEvent;
import com.evilnotch.silkspawners.client.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class RenderUtil {
	
	public static void setLightMap(Entity e, BlockPos pos) 
	{
		e.posX = pos.getX();
		e.posY = pos.getY();
		e.posZ = pos.getZ();
		setLightMap(e);
	}
	
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
	 * get the smallest scale based upon a mob stack for rendering so they are all at the same scaling
	 */
    public static final float oldScale = 0.4375F;;
	public static float getBlockScale(List<Entity> ents, boolean old)
	{
		if(old)
		{
			return oldScale;
		}
		else if(ents.isEmpty())
		{
			return -1;
		}
		float scale = getBlockScale(ents.get(0),old);
		for(Entity e : ents)
		{
			float compare = getBlockScale(e,old);
			if(compare < scale)
				scale = compare;
		}
		return scale;
	}
	
	public static float getBlockScale(Entity entity,boolean old) 
	{
		if(old)
		{
			return oldScale;
		}
		
		return getDynamicScale(entity);
	}

	public static float getDynamicScale(Entity entity) 
	{
	    float new_scale = 0.53125F;
	    float max = Math.max(entity.width, entity.height);

	    if ((double)max > 1.0D)
	    {
	       new_scale /= max;
	    }
	    return new_scale;
	}
	
	/**
	 * get the smallest scale based upon a mob stack for rendering
	 */
	public static float getItemScale(List<Entity> ents, boolean old)
	{
		if(ents.isEmpty())
			return -1;
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
				scale = 0.1F;
	        return scale;
		}
		return getDynamicScale(e);
	}
	
    public static boolean isDrawing(BufferBuilder buffer) 
	{
		return (Boolean)ReflectionUtil.getObject(buffer, BufferBuilder.class, ClientProxy.isDrawing);
	}

	public static double getRenderTime() {
		return ToolTipEvent.getRenderTime();
	}
	

}
