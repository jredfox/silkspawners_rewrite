package com.EvilNotch.silkspawners.client.render.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.simple.PairObj;
import com.EvilNotch.silkspawners.Config;
import com.EvilNotch.silkspawners.client.ToolTipEvent;
import com.EvilNotch.silkspawners.client.proxy.ClientProxy;

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
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zdoctor.lazymodder.client.render.itemrender.IItemRenderer;

/**
 * this is the copied modified class of NEISpawnerRender port
 * @author jredfox
 */
public class MobSpawnerItemRender implements IItemRenderer{
	
	public static float lastBrightnessX = 0.0F;
	public static float lastBrightnessY = 0.0F;

	@Override
	public boolean renderPre(RenderItem renderItem, ItemStack stack, IBakedModel model, TransformType type) 
	{
        prepare(type);
		return true;
	}

	/**
	 * ported from nei 1.7.10
	 */
	@Override
	public void renderPost(RenderItem renderItem, ItemStack itemstack, IBakedModel model, TransformType type) 
	{
		Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		ResourceLocation loc = null;
        try
        {
            World world = Minecraft.getMinecraft().world;

            NBTTagCompound nbt = itemstack.getTagCompound();
            if(nbt == null || !nbt.hasKey("SpawnData") && !nbt.hasKey("BlockEntityTag") || nbt.getBoolean("isBlank"))
            	return;
            cacheEnts();
            NBTTagCompound data = nbt.hasKey("SpawnData") ? nbt.getCompoundTag("SpawnData") : nbt.getCompoundTag("BlockEntityTag").getCompoundTag("SpawnData");
            String id = data.getString("id");
            loc = new ResourceLocation(id);
            PairObj<List<Entity>,Double[]> pair = getCachedList(loc,data);
            if(pair == null)
            	return;
        	List<Entity> toRender = pair.getKey();
        	Double[] offsets = pair.getValue();
            for(int i=0;i<toRender.size();i++)
            {
            	Entity e = toRender.get(i);
            	if(e == null)
            		continue;
            	renderEntity(e,world,offsets[i],type);
            }
        }
        catch(Exception e)
        {
            if(isDrawing(Tessellator.getInstance().getBuffer()))
                Tessellator.getInstance().draw();
            System.out.println("exception drawing:" + loc + " removing from hashmap for render");
            ents.remove(loc);
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
	
	public void renderEntity(Entity entity, World world,double offset,TransformType type) {
		float lastX = OpenGlHelper.lastBrightnessX;
		float lastY = OpenGlHelper.lastBrightnessY;
		
        GL11.glPushMatrix();
        
    	boolean disableLight = type == TransformType.GUI;
        setLightmapDisabled(disableLight);//don't enable light mapping to false if it's rendering in the gui
        
        entity.setWorld(world);
        float f1 = 0.4375F;
        if(EntityUtil.getShadowSize(entity) > 1.5)
            f1 = 0.1F;
        GL11.glRotatef((float) (getRenderTime()*10), 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-20F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.0F, -0.4F, 0.0F);
        GL11.glScalef(f1, f1, f1);
        entity.setLocationAndAngles(0, 0, 0, 0.0F, 0.0F);
        
        float partialTicks = Config.animationItem ? Minecraft.getMinecraft().getRenderPartialTicks() : 0;
        Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0D, offset, 0.0D, 0.0F, partialTicks,false);
        
        GL11.glPopMatrix();
        
        resetOpenGL(type);
        /*GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);*/
	}
	/**
	 * call this after rendering some entities
	 */
	public void resetOpenGL(TransformType type) 
	{
        if(type == TransformType.THIRD_PERSON_LEFT_HAND || type == TransformType.THIRD_PERSON_RIGHT_HAND)
        {
        	GlStateManager.disableCull();
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.blendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableRescaleNormal();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        
        ClientProxy.changeTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
	}

	public static void prepare(TransformType type){
        ClientProxy.changeTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.blendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();

        if (net.minecraft.client.Minecraft.isAmbientOcclusionEnabled())
        {
            GlStateManager.shadeModel(org.lwjgl.opengl.GL11.GL_SMOOTH);
        }
        else
        {
            GlStateManager.shadeModel(org.lwjgl.opengl.GL11.GL_FLAT);
        }
        
        if(type != TransformType.GUI)
        {
        	lastBrightnessX = OpenGlHelper.lastBrightnessX;
        	lastBrightnessY = OpenGlHelper.lastBrightnessY;
        }
        GlStateManager.enableAlpha();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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

	
	@Override
	public boolean renderItemOverlayIntoGUIPre(RenderItem renderItem, FontRenderer fontRenderer, ItemStack itemstack,int xPosition, int yPosition, String text) {
		return true;
	}

	@Override
	public void renderItemOverlayIntoGUIPost(RenderItem renderItem, FontRenderer fontRenderer, ItemStack itemstack,int xPosition, int yPosition, String text) {
	}
	
	public static List<Entity> li = new ArrayList();
	public static final PairObj<List<Entity>,Double[]> defaultPair = new PairObj<List<Entity>,Double[]>(li,new Double[]{0D});
	protected PairObj<List<Entity>,Double[]> getCachedList(ResourceLocation loc, NBTTagCompound data) 
	{
		if(data.getSize() > 1)
		{
			PairObj<List<Entity>,Double[]> ents = entsNBT.get(data);
			if(ents == null)
			{
				Entity e = MobSpawnerBaseLogic.getEntityJockey(data, Minecraft.getMinecraft().world, 0, 0, 0, Config.renderUseInitSpawn,false);
				if(e == null)
					return null;
				ents = getEnts(e);
				entsNBT.put(data, ents);
			}
			return ents;
		}
			
		Entity e = ents.get(loc);
		if(e == null)
			return null;
		
		if(li.isEmpty())
			li.add(e);
		else
			li.set(0,e);
		return defaultPair;
	}
	/**
	 * returns a pair of List<Entity>(passengers and entity base) as well as offsets array
	 */
	public PairObj<List<Entity>,Double[]> getEnts(Entity entity) {
		List<Entity> toRender = JavaUtil.toArray(entity.getRecursivePassengers());
		toRender.add(0,entity);
        
        Double[] offsets = new Double[toRender.size()];
    	for(int i=0;i<toRender.size();i++)
    	{
    		Entity e = toRender.get(i);
    		if(e.isRiding())
    		{
    			if(e.getRidingEntity() != null)
    				e.getRidingEntity().updatePassenger(e);
    		}
    		offsets[i] = e.posY;
    	}
    	
		return new PairObj<List<Entity>,Double[]>(toRender,offsets);
	}

	public boolean isDrawing(BufferBuilder buffer) {
		return (Boolean)ReflectionUtil.getObject(buffer, BufferBuilder.class, "isDrawing");
	}
	
	public static boolean entCached = false;
	public static HashMap<ResourceLocation,Entity> ents = new LinkedHashMap();
	public static HashMap<NBTTagCompound,PairObj<List<Entity>,Double[]>> entsNBT = new HashMap();
	public void cacheEnts() 
	{
		if(entCached)
			return;
		long time = System.currentTimeMillis();
		System.out.println("Starting to cache Ents!");
		cacheEnts(EntityUtil.living);
		cacheEnts(EntityUtil.livingbase);
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
				
				if(e instanceof EntitySlime)
				{
					NBTTagCompound nbt = EntityUtil.getEntityNBT(e);
					nbt.setInteger("Size", Config.slimeSize);
					e.readFromNBT(nbt);
				}
				else if(Config.renderUseInitSpawn)
				{
					living.onInitialSpawn(living.world.getDifficultyForLocation(new BlockPos(0,0,0)), (IEntityLivingData)null);
				}
			}
			ents.put(loc,e);
		}
	}

	public double getRenderTime() {
		return ToolTipEvent.getRenderTime();
	}
	
}