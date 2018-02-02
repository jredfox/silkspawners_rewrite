package com.EvilNotch.silkspawners.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.EvilNotch.silkspawners.MainJava;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ToolTipEvent {
	
	@SubscribeEvent
	public void devText(RenderGameOverlayEvent.Text e)
	{
		if(MainJava.isDev)
			e.getLeft().add(ChatFormatting.DARK_PURPLE + "SilkSpanwers " + MainJava.versionType[0] + ChatFormatting.WHITE + ":" + ChatFormatting.AQUA + MainJava.VERSION);
	}
	
	@SubscribeEvent
	public void dev(ItemTooltipEvent e)
	{
		if(e.getItemStack() == null || e.getItemStack().getTagCompound() == null || !(Block.getBlockFromItem(e.getItemStack().getItem()) instanceof BlockMobSpawner) )
			return;
		List<String> list = e.getToolTip();
		NBTTagCompound nbt = e.getItemStack().getTagCompound();
		nbt = nbt.copy();
		Block b = Block.getBlockFromItem(e.getItemStack().getItem() );
		String jockey  = MainJava.TranslateEntity(MainJava.jockeyString(nbt), Minecraft.getMinecraft().world);
//		String entity = MainJava.TranslateEntity(nbt.getCompoundTag("SpawnData").getString("id"),Minecraft.getMinecraft().world);
//		if(jockey != null)
//			entity = jockey + " Jockey";
//		if(entity == null)
//			return;
//		list.set(0, ChatFormatting.WHITE + entity + " " + b.getLocalizedName() );
		if(MainJava.multiIndexSpawner(nbt))
			list.add(ChatFormatting.LIGHT_PURPLE + "SpawnPotentials:" + nbt.getTagList("SpawnPotentials", 10).tagCount());
		
		if(MainJava.isStackCustomPos(nbt))
		{
			list.add(ChatFormatting.AQUA + "Custom Pos Spawner:true");
			int x = nbt.getInteger("x");
			int y = nbt.getInteger("y");
			int z = nbt.getInteger("z");
			list.add(ChatFormatting.YELLOW + "X:" + x + getOffset(nbt,0,x) );
			list.add(ChatFormatting.YELLOW + "Y:" + y + getOffset(nbt,1,y));
			list.add(ChatFormatting.YELLOW + "Z:" + z + getOffset(nbt,2,z));
		}
	}

	public String getOffset(NBTTagCompound nbt,int index,int ox) {
		if(!nbt.getCompoundTag("SpawnData").hasKey("Pos"))
			return "";
		NBTTagList list = nbt.getCompoundTag("SpawnData").getTagList("Pos", 6);
		double p = list.getDoubleAt(index);
		BigDecimal pos = new BigDecimal("" + p);
		BigDecimal oldx = new BigDecimal("" + ox);
		BigDecimal newx = new BigDecimal("" + ox);
		BigDecimal offset = oldx.subtract(pos).multiply(new BigDecimal("-1") );
		return " offset:" + offset;
	}

}
