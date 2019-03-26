package com.evilnotch.silkspawners.client.render.item;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import com.evilnotch.iitemrender.handlers.IItemRenderer;
import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.main.loader.LoaderFields;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.simple.PairObj;
import com.evilnotch.silkspawners.Config;
import com.evilnotch.silkspawners.client.ToolTipEvent;
import com.evilnotch.silkspawners.client.proxy.ClientProxy;
import com.evilnotch.silkspawners.client.render.tileentity.MobSpawnerStackBase;
import com.evilnotch.silkspawners.client.render.util.MobSpawnerCache;
import com.evilnotch.silkspawners.client.render.util.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * this is the copied modified class of NEISpawnerRender port
 * @author jredfox
 */
public class MobSpawnerItemRender implements IItemRenderer{
	
	public static float lastX = 0.0F;
	public static float lastY = 0.0F;
	
	@Override
	public void render(ItemStack stack, IBakedModel model, TransformType type, float partialTicks) 
	{
		IItemRendererHandler.renderItemStack(stack, model);
		IItemRendererHandler.restoreLastBlurMipmap();
        
		ResourceLocation loc = null;
        try
        {
            World world = Minecraft.getMinecraft().world;

            NBTTagCompound nbt = stack.getTagCompound();
            if(nbt == null || !nbt.hasKey("SpawnData") && !nbt.hasKey("BlockEntityTag") || nbt.getBoolean("isBlank"))
            	return;

            NBTTagCompound data = nbt.hasKey("SpawnData") ? nbt.getCompoundTag("SpawnData") : nbt.getCompoundTag("BlockEntityTag").getCompoundTag("SpawnData");
            loc = new ResourceLocation(data.getString("id"));
            
            PairObj<List<Entity>,Double[]> pair = MobSpawnerCache.getCachedList(loc, data);
            if(pair == null)
            {
            	return;
            }
        	List<Entity> toRender = pair.getKey();
        	Double[] offsets = pair.getValue();
            float f1 = RenderUtil.getItemScale(toRender, !Config.dynamicScalingItem);
            for(int i=0;i<toRender.size();i++)
            {
            	Entity e = toRender.get(i);
            	if(e == null)
            	{
            		continue;
            	}
            	renderEntity(e, f1, world, offsets[i], type, partialTicks);
            }
        }
        catch(Exception e)
        {
            if(RenderUtil.isDrawing(Tessellator.getInstance().getBuffer()))
                Tessellator.getInstance().draw();
            
            MobSpawnerCache.ents.remove(loc);
            
            System.out.println("exception drawing:" + loc + " removing from hashmap for render");   
        }
        
        if(type == TransformType.GUI)
        {
        	Minecraft.getMinecraft().entityRenderer.disableLightmap();
        	GlStateManager.disableLighting();
        }
        else
        {
        	 Minecraft.getMinecraft().entityRenderer.enableLightmap();
        	 GlStateManager.enableLighting();
        }
	}
	
	public void renderEntity(Entity entity, float scale, World world, double offset, TransformType type, float partialTicks) 
	{
		lastX = OpenGlHelper.lastBrightnessX;
		lastY = OpenGlHelper.lastBrightnessY;
		
        GL11.glPushMatrix();
        
    	boolean disableLight = type == TransformType.GUI;
        RenderUtil.setLightmapDisabled(disableLight);//don't enable light mapping to false if it's rendering in the gui
        
        entity.setWorld(world);
        GL11.glRotatef((float) (ToolTipEvent.getRenderTime()*10), 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-20F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.0F, -0.4F, 0.0F);
        GL11.glScalef(scale, scale, scale);
   
        
        partialTicks = Config.animationItem ? partialTicks : 0;
        EntityPlayer p = Minecraft.getMinecraft().player;
        
        entity.posX = p.posX;
        entity.posY = p.posY;
        entity.posZ = p.posZ;
        
        if(Config.dynamicLightingItem)
        	RenderUtil.setLightMap(entity);
        
        entity.setLocationAndAngles(0, 0, 0, 0.0F, 0.0F);
        
        Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0D, offset, 0.0D, 0.0F, partialTicks, false);
        
        GL11.glPopMatrix();
        
        resetOpenGL(type);
	}
	
	/**
	 * call this after rendering an entity
	 */
	public void resetOpenGL(TransformType type) 
	{
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        
        if(type == TransformType.THIRD_PERSON_LEFT_HAND || type == TransformType.THIRD_PERSON_RIGHT_HAND)
        {
        	GlStateManager.disableCull();
        }
        
        if(type == type.GUI)
        {
        	GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }
        else
        {
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.depthMask(true);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastX, lastY);
        IItemRendererHandler.restoreLastBlurMipmap();
	}


}