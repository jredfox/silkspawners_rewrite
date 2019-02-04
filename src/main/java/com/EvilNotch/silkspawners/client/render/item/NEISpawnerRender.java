package com.evilnotch.silkspawners.client.render.item;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.evilnotch.iitemrender.handlers.IItemRenderer;
import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.silkspawners.client.ToolTipEvent;
import com.evilnotch.silkspawners.client.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * this is the nei port from 1.7.10 > 1.12.2 with using resource locations instead of integer values
 * @author jredfox
 */
public class NEISpawnerRender implements IItemRenderer{

	@Override
	public boolean renderPre(RenderItem renderItem, ItemStack stack, IBakedModel model, TransformType type) {
        ClientProxy.changeTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		return true;
	}

	/**
	 * ported from nei 1.7.10 note the open gl reset is broken on lightmapping thus do not use this but, as a reference
	 */
	@Override
	public void renderPost(RenderItem renderItem, ItemStack itemstack, IBakedModel model, TransformType type) 
	{
		ResourceLocation loc = null;
        try
        {
            World world = Minecraft.getMinecraft().world;

            NBTTagCompound nbt = itemstack.getTagCompound();
            if(nbt == null || !nbt.hasKey("SpawnData"))
            	return;
            cacheEnts();
            NBTTagCompound data = nbt.getCompoundTag("SpawnData");
            String id = data.getString("id");
            loc = new ResourceLocation(id);
            Entity entity = getEntity(loc,data);
            if(entity == null)
            	return;
            
            GL11.glPushMatrix();
            
            entity.setWorld(world);
            float f1 = 0.4375F;
            if(EntityUtil.getShadowSize(entity) > 1.5)
                f1 = 0.1F;
            GL11.glRotatef((float) (getRenderTime()*10), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-20F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.4F, 0.0F);
            GL11.glScalef(f1, f1, f1);
            entity.setLocationAndAngles(0, 0, 0, 0.0F, 0.0F);
            Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0,false);
            
            GL11.glPopMatrix();
    
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }
        catch(Exception e)
        {
            if(isDrawing(Tessellator.getInstance().getBuffer()))
                Tessellator.getInstance().draw();
            System.out.println("exception drawing:" + loc + " removing from hashmap for render");
            ents.remove(loc);
        }
	}
	/**
	 * a method for getting the entity based on nbt. if size <= 1 will fetch from main cache for easy rendering
	 * since this is direct port from 1.7.10 doesn't support any data at all and also without jockey support. It's nice
	 * if you don't want to go through a huge process and only except to render static ids.
	 */
	protected Entity getEntity(ResourceLocation loc, NBTTagCompound data) 
	{
		return ents.get(loc);
	}

	public boolean isDrawing(BufferBuilder buffer) {
		return (Boolean)ReflectionUtil.getObject(buffer, BufferBuilder.class, "isDrawing");
	}
	
	public static boolean entCached = false;
	public static HashMap<ResourceLocation,Entity> ents = new LinkedHashMap();
	public void cacheEnts() 
	{
		if(entCached)
			return;
		long time = System.currentTimeMillis();
		System.out.println("Starting to cache Ents!");
		cacheEnts(EntityUtil.living);
		cacheEnts(EntityUtil.livingbase);
		cacheEnts(EntityUtil.nonliving);
		JavaUtil.printTime(time, "Done Cacheing Ents:");
		entCached = true;
	}

	protected void cacheEnts(HashMap<ResourceLocation, String[]> map) 
	{
		for(ResourceLocation loc : map.keySet())
		{
			Entity e = EntityUtil.createEntityByNameQuietly(loc, Minecraft.getMinecraft().world,true);
			if(e instanceof EntityLiving)
			{
				EntityLiving living = (EntityLiving)e;
				living.isChild();
			}
			ents.put(loc,e);
		}
	}

	public double getRenderTime() {
		return ToolTipEvent.getRenderTime();
	}

	@Override
	public boolean renderItemOverlayIntoGUIPre(RenderItem renderItem, FontRenderer fontRenderer, ItemStack itemstack,
			int xPosition, int yPosition, String text) {
		return true;
	}

	@Override
	public void renderItemOverlayIntoGUIPost(RenderItem renderItem, FontRenderer fontRenderer, ItemStack itemstack,
			int xPosition, int yPosition, String text) {
	}

}
