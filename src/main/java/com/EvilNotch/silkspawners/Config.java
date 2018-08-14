package com.EvilNotch.silkspawners;

import java.io.File;
import java.util.ArrayList;

import com.EvilNotch.lib.util.Line.LineBase;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config {
	public static boolean isDev = false;
	
	public static int maxSpawnerName = 34;
	public static int default_Delay = 20;
	
	public static boolean creativeTabSpawners = true;
	public static boolean hasCustomName = false;
	public static String spawnerBlockName = "";
	public static boolean coloredSpawners = true;
	public static boolean renderUseInitSpawn = true;
	public static int slimeSize = 2;
	public static int spawnerCacheItem = 100;
	public static boolean nonLivingTab = false;
	public static boolean animationSpawner = true;
	public static boolean animationItem = true;
	public static boolean mobItemRender = true;
	
	public static boolean tooltip_spawncount = true;
	public static boolean tooltip_maxnearbyents = true;
	public static boolean tooltip_delay = true;
	public static boolean tooltip_SpawnRange = false;
	public static boolean tooltip_MinSpawnDelay = false;
	public static boolean tooltip_MaxSpawnDelay = false;
	public static boolean tooltip_RequiredPlayerRange = false;
	public static boolean tooltip_CustomNames;
	public static boolean tooltip_CustomPos;
	
	public static void loadConfig(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(new File(event.getModConfigurationDirectory(),"silkspawners.cfg") );
		config.load();
		isDev = config.get("general", "isDev", false).getBoolean();
		maxSpawnerName = config.get("general", "maxCharSpawnerName", maxSpawnerName).getInt();
		default_Delay = config.get("general", "defaultDelay", default_Delay).getInt();
		spawnerBlockName =  config.get("general", "spawnerBlockName", spawnerBlockName).getString();
		
		coloredSpawners = config.get("render", "coloredSpawnerNames", coloredSpawners).getBoolean();
		creativeTabSpawners = config.get("render", "creativeTabSpawners", creativeTabSpawners).getBoolean();
		renderUseInitSpawn = config.get("render", "renderUseInitSpawn", renderUseInitSpawn).getBoolean();
		spawnerCacheItem = config.get("render", "spawnerCacheItemTime", spawnerCacheItem).getInt();
		slimeSize = config.get("render", "slimeSize", 2).getInt();
		nonLivingTab = config.get("render", "nonLivingEntsInTab", nonLivingTab).getBoolean();
		animationSpawner = config.get("render", "animationSpawnerBlock", animationSpawner).getBoolean();
		animationItem = config.get("render", "animationSpawnerItem", animationItem).getBoolean();
		mobItemRender = config.get("render", "mobItemRender", mobItemRender).getBoolean();
		if(!LineBase.toWhiteSpaced(spawnerBlockName).equals(""))
			hasCustomName = true;
		
		tooltip_spawncount = config.get("tooltip", "spawnCount", true).getBoolean();
		tooltip_maxnearbyents = config.get("tooltip", "maxNearbyEntities", true).getBoolean();
		tooltip_delay = config.get("tooltip", "delay", true).getBoolean();
		tooltip_SpawnRange = config.get("tooltip", "spawnRange", true).getBoolean();
		tooltip_MinSpawnDelay = config.get("tooltip", "minSpawnDelay", true).getBoolean();
		tooltip_MaxSpawnDelay = config.get("tooltip", "maxSpawnDelay", true).getBoolean();
		tooltip_RequiredPlayerRange = config.get("tooltip", "requiredPlayerRange", true).getBoolean();
		tooltip_CustomNames = config.get("tooltip", "nameTag", true).getBoolean(true);
		tooltip_CustomPos = config.get("tooltip", "CustomPos", true).getBoolean();
		config.save();
	}

}
