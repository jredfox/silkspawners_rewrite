package com.EvilNotch.silkspawners.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.lwjgl.input.Keyboard;

import com.EvilNotch.lib.minecraft.events.DynamicTranslationEvent;
import com.EvilNotch.silkspawners.Config;
import com.EvilNotch.silkspawners.MainJava;
import com.EvilNotch.silkspawners.SpawnerUtil;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.translation.I18n;
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
	public void translate(DynamicTranslationEvent e)
	{
		if(e.stack.getTagCompound() == null || !e.stack.getTagCompound().hasKey("silkTag"))
			return;
		NBTTagCompound display = e.stack.getTagCompound().getCompoundTag("display");
		if(!display.hasKey("EntName"))
			return;
		String unlocal = display.getString("EntName");
		String ent = I18n.translateToLocal(unlocal);
		String block = I18n.translateToLocal(e.stack.getItem().getUnlocalizedName() + ".name");
		String name = ent;
		if(display.getBoolean("isJockey"))
			name += " " + I18n.translateToLocal("silkspawners.jockey.name");
		if((name + " " + block).length() < Config.maxSpawnerName)
			name += " " + block;
		e.translation = name;
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
		boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		ArrayList<String> advanced = new ArrayList();
		
		NBTTagCompound data = nbt.getCompoundTag("SpawnData");
		String custom = data.getString("CustomName");
		if(Config.tooltip_CustomNames && Strings.isNotEmpty(custom))
    		list.add(ChatFormatting.AQUA + "CustomName: " + ChatFormatting.YELLOW + custom);
		if(SpawnerUtil.multiIndexSpawner(nbt))
			list.add(ChatFormatting.LIGHT_PURPLE + "SpawnPotentials:" + nbt.getTagList("SpawnPotentials", 10).tagCount());
		
		if(SpawnerUtil.isStackCurrentCustomPos(nbt))
		{
			if(Config.tooltip_CustomPos)
				list.add(ChatFormatting.AQUA + "Custom Pos Spawner:true");
			 advanced.add(ChatFormatting.YELLOW + "offsetX:" + getOffset(nbt,0) );
			 advanced.add(ChatFormatting.YELLOW + "offsetY:" + getOffset(nbt,1) );
			 advanced.add(ChatFormatting.YELLOW + "offsetZ:" + getOffset(nbt,2) );
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
