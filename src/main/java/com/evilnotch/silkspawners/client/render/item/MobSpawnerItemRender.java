package com.evilnotch.silkspawners.client.render.item;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.evilnotch.iitemrender.handlers.IItemRenderer;
import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.evilnotch.lib.util.simple.PairObj;
import com.evilnotch.silkspawners.Config;
import com.evilnotch.silkspawners.client.render.util.MobSpawnerCache;
import com.evilnotch.silkspawners.client.render.util.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
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
            
            PairObj<List<Entity>, Vec3d[]> pair = MobSpawnerCache.getCachedList(loc, data);
            if(pair == null)
            {
            	return;
            }
        	List<Entity> toRender = pair.getKey();
        	Vec3d[] offsets = pair.getValue();
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
        
       RenderUtil.setLightmapDisabled(IItemRendererHandler.isGui(type));
	}
	
	public void renderEntity(Entity entity, float scale, World world, Vec3d offset, TransformType type, float partialTicks) 
	{	
        GlStateManager.pushMatrix();
        
        GlStateManager.rotate((float) (RenderUtil.getRenderTime()*10), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-20F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, -0.4F, 0.0F);
        GlStateManager.scale(scale, scale, scale);
        
        boolean flag = IItemRendererHandler.isGui(type);
        RenderUtil.setLightmapDisabled(flag);//always keep lighting enabled for rendering entities
        if(flag)
        {
        	GlStateManager.enableNormalize();
        }
   
        partialTicks = Config.animationItem ? partialTicks : 0;
        entity.setWorld(world);
        entity.dimension = world.provider.getDimension();
        entity.setLocationAndAngles(IItemRendererHandler.lastX, IItemRendererHandler.lastY, IItemRendererHandler.lastZ, entity.rotationYaw, entity.rotationPitch);
        
        if(!IItemRendererHandler.isGui(type) && Config.dynamicLightingItem)
        {
        	RenderUtil.setLightMap(entity);
        }
        
        RenderUtil.renderEntity(entity, offset.x, offset.y, offset.z, 0.0F, partialTicks, Config.renderShadows);
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
        
        if(IItemRendererHandler.isThirdPerson(type))
        {
        	GlStateManager.disableCull();
        }
        else
        {
        	GlStateManager.enableCull();
        }
        
        GL11.glEnable(GL11.GL_BLEND);//for people stupid and don't follow 1.8+ rules
        GlStateManager.enableBlend();
        if(IItemRendererHandler.isGui(type))
        {
        	GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        	GlStateManager.disableNormalize();
        }
        else
        {
        	GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        	GlStateManager.enableNormalize();
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
	
	@Override
	public void restoreLastOpenGl()
	{
		if(IItemRendererHandler.isThirdPerson(IItemRendererHandler.cachedTransformType))
		{
			GlStateManager.disableCull();
		}
	}

}