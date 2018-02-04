package com.EvilNotch.silkspawners;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config {
	public static boolean canDebug = true;
	public static boolean isDev = false;
	public static boolean tooltip_spawncount = true;
	public static boolean tooltip_maxnearbyents = true;
	public static boolean tooltip_delay = true;
	
	public static boolean tooltip_SpawnRange = false;
	public static boolean tooltip_MinSpawnDelay = false;
	public static boolean tooltip_MaxSpawnDelay = false;
	public static boolean tooltip_RequiredPlayerRange = false;
	public static int maxSpawnerName = 35;
	
	public static void loadConfig(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(new File(event.getModConfigurationDirectory(),"silkspawners.cfg") );
		config.load();
		canDebug = config.getBoolean("canDebug", "general", true, "");
		isDev = config.getBoolean("isDev", "general", false, "gives you version on screen");
		maxSpawnerName = config.getInt("maxCharSpawnerName", "general", 35, 0, 100, "");
		
		tooltip_spawncount = config.getBoolean("spawnCount", "tooltip", true, "");
		tooltip_maxnearbyents = config.getBoolean("maxNearbyEntities", "tooltip", true, "");
		tooltip_delay = config.getBoolean("delay", "tooltip", true, "");
		
		tooltip_SpawnRange = config.getBoolean("spawnRange", "tooltip_advanced", true, "");
		tooltip_MinSpawnDelay = config.getBoolean("minSpawnDelay", "tooltip_advanced", true, "");
		tooltip_MaxSpawnDelay = config.getBoolean("maxSpawnDelay", "tooltip_advanced", true, "");
		tooltip_RequiredPlayerRange = config.getBoolean("requiredPlayerRange", "tooltip_advanced", true, "");
		config.save();
	}

}
