package com.evilnotch.silkspawners.commands;

import com.evilnotch.lib.minecraft.util.PlayerUtil;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.IClientCommand;

public class CommandMTHand extends CommandBase implements IClientCommand{

	@Override
	public String getName() {
		return "mtHand";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/" + getName();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		EntityPlayer player = (EntityPlayer) sender;
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		NBTTagCompound nbt = stack.getTagCompound();
		PlayerUtil.sendClipBoard(player, "", stack.getItem().getRegistryName().toString());
		if(nbt != null)
			PlayerUtil.copyClipBoard(player, stack.getTagCompound().toString());
	}

	@Override
	public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
		return true;
	}

}
