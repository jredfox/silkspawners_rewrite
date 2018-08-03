package com.EvilNotch.silkspawners.client.render.tileentity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
/**
 * this class is a direct port of the 1.7.10 minecraft spawner render. I keep it as a spare
 */
public class MobSpawnerRender extends TileEntitySpecialRenderer<TileEntity>{
	
    public void renderTileEntityAt(TileEntityMobSpawner tile, double x, double y, double z, float partialTicks)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.5F, (float)y, (float)z + 0.5F);
        render(tile.getSpawnerBaseLogic(), x, y, z, partialTicks);
        GL11.glPopMatrix(); 
    }

    public static void render(MobSpawnerBaseLogic mobSpawnerLogic, double posX, double posY, double posZ, float partialTicks)
    {
        Entity entity = mobSpawnerLogic.getCachedEntity();

        if (entity != null)
        {
            entity.setWorld(mobSpawnerLogic.getSpawnerWorld());
            float f1 = 0.4375F;
            GL11.glTranslatef(0.0F, 0.4F, 0.0F);
            GlStateManager.rotate((float)(mobSpawnerLogic.getPrevMobRotation() + (mobSpawnerLogic.getMobRotation() - mobSpawnerLogic.getPrevMobRotation()) * (double)partialTicks) * 10.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.4F, 0.0F);
            GL11.glScalef(f1, f1, f1);
            entity.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
            Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);
        }
    }
    @Override
    public void render(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        this.renderTileEntityAt((TileEntityMobSpawner)te, x, y, z, partialTicks);
    }
}
