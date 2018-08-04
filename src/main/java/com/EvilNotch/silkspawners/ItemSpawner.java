package com.EvilNotch.silkspawners;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.util.JavaUtil;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class ItemSpawner extends ItemBlock{
	
	public CreativeTabs tab = null;

	public ItemSpawner() 
	{
		super(Blocks.MOB_SPAWNER);
		this.setRegistryName(Blocks.MOB_SPAWNER.getRegistryName());
		this.setUnlocalizedName(Blocks.MOB_SPAWNER.getUnlocalizedName());
		this.setCreativeTab(CreativeTabs.REDSTONE);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}
	
    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     * hashmap between resourcelocation,actual client name
     * hashmap between resourcelocation,unloalized name
     */
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (!Config.creativeTabSpawners)
        {
        	return;
        }
        
        if(tab == MainJava.tab_living || tab == CreativeTabs.SEARCH)
        	populateTab(tab,EntityUtil.living,items);
        
        if(tab == MainJava.tab_nonliving || tab == CreativeTabs.SEARCH)
        {
        	populateTab(tab,EntityUtil.livingbase,items);
        	populateTab(tab,EntityUtil.nonliving,items);
        }
    }
	protected void populateTab(CreativeTabs redstone, HashMap<ResourceLocation, String[]> living,NonNullList<ItemStack> items) 
	{
	       Iterator<Map.Entry<ResourceLocation,String[]>> it = living.entrySet().iterator();
	        while(it.hasNext())
	        {
	        	Map.Entry<ResourceLocation, String[]> map = it.next();
	        	ResourceLocation loc = map.getKey();
	        	String[] value = map.getValue();
	        	
	        	ItemStack spawner = new ItemStack(Blocks.MOB_SPAWNER,1);
	        	NBTTagCompound nbt = new NBTTagCompound();
	        	
	        	new TileEntityMobSpawner().writeToNBT(nbt);
	    		nbt.removeTag("x");
				nbt.removeTag("y");
				nbt.removeTag("z");
				nbt.removeTag("id");
				
	        	nbt.setString("silkTag", loc.toString());
	        	nbt.setTag("SpawnData", new NBTTagCompound());
	        	NBTTagCompound data = (NBTTagCompound) nbt.getTag("SpawnData");
	        	data.setString("id", loc.toString());
	        	
	        	//generate default tags so spawners stack
	        	nbt.setShort("SpawnCount", (short)4);
	        	nbt.setShort("MaxNearbyEntities", (short)6);
	        	nbt.setInteger("Delay", Config.default_Delay);
	        	nbt.setShort("SpawnRange", (short)4);
	        	nbt.setShort("MinSpawnDelay", (short)200);
	        	nbt.setShort("MaxSpawnDelay", (short)800);
	        	nbt.setShort("SpawnCount", (short)4);
	        	nbt.setShort("RequiredPlayerRange", (short)16);
	        	NBTTagList list = new NBTTagList();
	        	NBTTagCompound entry = new NBTTagCompound();
	        	NBTTagCompound entity = new NBTTagCompound();
	        	entity.setString("id", loc.toString());
	        	entry.setTag("Entity", entity);
	        	entry.setInteger("Weight", 1);
	        	list.appendTag(entry);
	        	nbt.setTag("SpawnPotentials", list);
	        	
	        	if(MainJava.dungeontweaks)
	        	{
	        		NBTTagCompound caps = nbt.getCompoundTag("ForgeCaps");
	        		if(caps == null)
	        		{
	        			caps = new NBTTagCompound();
	        			nbt.setTag("ForgeCaps", caps);
	        		}
	        		caps.setInteger("dungeontweaks:hasscanned", 1);
	        	}
	        	
	        	//display name
	        	nbt.setTag("display", new NBTTagCompound());
	        	NBTTagCompound display = (NBTTagCompound) nbt.getTag("display");
	        	display.setString("EntName", value[0]);
	        	display.setString("EntColor", value[2]);
	        	
	        	spawner.setTagCompound(nbt);
	        	items.add(spawner);
	        }
	}
}
