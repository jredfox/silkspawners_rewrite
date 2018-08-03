package com.EvilNotch.silkspawners.client.render.tileentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.EvilNotch.lib.util.JavaUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;

public class MobSpawnerStackBase extends TileEntitySpecialRenderer<TileEntity>{
	/**
	 * this has the capability of rendering an entire stack of mobs via the tile entity with 1.9+ passenger support
	 */
    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
    	MobSpawnerBaseLogic logic = ((TileEntityMobSpawner)te).getSpawnerBaseLogic();
    	List<Entity> ents = logic.getCachedEntities();;
        this.setLightmapDisabled(false);

        for(int i=0;i<ents.size();i++)
        {
        	Entity e = ents.get(i);
        	if(e == null)
        		continue;
        	GL11.glPushMatrix();
        	GL11.glTranslatef((float)x + 0.5F, (float)y, (float)z + 0.5F);
        	renderSpawnerEntity(e,logic.offsets[i],logic,x,y,z,partialTicks,destroyStage,alpha);
        	GL11.glPopMatrix();
        }
    }

	public void renderSpawnerEntity(Entity entity,double offset,MobSpawnerBaseLogic mobSpawnerLogic, double x, double y, double z, float partialTicks, int destroyStage,float alpha) 
	{
        if (entity != null)
        {
            entity.setWorld(mobSpawnerLogic.getSpawnerWorld());
            float f1 = 0.4375F;
            GL11.glTranslatef(0.0F, 0.4F, 0.0F);
            GlStateManager.rotate((float)(mobSpawnerLogic.getPrevMobRotation() + (mobSpawnerLogic.getMobRotation() - mobSpawnerLogic.getPrevMobRotation()) * (double)partialTicks) * 10.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.4F, 0.0F);
            GL11.glScalef(f1, f1, f1);
            entity.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
            Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0D, offset, 0.0D, 0.0F, partialTicks, false);
        }
	}

}
