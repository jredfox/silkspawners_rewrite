package com.evilnotch.silkspawners.commands;

import com.evilnotch.silkspawners.MainJava;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public class CommandSpawner extends CommandBase{

	@Override
	public String getName() {
		return "giveSpawner";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/" + getName();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		if(!(sender instanceof EntityPlayer) || args.length == 0)
			return;
		EntityPlayer p = (EntityPlayer)sender;
		ItemStack stack = new ItemStack(Blocks.MOB_SPAWNER, 1);
		
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagCompound data = new NBTTagCompound();
        String id = new ResourceLocation(args[0]).toString();
        data.setString("id",  id);
        nbt.setTag("SpawnData", data);
        nbt.setString("silkTag", id);
        stack.setTagCompound(nbt);
        
        boolean flag = p.inventory.addItemStackToInventory(stack);
        if (flag)
        {
            p.world.playSound((EntityPlayer)null, p.posX, p.posY, p.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((p.getRNG().nextFloat() - p.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            p.inventoryContainer.detectAndSendChanges();
        }
		
	}

}
