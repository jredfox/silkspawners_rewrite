package com.EvilNotch.silkspawners.client;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.EvilNotch.silkspawners.Config;
import com.EvilNotch.silkspawners.MainJava;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ToolTipEvent {
	
	@SubscribeEvent
	public void devText(RenderGameOverlayEvent.Text e)
	{
		if(Config.isDev)
			e.getLeft().add(ChatFormatting.DARK_PURPLE + "SilkSpanwers " + MainJava.versionType[2] + ChatFormatting.WHITE + ":" + ChatFormatting.AQUA + MainJava.VERSION);
	}
	
	@SubscribeEvent
	public void spawnerToolTip(ItemTooltipEvent e)
	{
		if(e.getItemStack() == null || e.getItemStack().getTagCompound() == null || !(Block.getBlockFromItem(e.getItemStack().getItem()) instanceof BlockMobSpawner) && !e.getItemStack().getTagCompound().hasKey("silkTag"))
			return;
		List<String> list = e.getToolTip();
		NBTTagCompound nbt = e.getItemStack().getTagCompound();
		nbt = nbt.copy();
		Block b = Block.getBlockFromItem(e.getItemStack().getItem() );
		String jockey_test = MainJava.jockeyString(nbt);
		String jockey  = null;
		if(jockey_test != null)
			jockey = MainJava.TranslateEntity(new ResourceLocation(jockey_test), Minecraft.getMinecraft().world);
		ArrayList<String> advanced = new ArrayList();
//		String entity = MainJava.TranslateEntity(nbt.getCompoundTag("SpawnData").getString("id"),Minecraft.getMinecraft().world);
//		if(jockey != null)
//			entity = jockey + " Jockey";
//		if(entity == null)
//			return;
//		list.set(0, ChatFormatting.WHITE + entity + " " + b.getLocalizedName() );
		boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		if(MainJava.multiIndexSpawner(nbt))
			list.add(ChatFormatting.LIGHT_PURPLE + "SpawnPotentials:" + nbt.getTagList("SpawnPotentials", 10).tagCount());
		
		if(MainJava.isStackCustomPos(nbt))
		{
			list.add(ChatFormatting.AQUA + "Custom Pos Spawner:true");
			if(shift)
			{
			  list.add(ChatFormatting.YELLOW + "offsetX:" + getOffset(nbt,0) );
			  list.add(ChatFormatting.YELLOW + "offsetY:" + getOffset(nbt,1) );
			  list.add(ChatFormatting.YELLOW + "offsetZ:" + getOffset(nbt,2) );
			}
		}
		if(nbt.hasKey("SpawnCount") && Config.tooltip_spawncount)
			advanced.add(ChatFormatting.DARK_AQUA + "SpawnCount:" + nbt.getInteger("SpawnCount"));
		if(nbt.hasKey("MaxNearbyEntities")&& Config.tooltip_maxnearbyents)
			advanced.add(ChatFormatting.DARK_PURPLE + "MaxNearbyEntities:" + nbt.getInteger("MaxNearbyEntities"));
		if(nbt.hasKey("Delay")&& Config.tooltip_delay)
			advanced.add(ChatFormatting.BLUE + "Delay: " + ChatFormatting.YELLOW + nbt.getInteger("Delay"));
		if(nbt.hasKey("SpawnRange") && Config.tooltip_SpawnRange)
			advanced.add(ChatFormatting.DARK_GRAY + "SpawnRange:" + ChatFormatting.WHITE + nbt.getInteger("SpawnRange"));
		if(nbt.hasKey("MinSpawnDelay") && Config.tooltip_MinSpawnDelay)
			advanced.add(ChatFormatting.DARK_GRAY + "MinSpawnDelay:" + ChatFormatting.WHITE + nbt.getInteger("MinSpawnDelay"));
		if(nbt.hasKey("MaxSpawnDelay") && Config.tooltip_MaxSpawnDelay)
			advanced.add(ChatFormatting.DARK_GRAY + "MaxSpawnDelay:" + ChatFormatting.WHITE + nbt.getInteger("MaxSpawnDelay"));
		if(nbt.hasKey("RequiredPlayerRange") && Config.tooltip_RequiredPlayerRange)
			advanced.add(ChatFormatting.DARK_GRAY + "RequiredPlayerRange:" + ChatFormatting.WHITE + nbt.getInteger("RequiredPlayerRange"));
		
		//if enabled and shifing add advanced tooltips
		for(String s : advanced)
			if(shift)
				list.add(s);
		
		if(!shift && advanced.size() > 0)
			list.add(ChatFormatting.DARK_GRAY + "shift advanced:");
	}

	public double getOffset(NBTTagCompound nbt,int index) {
		if(!nbt.getCompoundTag("SpawnData").hasKey("offsets"))
			return -1;
		NBTTagList list = nbt.getCompoundTag("SpawnData").getTagList("offsets", 6);
		return list.getDoubleAt(index);
	}

}
