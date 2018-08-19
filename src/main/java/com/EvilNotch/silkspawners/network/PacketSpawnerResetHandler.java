package com.EvilNotch.silkspawners.network;

import com.EvilNotch.lib.minecraft.network.MessegeBase;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;

public class PacketSpawnerResetHandler extends MessegeBase<PacketSpawnerReset>{

	@Override
	public void handleClientSide(PacketSpawnerReset msg, EntityPlayer p) 
	{
		Minecraft.getMinecraft().addScheduledTask(() -> 
		{
			Minecraft mc = Minecraft.getMinecraft();
			TileEntity tile = mc.world.getTileEntity(msg.pos);
			if(!(tile instanceof TileEntityMobSpawner))
			{
				System.out.println("Invalid packet recieved for mobspawner update at pos:" + msg.pos);
				return;
			}
			TileEntityMobSpawner spawner = (TileEntityMobSpawner)tile;
			MobSpawnerBaseLogic logic = spawner.getSpawnerBaseLogic();
			logic.cachedEntity = null;
			logic.cachedEntities.clear();
			logic.placedLast = false;
			System.out.println("packet recieved for mobspawner reset at pos:" + msg.pos);
		});
	}
	@Override
	public void handleServerSide(PacketSpawnerReset a, EntityPlayer b) {}

}
