package com.EvilNotch.silkspawners;

import java.io.File;
import java.util.ArrayList;

import com.EvilNotch.lib.util.Line.LineBase;

import net.minecraft.util.ResourceLocation;
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
	public static int maxSpawnerName = 34;
	public static int default_Delay = 20;
	public static boolean tooltip_CustomNames;
	public static boolean tooltip_CustomPos;
	
	public static void loadConfig(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(new File(event.getModConfigurationDirectory(),"silkspawners.cfg") );
		config.load();
		canDebug = config.get("general", "canDebug", true).getBoolean();
		isDev = config.get("general", "isDev", false).getBoolean();
		maxSpawnerName = config.get("general", "maxCharSpawnerName", maxSpawnerName).getInt();
		default_Delay = config.get("general", "defaultDelay", default_Delay).getInt();
		
		tooltip_spawncount = config.get("tooltip_advanced", "spawnCount", true).getBoolean();
		tooltip_maxnearbyents = config.get("tooltip_advanced", "maxNearbyEntities", true).getBoolean();
		tooltip_delay = config.get("tooltip_advanced", "delay", true).getBoolean();
		tooltip_SpawnRange = config.get("tooltip_advanced", "spawnRange", true).getBoolean();
		tooltip_MinSpawnDelay = config.get("tooltip_advanced", "minSpawnDelay", true).getBoolean();
		tooltip_MaxSpawnDelay = config.get("tooltip_advanced", "maxSpawnDelay", true).getBoolean();
		tooltip_RequiredPlayerRange = config.get("tooltip_advanced", "requiredPlayerRange", true).getBoolean();
		tooltip_CustomNames = config.get("tooltip", "nameTag", true).getBoolean(true);
		tooltip_CustomPos = config.get("tooltip", "CustomPos", true).getBoolean();
		config.save();
	}

}
