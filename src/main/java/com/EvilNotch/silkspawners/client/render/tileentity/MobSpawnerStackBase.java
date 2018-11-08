package com.evilnotch.silkspawners.client.render.tileentity;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.evilnotch.silkspawners.client.render.item.MobSpawnerItemRender;
import com.evilnotch.lib.minecraft.util.TileEntityUtil;
import com.evilnotch.silkspawners.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class MobSpawnerStackBase extends TileEntitySpecialRenderer<TileEntity>{
	
	/**
	 * this has the capability of rendering an entire stack of mobs via the tile entity with 1.9+ passenger support
	 */
    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
    	if(te.getWorld() == null)
    		return;//don't render until the tile entity world is set
    	MobSpawnerBaseLogic logic = ((TileEntityMobSpawner)te).getSpawnerBaseLogic();
    	List<Entity> ents = logic.getCachedEntities();
    	float scale = getScale(ents, !Config.dynamicScalingBlock);
    	
    	float posX = OpenGlHelper.lastBrightnessX;
    	float posY = OpenGlHelper.lastBrightnessY;
    	
        for(int i=0;i<ents.size();i++)
        {
        	Entity e = ents.get(i);
        	if(e == null)
        		continue;
        	if(e instanceof EntityPig && !logic.updated)
        	{
        		System.out.println("returning from render isPig with no update:");
        		return;
        	}
        	else if(e instanceof EntityPig)
        	{
        		if(Config.isDev)
        			System.out.println("why you here you pig:" + TileEntityUtil.getTileNBT(te));
        	}
        	GL11.glPushMatrix();
        	GL11.glTranslatef((float)x + 0.5F, (float)y, (float)z + 0.5F);

        	MobSpawnerItemRender.setLightmapDisabled(false);
        	BlockPos pos = te.getPos();
            setLightMap(e, pos);//set the lighting to the entitie's lighting for glowing textures like blazes
        	renderSpawnerEntity(e,scale,logic.offsets[i],logic,x,y,z,partialTicks,destroyStage,alpha);
            GlStateManager.depthMask(true);
        	GlStateManager.enableRescaleNormal();
        	GL11.glPopMatrix();
        }
        //reset the light map back to the intial coords
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, posX, posY);
    }

	public static void setLightMap(Entity e, BlockPos pos) 
	{
		e.posX = pos.getX();
		e.posY = pos.getY();
		e.posZ = pos.getZ();
		setLightMap(e);
	}

	public static void setLightMap(Entity e) 
	{
	    int i = e.getBrightnessForRender();

        if (e.isBurning())
        {
            i = 15728880;
        }

        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
	}

	public void renderSpawnerEntity(Entity entity, float scale, double offset, MobSpawnerBaseLogic mobSpawnerLogic, double x, double y, double z, float partialTicks, int destroyStage,float alpha) 
	{
        if (entity != null)
        {
            entity.setWorld(mobSpawnerLogic.getSpawnerWorld());
     
            GL11.glTranslatef(0.0F, 0.4F, 0.0F);
            GlStateManager.rotate((float)(mobSpawnerLogic.getPrevMobRotation() + (mobSpawnerLogic.getMobRotation() - mobSpawnerLogic.getPrevMobRotation()) * (double)partialTicks) * 10.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.4F, 0.0F);
            GL11.glScalef(scale, scale, scale);
            entity.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
            if(!mobSpawnerLogic.active || entity instanceof EntityBlaze && !Config.animationSpawner || Config.noPartialTickBlock && !Config.animationSpawner)
            {
            	partialTicks = 0;
            }
            
            Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0D, offset, 0.0D, 0.0F, partialTicks, false);
        }
	}
	
	/**
	 * get the smallest scale based upon a mob stack for rendering
	 */
	public static float getScale(List<Entity> ents, boolean old)
	{
		if(ents.isEmpty())
			return -1;
		float scale = getScale(ents.get(0),old);
		for(Entity e : ents)
		{
			float compare = getScale(e,old);
			if(compare < scale)
				scale = compare;
		}
		return scale;
	}
	
	public static float getScale(Entity entity,boolean old) 
	{
		float old_scale = 0.4375F;
		if(old)
			return old_scale;
		else
		{
	       float new_scale = 0.53125F;
	       float max = Math.max(entity.width, entity.height);

	       if ((double)max > 1.0D)
	       {
	    	   new_scale /= max;
	       }
	       return new_scale;
		}
	}

}