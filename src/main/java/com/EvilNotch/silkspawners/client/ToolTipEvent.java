package com.EvilNotch.silkspawners.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.lwjgl.input.Keyboard;

import com.EvilNotch.lib.minecraft.EnumChatFormatting;
import com.EvilNotch.lib.minecraft.events.DynamicTranslationEvent;
import com.EvilNotch.lib.util.Line.LineBase;
import com.EvilNotch.lib.util.simple.PairObj;
import com.EvilNotch.silkspawners.Config;
import com.EvilNotch.silkspawners.ItemSpawner;
import com.EvilNotch.silkspawners.MainJava;
import com.EvilNotch.silkspawners.SpawnerUtil;
import com.EvilNotch.silkspawners.client.render.item.MobSpawnerItemRender;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ToolTipEvent {
	
	public int time = 0;
	@SubscribeEvent
	public void sptick(ClientTickEvent e)
	{
		if(e.phase != Phase.END)
			return;
		if(time >=(20*Config.spawnerCacheItem))
		{
			MobSpawnerItemRender.entsNBT.clear();
			System.out.println("client data size:" + MobSpawnerItemRender.entsNBT.size());
			time = 0;
		}
		time++;
		
		if(Config.animationItemData)
		for(PairObj<List<Entity>,Double[]> pair : MobSpawnerItemRender.entsNBT.values())
		{
			for(Entity ent : pair.obj1)
			{
				ent.ticksExisted++;
			}
		}
			
		if(Config.animationItem)
		for(Entity ent : MobSpawnerItemRender.ents.values())
		{
			ent.ticksExisted++;
		}
	}
    public static int renderTime;
    public static float renderFrame;

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if(event.phase == Phase.END) {
            renderTime++;
        }
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        if(event.phase == Phase.START)
            renderFrame = event.renderTickTime;
    }
    
   public static float getRenderFrame() {
        return renderFrame;
    }
	public static double getRenderTime() {
	    return renderTime + getRenderFrame();
	}
	    
	@SubscribeEvent
	public void devText(RenderGameOverlayEvent.Text e)
	{
		if(Config.isDev)
			e.getLeft().add(ChatFormatting.DARK_PURPLE + "SilkSpanwers " + MainJava.versionType[2] + ChatFormatting.WHITE + ":" + ChatFormatting.AQUA + MainJava.VERSION);
	}
	/**
	 * let other mods override this if needed
	 */
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void translate(DynamicTranslationEvent e)
	{
		if(e.stack.getTagCompound() == null || !e.stack.getTagCompound().hasKey("silkTag"))
			return;
		NBTTagCompound display = e.stack.getTagCompound().getCompoundTag("display");
		if(display.hasKey("Name"))
			return;
		if(!display.hasKey("EntName"))
			return;
		String unlocal = display.getString("EntName");
		String ent = I18n.translateToLocal(unlocal);
		String block = Config.hasCustomName ? Config.spawnerBlockName : I18n.translateToLocal(e.stack.getItem().getUnlocalizedName() + ".name");
		String name = ent;
		if(display.getBoolean("isJockey"))
			name += " " + I18n.translateToLocal("silkspawners.jockey.name");
		if((name + " " + block).length() < Config.maxSpawnerName)
			name += " " + block;
		if(Config.coloredSpawners)
		{
			name = display.getString("EntColor") + name + EnumChatFormatting.RESET;
		}
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
    		list.add(ChatFormatting.AQUA + I18n.translateToLocal("silkspawners.CustomName.name") + ": " + ChatFormatting.YELLOW + custom);
		if(SpawnerUtil.multiIndexSpawner(nbt))
			list.add(ChatFormatting.LIGHT_PURPLE + I18n.translateToLocal("silkspawners.SpawnPotentials.name") +  ":" + nbt.getTagList("SpawnPotentials", 10).tagCount());
		
		if(SpawnerUtil.isStackCurrentCustomPos(nbt))
		{
			if(Config.tooltip_CustomPos)
				list.add(ChatFormatting.AQUA + "Custom Pos Spawner:true");
			 String offset = I18n.translateToLocal("silkspawners.offset.name");
			 advanced.add(ChatFormatting.YELLOW +  offset + "X:" + getOffset(nbt,0) );
			 advanced.add(ChatFormatting.YELLOW +  offset + "Y:" + getOffset(nbt,1) );
			 advanced.add(ChatFormatting.YELLOW +  offset + "Z:" + getOffset(nbt,2) );
		}
		if(nbt.hasKey("SpawnCount") && Config.tooltip_spawncount)
			advanced.add(ChatFormatting.DARK_AQUA + I18n.translateToLocal("silkspawners.SpawnCount.name") + ":" + nbt.getInteger("SpawnCount"));
		if(nbt.hasKey("MaxNearbyEntities")&& Config.tooltip_maxnearbyents)
			advanced.add(ChatFormatting.DARK_PURPLE + I18n.translateToLocal("silkspawners.MaxNearbyEntities.name") + ":" + nbt.getInteger("MaxNearbyEntities"));
		if(nbt.hasKey("Delay")&& Config.tooltip_delay)
			advanced.add(ChatFormatting.BLUE + I18n.translateToLocal("silkspawners.Delay.name") + ": " + ChatFormatting.YELLOW + nbt.getInteger("Delay"));
		if(nbt.hasKey("SpawnRange") && Config.tooltip_SpawnRange)
			advanced.add(ChatFormatting.DARK_GRAY + I18n.translateToLocal("silkspawners.SpawnRange.name") + ":" + ChatFormatting.WHITE + nbt.getInteger("SpawnRange"));
		if(nbt.hasKey("MinSpawnDelay") && Config.tooltip_MinSpawnDelay)
			advanced.add(ChatFormatting.DARK_GRAY + I18n.translateToLocal("silkspawners.MinSpawnDelay.name") + ":" + ChatFormatting.WHITE + nbt.getInteger("MinSpawnDelay"));
		if(nbt.hasKey("MaxSpawnDelay") && Config.tooltip_MaxSpawnDelay)
			advanced.add(ChatFormatting.DARK_GRAY + I18n.translateToLocal("silkspawners.MaxSpawnDelay.name") + ":" + ChatFormatting.WHITE + nbt.getInteger("MaxSpawnDelay"));
		if(nbt.hasKey("RequiredPlayerRange") && Config.tooltip_RequiredPlayerRange)
			advanced.add(ChatFormatting.DARK_GRAY + I18n.translateToLocal("silkspawners.RequiredPlayerRange.name") + ":" + ChatFormatting.WHITE + nbt.getInteger("RequiredPlayerRange"));
		
		//if enabled and shifing add advanced tooltips
		for(String s : advanced)
			if(shift)
				list.add(s);
		
		if(!shift && advanced.size() > 0)
			list.add(ChatFormatting.DARK_GRAY + I18n.translateToLocal("silkspawners.shift_advanced.name") + ":");
	}

	public double getOffset(NBTTagCompound nbt,int index) {
		if(!nbt.getCompoundTag("SpawnData").hasKey("offsets"))
			return -1;
		NBTTagList list = nbt.getCompoundTag("SpawnData").getTagList("offsets", 6);
		return list.getDoubleAt(index);
	}

}
