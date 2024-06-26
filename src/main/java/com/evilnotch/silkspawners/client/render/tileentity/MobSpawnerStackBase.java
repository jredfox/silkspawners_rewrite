package com.evilnotch.silkspawners.client.render.tileentity;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.evilnotch.silkspawners.Config;
import com.evilnotch.silkspawners.MainJava;
import com.evilnotch.silkspawners.client.render.compat.JITL;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MobSpawnerStackBase extends TileEntitySpecialRenderer<TileEntity>{
	
	public static boolean hasJITL = MainJava.hasJITL;
	
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
    	pre();
        GlStateManager.translate((float)offsetX + 0.5F, (float)offsetY, (float)offsetZ + 0.5F);//preset make sure it's lined up rendering inside of the center of the block
        
    	MobSpawnerBaseLogic logic = ((TileEntityMobSpawner)te).getSpawnerBaseLogic();
    	List<Entity> ents = logic.getCachedEntities();
    	
    	float scale = RenderUtil.getBlockScale(ents, !Config.dynamicScalingBlock);
    	float lastX = OpenGlHelper.lastBrightnessX;
    	float lastY = OpenGlHelper.lastBrightnessY;
    	
        for(int i=0;i<ents.size();i++)
        {
        	Entity e = ents.get(i);
        	if(e instanceof EntityPig && !logic.updated)
        	{
        		System.out.println("returning from render isPig with no update:");
        		break;
        	}
        	
        	renderSpawnerEntity(e, scale, logic.offsets[i], logic, partialTicks, lastX, lastY);
        }
        post();
        GlStateManager.popMatrix();
    }

	public void pre()
	{
		if(hasJITL)
			JITL.pre();
	}
	
	public void post() 
	{
		if(hasJITL)
			JITL.post();
	}

	public void renderSpawnerEntity(Entity entity, float scale, Vec3d offset, MobSpawnerBaseLogic mobSpawnerLogic, float partialTicks, float lastX, float lastY) 
	{
		GlStateManager.pushMatrix();
		
        //newer code has the translate down -0.2F missing but, doesn't fully revert the initial 0.4F up so I don't like the newer code and won't be used with silkspawners installed
        GlStateManager.translate(0.0F, 0.4F, 0.0F);
        if(!Config.debugRotation)
        	GlStateManager.rotate((float)(mobSpawnerLogic.getPrevMobRotation() + (mobSpawnerLogic.getMobRotation() - mobSpawnerLogic.getPrevMobRotation()) * (double)partialTicks) * 10.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-30.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, -0.4F, 0.0F);
        GlStateManager.scale(scale, scale, scale);
        
        if(!mobSpawnerLogic.active || !Config.animationSpawner)
        {
          	partialTicks = 0;
        }
            
        RenderUtil.setLightmapDisabledTE(false);
        World world = mobSpawnerLogic.getSpawnerWorld();
        entity.setWorld(world);
        entity.dimension = world.provider.getDimension();
        BlockPos pos = mobSpawnerLogic.getSpawnerPosition();
        entity.moveToBlockPosAndAngles(pos, entity.rotationYaw, entity.rotationPitch);
        
    	if(Config.dynamicLightingBlock)
    	{
    		RenderUtil.setLightMap(entity);//set the lighting to the entity's lighting for glowing textures like blazes
    	}
        
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
        GL11.glEnable(GL11.GL_BLEND);//for people stupid and don't follow 1.8+ rules
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.depthMask(true);
        GlStateManager.enableNormalize();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastX, lastY);
	}
}