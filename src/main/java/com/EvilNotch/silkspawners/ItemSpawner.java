package com.EvilNotch.silkspawners;

import java.util.Collections;
import java.util.List;

import com.EvilNotch.lib.minecraft.EntityUtil;
import com.EvilNotch.lib.util.JavaUtil;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
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
	@Override
    public CreativeTabs getCreativeTab()
    {
    	return this.tab;
    }
	@Override
	public Item setCreativeTab(CreativeTabs tab)
	{
		this.tab = tab;
		return this;
	}
    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     * hashmap between resourcelocation,actual client name
     * hashmap between resourcelocation,unloalized name
     */
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (!this.isInCreativeTab(tab) || !Config.creativeTabSpawners)
        {
        	return;
        }
        
        List<ResourceLocation> li = JavaUtil.asList(net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES.getKeys());
        Collections.sort(li);//Alphabetize them first
        
        for(ResourceLocation loc :  li)
        {
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
        	display.setString("EntName", EntityUtil.getUnlocalizedName(data, com.EvilNotch.lib.main.MainJava.fake_world));
        	
        	spawner.setTagCompound(nbt);
        	items.add(spawner);
        }
    }
	
}
