package com.evilnotch.silkspawners.client.render.tileentity;

import java.util.List;

import com.evilnotch.lib.minecraft.util.TileEntityUtil;
import com.evilnotch.silkspawners.Config;
import com.evilnotch.silkspawners.EntityPos;
import com.evilnotch.silkspawners.client.render.util.RenderUtil;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;

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
    	
    	GlStateManager.pushMatrix();
        GlStateManager.translate((float)offsetX + 0.5F, (float)offsetY, (float)offsetZ + 0.5F);//preset make sure it's lined up rendering inside of the center of the block
        
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
        		break;
        	}
        	else if(e instanceof EntityPig)
        	{
        		if(com.evilnotch.lib.main.Config.debug)
        			System.out.println("why you here you pig:" + TileEntityUtil.getTileNBT(te));
        	}
        	
        	renderSpawnerEntity(e, scale, logic.offsets[i], logic, partialTicks, lastX, lastY);
        }
        GlStateManager.popMatrix();
    }

	public void renderSpawnerEntity(Entity entity, float scale, EntityPos offset, MobSpawnerBaseLogic mobSpawnerLogic, float partialTicks, float lastX, float lastY) 
	{
		GlStateManager.pushMatrix();
		
        RenderUtil.setLightmapDisabled(false);
        entity.setWorld(mobSpawnerLogic.getSpawnerWorld());
        
        //newer code has the translate down -0.2F missing but, doesn't fully revert the initial 0.4F up so I don't like the newer code and won't be used with silkspawners installed
        GlStateManager.translate(0.0F, 0.4F, 0.0F);
        GlStateManager.rotate((float)(mobSpawnerLogic.getPrevMobRotation() + (mobSpawnerLogic.getMobRotation() - mobSpawnerLogic.getPrevMobRotation()) * (double)partialTicks) * 10.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-30.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, -0.4F, 0.0F);
        GlStateManager.scale(scale, scale, scale);
        
        if(!mobSpawnerLogic.active || !Config.animationSpawner)
        {
          	partialTicks = 0;
        }
            
        BlockPos pos = mobSpawnerLogic.getSpawnerPosition();
        
        if(Config.dynamicSetPositioning || Config.dynamicLightingBlock)
        {
        	entity.moveToBlockPosAndAngles(pos, entity.rotationYaw, entity.rotationPitch);
        }
        
    	if(Config.dynamicLightingBlock)
    	{
    		RenderUtil.setLightMap(entity);//set the lighting to the entitie's lighting for glowing textures like blazes
    	}
    	
        if(!Config.dynamicSetPositioning)
        {
        	entity.setLocationAndAngles(0, 0, 0, entity.rotationYaw, entity.rotationPitch);
        }
        
        entity.setRotationYawHead(0.0F);//fix head bugs
        RenderUtil.renderEntity(entity, offset.x, offset.y, offset.z, 0.0F, partialTicks, Config.renderShadows);
        resetOpenGl(lastX, lastY);
        
        GlStateManager.popMatrix();
	}
	
	/**
	 * store the lastX and lastY before rendering entity then call this after
	 */
	public void resetOpenGl(float lastX, float lastY) 
	{   
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.depthMask(true);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastX, lastY);
	}
}