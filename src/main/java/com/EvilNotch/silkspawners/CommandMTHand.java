package com.EvilNotch.silkspawners;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import com.EvilNotch.lib.minecraft.EntityUtil;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
		EntityUtil.sendClipBoard("", "",player, "",stack.getItem().getRegistryName().toString());
		writeToClipboard(("" + stack.getTagCompound()),null);
	}
	public static void writeToClipboard(String s, ClipboardOwner owner) 
	{
		if(s == null)
			s = "null";
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    Transferable transferable = new StringSelection(s);
	    clipboard.setContents(transferable, owner);
	}

	@Override
	public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
		return true;
	}

}
