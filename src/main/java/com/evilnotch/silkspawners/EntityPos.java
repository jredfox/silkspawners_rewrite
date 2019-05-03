package com.evilnotch.silkspawners;

import com.evilnotch.lib.minecraft.util.EntityUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

/**
 * cache an entities pos, yaw and pitch then apply them later
 * @author jredfox
 */
public class EntityPos{
	
	public double x;
	public double y;
	public double z;
	public float yaw;
	public float pitch;
	public float yawHead;
	public float renderYawOffset;
	public double motionX;
	public double motionY;
	public double motionZ;
	
	public EntityPos(Entity entity)
	{
		this.x = entity.posX;
		this.y = entity.posY;
		this.z = entity.posZ;
		this.yaw = entity.rotationYaw;
		this.pitch = entity.rotationPitch;
		this.yawHead = entity.getRotationYawHead();
		this.motionX = entity.motionX;
		this.motionY = entity.motionY;
		this.motionZ = entity.motionZ;
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase living = (EntityLivingBase)entity;
			this.renderYawOffset = living.renderYawOffset;
		}
		else
		{
			this.renderYawOffset = this.yaw;
		}
	}
	
	public void applyPos(Entity entity)
	{
		entity.setLocationAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
		entity.setRenderYawOffset(this.renderYawOffset);
		entity.setRotationYawHead(this.yawHead);
		entity.motionX = this.motionX;
		entity.motionY = this.motionY;
		entity.motionZ = this.motionZ;

		//prevs
		entity.prevRotationYaw = entity.rotationYaw;
		entity.prevRotationPitch = entity.rotationPitch;
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase living = (EntityLivingBase) entity;
			living.prevRenderYawOffset = living.renderYawOffset;
			living.prevRotationYawHead = living.rotationYawHead;
			
			//additional code not sure if really needed or not
			living.prevCameraPitch = living.cameraPitch;
			living.prevSwingProgress = living.swingProgress;
			living.prevLimbSwingAmount = living.limbSwingAmount;
		}
	}

}
