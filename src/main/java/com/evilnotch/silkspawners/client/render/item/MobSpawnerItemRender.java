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
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

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
		IItemRendererHandler.applyTransformPreset(model);
		
		//make the entities render inside of the center of the block to start out with
		GlStateManager.translate(0.5F, 0.5F, 0.5F);

        try
        {
    		lastX = OpenGlHelper.lastBrightnessX;
    		lastY = OpenGlHelper.lastBrightnessY;
    		
            World world = Minecraft.getMinecraft().world;

            NBTTagCompound nbt = stack.getTagCompound();
            if(nbt == null || !nbt.hasKey("SpawnData") && !nbt.hasKey("BlockEntityTag") || nbt.getBoolean("isBlank"))
            	return;

            NBTTagCompound data = nbt.hasKey("SpawnData") ? nbt.getCompoundTag("SpawnData") : nbt.getCompoundTag("BlockEntityTag").getCompoundTag("SpawnData");
            ResourceLocation loc = new ResourceLocation(data.getString("id"));
            
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
            throw new RuntimeException(e);//make people crash so I know if it's broken
//            if(RenderUtil.isDrawing(Tessellator.getInstance().getBuffer()))
//                Tessellator.getInstance().draw();
//            System.out.println("exception drawing:" + stack.getItem().getRegistryName() + " with NBT:" + stack.getTagCompound());   
        }
        
       RenderUtil.setLightmapDisabled(IItemRendererHandler.isGui(type) || IItemRendererHandler.isUnkown(type));
	}
	
	public void renderEntity(Entity entity, float scale, World world, double offset, TransformType type, float partialTicks) 
	{	
        GlStateManager.pushMatrix();
        
        RenderUtil.setLightmapDisabled(IItemRendererHandler.isGui(type) || IItemRendererHandler.isUnkown(type));//always keep lighting enabled for rendering entities
        
        entity.setWorld(world);
        GlStateManager.rotate((float) (RenderUtil.getRenderTime()*10), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-20F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, -0.4F, 0.0F);
        GlStateManager.scale(scale, scale, scale);
   
        partialTicks = Config.animationItem ? partialTicks : 0;
        
        if(Config.dynamicSetPositioning || Config.dynamicLightingItem)
        {
        	entity.setLocationAndAngles(IItemRendererHandler.lastX, IItemRendererHandler.lastY, IItemRendererHandler.lastZ, 0.0F, 0.0F);
        }
        
        if(!IItemRendererHandler.isGui(type) && Config.dynamicLightingItem)
        {
        	RenderUtil.setLightMap(entity);
        }
        
        if(!Config.dynamicSetPositioning)
        {
        	entity.setLocationAndAngles(0, 0, 0, 0.0F, 0.0F);
        }
        
        entity.setRotationYawHead(0.0F);//fixes head bugs
        RenderUtil.renderEntity(entity, 0.0D, offset, 0.0D, 0.0F, partialTicks, Config.renderShadows);
        
        GlStateManager.popMatrix();
        
        resetOpenGL(type);
	}
	
	/**
	 * call this after rendering an entity
	 */
	public void resetOpenGL(TransformType type) 
	{
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        
        if(type == TransformType.THIRD_PERSON_LEFT_HAND || type == TransformType.THIRD_PERSON_RIGHT_HAND)
        {
        	GlStateManager.disableCull();
        }
        
        GlStateManager.enableBlend();
        if(type == type.GUI)
        {
        	GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }
        else
        {
        	GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }
        
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastX, lastY);
	}

	/**
	 * return the model so the entity has the same scaling, rotation, and transitioning as the IBakedModel. 
	 * For normal non hooks of IItemRenderers return FIXED(1.7.10 transforms) or NONE(no transforms)
	 */
	@Override
	public TransformPreset getTransformPreset() 
	{
		return TransformPreset.NONE;
	}

}