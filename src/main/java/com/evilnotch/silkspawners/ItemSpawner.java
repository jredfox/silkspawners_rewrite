package com.evilnotch.silkspawners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.evilnotch.lib.minecraft.basicmc.auto.creativetab.ICreativeTabHook;
import com.evilnotch.lib.minecraft.basicmc.auto.creativetab.ItemCreativeTabs;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.minecraft.util.NBTUtil;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class ItemSpawner implements ICreativeTabHook{
	
	public ItemSpawner()
	{
		ItemCreativeTabs.register(this);
	}
	
    /**
     * returns a list of items mob spawners with living,livingbase,and non living supported
     * it uses a cache so it doesn't lag each time
     */
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
    	if(!Config.creativeTabSpawners)
    	{
    		return;
    	}
        if(tab == MainJava.tab_living)
        {
        	populateTab(tab, EntityUtil.living, items);
        }
        else if(tab == MainJava.tab_nonliving)
        {
        	populateTab(MainJava.tab_nonliving, EntityUtil.livingbase,items);
        	if(Config.nonLivingTab)
        	{
        		populateTab(tab, EntityUtil.nonliving, items);
        	}
        }
        else if(tab == MainJava.tab_custom)
        {
        	populateCustomSpawnerEntries(items);
        }
        else if(tab == CreativeTabs.SEARCH)
        {
        	populateTab(tab,EntityUtil.living, items);
        	populateTab(tab,EntityUtil.livingbase, items);
        	if(Config.nonLivingTab)
        	{
        		populateTab(tab, EntityUtil.nonliving, items);
        	}
        	populateCustomSpawnerEntries(items);
        }
    }
    
	protected static void populateTab(CreativeTabs redstone, HashMap<ResourceLocation, String[]> living, List<ItemStack> items) 
	{
	       Iterator<Map.Entry<ResourceLocation,String[]>> it = living.entrySet().iterator();
       		NBTTagCompound tags = new NBTTagCompound();
       		new TileEntityMobSpawner().writeToNBT(tags);
       		
	        while(it.hasNext())
	        {
	        	Map.Entry<ResourceLocation, String[]> map = it.next();
	        	ResourceLocation loc = map.getKey();
	        	String[] value = map.getValue();
	        	
	        	ItemStack spawner = new ItemStack(Blocks.MOB_SPAWNER,1);
	        	NBTTagCompound nbt = tags.copy();
	    		nbt.removeTag("x");
				nbt.removeTag("y");
				nbt.removeTag("z");
				nbt.removeTag("id");
				
				//mod support
				MainJava.removeDungeonTweaksTag(nbt);
				
	        	nbt.setString("silkTag", loc.toString());
	        	nbt.setTag("SpawnData", new NBTTagCompound());
	        	NBTTagCompound data = (NBTTagCompound) nbt.getTag("SpawnData");
	        	data.setString("id", loc.toString());
	        	
	        	//generate default tags so spawners stack
	        	nbt.setInteger("Delay", Config.default_Delay);
	        	NBTTagList list = new NBTTagList();
	        	NBTTagCompound entry = new NBTTagCompound();
	        	NBTTagCompound entity = new NBTTagCompound();
	        	entity.setString("id", loc.toString());
	        	entry.setTag("Entity", entity);
	        	entry.setInteger("Weight", 1);
	        	list.appendTag(entry);
	        	nbt.setTag("SpawnPotentials", list);
	        	
	        	//display name
	        	nbt.setTag("display", new NBTTagCompound());
	        	NBTTagCompound display = (NBTTagCompound) nbt.getTag("display");
	        	display.setString("EntName", value[0]);
	        	display.setString("EntColor", Config.colorToConfig.get(value[2]));
	        	
	        	spawner.setTagCompound(nbt);
	        	items.add(spawner);
	        }
	}
	
	public static void populateCustomSpawnerEntries(List<ItemStack> tabList) 
	{
		List<ItemStack> stacks = new ArrayList(17);
		
    	ItemStack skele = new ItemStack(Blocks.MOB_SPAWNER,1);
    	skele.setTagCompound(NBTUtil.getNBTFromString("{MaxNearbyEntities:6s,RequiredPlayerRange:16s,SpawnCount:4s,display:{isJockey:1b,EntName:\"entity.Skeleton.name\",EntColor:\"type_ranged\"},silkTag:\"minecraft:spider\",SpawnData:{Passengers:[{id:\"skeleton\"}],id:\"minecraft:spider\"},MaxSpawnDelay:800s,SpawnRange:4s,Delay:20,MinSpawnDelay:200s,SpawnPotentials:[{Entity:{Passengers:[{id:\"skeleton\"}],id:\"minecraft:spider\"},Weight:1}]}"));
    	
    	ItemStack wither = new ItemStack(Blocks.MOB_SPAWNER,1);
    	wither.setTagCompound(NBTUtil.getNBTFromString("{MaxNearbyEntities:6s,RequiredPlayerRange:16s,SpawnCount:4s,display:{isJockey:1b,EntName:\"entity.WitherSkeleton.name\",EntColor:\"type_ranged\"},silkTag:\"minecraft:spider\",SpawnData:{Passengers:[{id:\"wither_skeleton\"}],id:\"minecraft:spider\"},MaxSpawnDelay:800s,SpawnRange:4s,Delay:20,MinSpawnDelay:200s,SpawnPotentials:[{Entity:{Passengers:[{id:\"wither_skeleton\"}],id:\"minecraft:spider\"},Weight:1}]}"));
    	
    	ItemStack stray = new ItemStack(Blocks.MOB_SPAWNER,1);
    	stray.setTagCompound(NBTUtil.getNBTFromString("{MaxNearbyEntities:6s,RequiredPlayerRange:16s,SpawnCount:4s,display:{isJockey:1b,EntName:\"entity.Stray.name\",EntColor:\"type_ranged\"},silkTag:\"minecraft:spider\",SpawnData:{Passengers:[{id:\"stray\"}],id:\"minecraft:spider\"},MaxSpawnDelay:800s,SpawnRange:4s,Delay:20,MinSpawnDelay:200s,SpawnPotentials:[{Entity:{Passengers:[{id:\"stray\"}],id:\"minecraft:spider\"},Weight:1}]}"));
    	
    	ItemStack skeletrap = new ItemStack(Blocks.MOB_SPAWNER,1);
    	skeletrap.setTagCompound(NBTUtil.getNBTFromString("{MaxNearbyEntities:6s,RequiredPlayerRange:16s,SpawnCount:4s,display:{EntName:\"silkspawners.skeletontrap.name\",EntColor:\"type_ranged\"},silkTag:\"minecraft:skeleton_horse\",SpawnData:{id:\"minecraft:skeleton_horse\",SkeletonTrap:1},MaxSpawnDelay:800s,SpawnRange:4s,Delay:20,MinSpawnDelay:200s,SpawnPotentials:[{Entity:{id:\"minecraft:skeleton_horse\",SkeletonTrap:1},Weight:1}]}"));
    	
    	ItemStack creeper = new ItemStack(Blocks.MOB_SPAWNER,1);
    	creeper.setTagCompound(NBTUtil.getNBTFromString("{MaxNearbyEntities:6s,RequiredPlayerRange:16s,SpawnCount:4s,display:{EntName:\"silkspawners.poweredcreeper.name\",EntColor:\"type_monster\"},silkTag:\"minecraft:creeper\",SpawnData:{id:\"minecraft:creeper\",powered:1},MaxSpawnDelay:800s,SpawnRange:4s,Delay:20,MinSpawnDelay:200s,SpawnPotentials:[{Entity:{id:\"minecraft:creeper\",powered:1},Weight:1}]}"));
    	
    	ItemStack sheep = new ItemStack(Blocks.MOB_SPAWNER,1);
    	sheep.setTagCompound(NBTUtil.getNBTFromString("{MaxNearbyEntities:6s,RequiredPlayerRange:16s,SpawnCount:4s,display:{EntName:\"entity.Sheep.name\",EntColor:\"rainbow\"},silkTag:\"minecraft:sheep\",SpawnData:{CustomName:\"jeb_\",id:\"minecraft:sheep\"},MaxSpawnDelay:800s,SpawnRange:4s,Delay:20,MinSpawnDelay:200s,SpawnPotentials:[{Entity:{CustomName:\"jeb_\",id:\"minecraft:sheep\"},Weight:1}]}"));
    	
    	ItemStack toast = new ItemStack(Blocks.MOB_SPAWNER,1);
    	toast.setTagCompound(NBTUtil.getNBTFromString("{MaxNearbyEntities:6s,RequiredPlayerRange:16s,SpawnCount:4s,display:{EntName:\"entity.Rabbit.name\",EntColor:\"type_creature\"},silkTag:\"minecraft:rabbit\",SpawnData:{CustomName:\"Toast\",id:\"minecraft:rabbit\"},MaxSpawnDelay:800s,SpawnRange:4s,Delay:20,MinSpawnDelay:200s,SpawnPotentials:[{Entity:{CustomName:\"Toast\",id:\"minecraft:rabbit\"},Weight:1}]}"));
    	
    	ItemStack killerrabbit = new ItemStack(Blocks.MOB_SPAWNER,1);
    	killerrabbit.setTagCompound(NBTUtil.getNBTFromString("{MaxNearbyEntities:6s,RequiredPlayerRange:16s,SpawnCount:4s,display:{EntName:\"silkspawners.killerrabbit.name\",EntColor:\"type_monster\"},silkTag:\"minecraft:rabbit\",SpawnData:{RabbitType:99,id:\"minecraft:rabbit\"},MaxSpawnDelay:800s,SpawnRange:4s,Delay:20,MinSpawnDelay:200s,SpawnPotentials:[{Entity:{RabbitType:99,id:\"minecraft:rabbit\"},Weight:1}]}"));
    	
    	ItemStack johnny = new ItemStack(Blocks.MOB_SPAWNER,1);
    	johnny.setTagCompound(NBTUtil.getNBTFromString("{MaxNearbyEntities:6s,RequiredPlayerRange:16s,SpawnCount:4s,display:{EntName:\"entity.VindicationIllager.name\",EntColor:\"type_monster\"},silkTag:\"minecraft:vindication_illager\",SpawnData:{CustomName:\"Johnny\",id:\"minecraft:vindication_illager\",HandItems:[{id:\"minecraft:iron_axe\",Count:1b}]},MaxSpawnDelay:800s,SpawnRange:4s,Delay:20,MinSpawnDelay:200s,SpawnPotentials:[{Entity:{CustomName:\"Johnny\",id:\"minecraft:vindication_illager\",HandItems:[{id:\"minecraft:iron_axe\",Count:1b}]},Weight:1}]}"));

    	ItemStack chicken = new ItemStack(Blocks.MOB_SPAWNER,1);
    	chicken.setTagCompound(NBTUtil.getNBTFromString("{MaxNearbyEntities:6s,RequiredPlayerRange:16s,SpawnCount:4s,display:{isJockey:1b,EntName:\"entity.Chicken.name\",EntColor:\"type_monster\"},silkTag:\"minecraft:chicken\",SpawnData:{Passengers:[{IsBaby:1,id:\"zombie\"}],id:\"minecraft:chicken\"},MaxSpawnDelay:800s,SpawnRange:4s,Delay:20,MinSpawnDelay:200s,SpawnPotentials:[{Entity:{Passengers:[{IsBaby:1,id:\"zombie\"}],id:\"minecraft:chicken\"},Weight:1}]}"));
    	
    	ItemStack chicken2 = new ItemStack(Blocks.MOB_SPAWNER,1);
    	chicken2.setTagCompound(NBTUtil.getNBTFromString("{MaxNearbyEntities:6s,RequiredPlayerRange:16s,SpawnCount:4s,display:{isJockey:1b,EntName:\"entity.Chicken.name\",EntColor:\"type_fire\"},silkTag:\"minecraft:chicken\",SpawnData:{Passengers:[{IsBaby:1,id:\"zombie_pigman\",HandItems:[{id:golden_sword,Count:1,Damage:17},{}] }],id:\"minecraft:chicken\"},MaxSpawnDelay:800s,SpawnRange:4s,Delay:20,MinSpawnDelay:200s,SpawnPotentials:[{Entity:{Passengers:[{IsBaby:1,id:\"zombie_pigman\",HandItems:[{id:golden_sword,Count:1,Damage:17},{}]}],id:\"minecraft:chicken\"},Weight:1}]}"));
    	
    	stacks.add(skele);
    	stacks.add(wither);
    	stacks.add(stray);
    	stacks.add(skeletrap);
    	stacks.add(creeper);
    	stacks.add(sheep);
    	stacks.add(toast);
    	stacks.add(killerrabbit);
    	stacks.add(johnny);
       	stacks.add(chicken);
    	stacks.add(chicken2);
    	
    	for(int i=0;i<6;i++)
    	{
    		ItemStack chickenv = new ItemStack(Blocks.MOB_SPAWNER,1);
    		chickenv.setTagCompound(NBTUtil.getNBTFromString("{MaxNearbyEntities:6s,RequiredPlayerRange:16s,SpawnCount:4s,display:{isJockey:1b,EntName:\"entity.Chicken.name\",EntColor:\"type_monster\"},silkTag:\"minecraft:chicken\",SpawnData:{Passengers:[{IsBaby:1,id:\"zombie_villager\",Profession:" + i + "}],id:\"minecraft:chicken\"},MaxSpawnDelay:800s,SpawnRange:4s,Delay:20,MinSpawnDelay:200s,SpawnPotentials:[{Entity:{Passengers:[{IsBaby:1,id:\"zombie_villager\",Profession:" + i + "}],id:\"minecraft:chicken\"},Weight:1}]}"));
    		stacks.add(chickenv);
    	}

    	for(ItemStack stack : stacks)
    	{
    		stack.getTagCompound().setInteger("Delay", Config.default_Delay);
    		tabList.add(stack);
    	}
	}
}
