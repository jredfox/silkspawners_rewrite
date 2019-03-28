package com.evilnotch.silkspawners.client.render.tileentity;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.evilnotch.silkspawners.client.render.item.MobSpawnerItemRender;
import com.evilnotch.silkspawners.client.render.util.RenderUtil;
import com.evilnotch.iitemrender.handlers.IItemRendererHandler;
import com.evilnotch.lib.minecraft.util.TileEntityUtil;
import com.evilnotch.silkspawners.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MobSpawnerStackBase extends TileEntitySpecialRenderer<TileEntity>{
	
	/**
	 * this has the capability of rendering an entire stack of mobs via the tile entity with 1.9+ passenger support
	 */
    @Override
    public void render(TileEntity te, double offsetX, double offsetY, double offsetZ, float partialTicks, int destroyStage, float alpha)
    {
    	if(te.getWorld() == null)
    	{
    		return;
    	}
    	
    	MobSpawnerBaseLogic logic = ((TileEntityMobSpawner)te).getSpawnerBaseLogic();
    	List<Entity> ents = logic.getCachedEntities();
    	
    	float scale = RenderUtil.getBlockScale(ents, !Config.dynamicScalingBlock);
    	float lastX = OpenGlHelper.lastBrightnessX;
    	float lastY = OpenGlHelper.lastBrightnessY;
    	
        for(int i=0;i<ents.size();i++)
        {
        	Entity e = ents.get(i);
        	if(e == null)
        	{
        		continue;
        	}
        	else if(e instanceof EntityPig && !logic.updated)
        	{
        		System.out.println("returning from render isPig with no update:");
        		return;
        	}
        	else if(e instanceof EntityPig)
        	{
        		if(com.evilnotch.lib.main.Config.debug)
        			System.out.println("why you here you pig:" + TileEntityUtil.getTileNBT(te));
        	}
        	
        	renderSpawnerEntity(e, scale, logic.offsets[i], logic, offsetX, offsetY, offsetZ, partialTicks, lastX, lastY);
        }
    }

	public void renderSpawnerEntity(Entity entity, float scale, double offset, MobSpawnerBaseLogic mobSpawnerLogic, double offsetX, double offsetY, double offsetZ, float partialTicks, float lastX, float lastY) 
	{
		GL11.glPushMatrix();
		
        RenderUtil.setLightmapDisabled(false);
    	if(Config.dynamicLightingBlock)
    	{
    		RenderUtil.setLightMap(entity, mobSpawnerLogic.getSpawnerPosition());//set the lighting to the entitie's lighting for glowing textures like blazes
    	}
    	
        entity.setWorld(mobSpawnerLogic.getSpawnerWorld());
        
    	GL11.glTranslatef((float)offsetX + 0.5F, (float)offsetY, (float)offsetZ + 0.5F);
        GL11.glTranslatef(0.0F, 0.4F, 0.0F);
        GlStateManager.rotate((float)(mobSpawnerLogic.getPrevMobRotation() + (mobSpawnerLogic.getMobRotation() - mobSpawnerLogic.getPrevMobRotation()) * (double)partialTicks) * 10.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.0F, -0.4F, 0.0F);
        GL11.glScalef(scale, scale, scale);
        
        if(!mobSpawnerLogic.active || !Config.animationSpawner)
        {
          	partialTicks = 0;
        }
            
        BlockPos pos = mobSpawnerLogic.getSpawnerPosition();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if(Config.dynamicSetPositioning)
        {
        	entity.moveToBlockPosAndAngles(pos, entity.rotationYaw, entity.rotationPitch);
        	entity.setLocationAndAngles(x + 0.5D, y, z + 0.5D, entity.rotationYaw, entity.rotationPitch);
        }
        else
        {
        	entity.setLocationAndAngles(0, 0, 0, entity.rotationYaw, entity.rotationPitch);
        }
        entity.setRotationYawHead(0.0F);//fix head bugs
        RenderUtil.renderEntity(entity, 0.0D, offset, 0.0D, 0.0F, partialTicks, Config.renderShadows);
        resetOpenGl(lastX, lastY);
        
    	GL11.glPopMatrix();
	}
	
	/**
	 * store the lastX and lastY before rendering entity then call this after
	 */
	public void resetOpenGl(float lastX, float lastY) 
	{   
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.depthMask(true);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastX, lastY);
        IItemRendererHandler.restoreLastBlurMipmap();
	}
}