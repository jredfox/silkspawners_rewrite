package com.EvilNotch.silkspawners.client.render.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.EvilNotch.lib.Api.ReflectionUtil;
import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.util.JavaUtil;
import com.EvilNotch.lib.util.simple.PairObj;
import com.EvilNotch.silkspawners.client.ToolTipEvent;
import com.EvilNotch.silkspawners.client.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
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
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import zdoctor.lazymodder.client.render.itemrender.IItemRenderer;

/**
 * this is the copied modified class of NEISpawnerRender port
 * @author jredfox
 *
 */
public class MobSpawnerItemRender implements IItemRenderer{
	

	@Override
	public boolean renderPre(RenderItem renderItem, ItemStack stack, IBakedModel model, TransformType type) {
        ClientProxy.changeTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		return true;
	}

	/**
	 * ported from nei 1.7.10
	 */
	@Override
	public void renderPost(RenderItem renderItem, ItemStack itemstack, IBakedModel model, TransformType type) 
	{
		ResourceLocation loc = null;
        try
        {
            World world = Minecraft.getMinecraft().world;

            NBTTagCompound nbt = itemstack.getTagCompound();
            if(nbt == null || !nbt.hasKey("SpawnData") && !nbt.hasKey("BlockEntityTag"))
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
            	renderEntity(e,world,offsets[i]);
            }
        }
        catch(Exception e)
        {
            if(isDrawing(Tessellator.getInstance().getBuffer()))
                Tessellator.getInstance().draw();
            System.out.println("exception drawing:" + loc + " removing from hashmap for render");
            ents.remove(loc);
        }
	}
	
	public void renderEntity(Entity entity, World world,double offset) {
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
        Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0D, offset, 0.0D, 0.0F, 0,false);
        
        GL11.glPopMatrix();

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}
	
	@Override
	public boolean renderItemOverlayIntoGUIPre(RenderItem renderItem, FontRenderer fontRenderer, ItemStack itemstack,int xPosition, int yPosition, String text) {
		return true;
	}

	@Override
	public void renderItemOverlayIntoGUIPost(RenderItem renderItem, FontRenderer fontRenderer, ItemStack itemstack,int xPosition, int yPosition, String text) {
	}
	
	public static final List<Entity> li = new ArrayList();
	public static PairObj<List<Entity>,Double[]> defaultPair = null;
	protected PairObj<List<Entity>,Double[]> getCachedList(ResourceLocation loc, NBTTagCompound data) 
	{
		if(data.getSize() > 1)
		{
			PairObj<List<Entity>,Double[]> ents = entsNBT.get(data);
			if(ents == null)
			{
				Entity e = EntityUtil.getEntityJockey(data, Minecraft.getMinecraft().world, 0, 0, 0, true);
				if(e == null)
					return null;
				ents = getEnts(e);
				entsNBT.put(data, ents);
			}
			return ents;
		}
		//convert default entity into the new format using list<Entity> offsets[] while still being optimzied
		if(defaultPair == null)
			defaultPair = new PairObj<List<Entity>,Double[]>(li,new Double[]{0D});
			
		Entity e = ents.get(loc);
		if(e == null)
			return null;
		
		if(li.isEmpty())
			li.add(e);
		else
			li.set(0,e);
		return defaultPair;
	}

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
			}
			ents.put(loc,e);
		}
	}

	public double getRenderTime() {
		return ToolTipEvent.getRenderTime();
	}
	
}