package com.evilnotch.silkspawners;

import com.evilnotch.lib.api.ReflectionUtil;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.World;

public class TileUtil {
	
	public static final RegistryNamespaced < ResourceLocation, Class <? extends TileEntity >> tileRegistry = (RegistryNamespaced<ResourceLocation, Class<? extends TileEntity>>) ReflectionUtil.getObject(null, TileEntity.class, "REGISTRY");
	public static final ResourceLocation mob_spawner = new ResourceLocation("mob_spawner");
	
	/**
	 * create a tile entity from string without knowing it's current mapping class
	 */
	public static TileEntity create(ResourceLocation loc)
	{
		Class oclass = tileRegistry.getObject(loc);
        if (oclass != null)
        {
            try 
            {
				return (TileEntity) oclass.newInstance();
			} 
            catch (InstantiationException | IllegalAccessException e)
            {
				return null;
			}
        }
        return null;
	}

}
