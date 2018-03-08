package com.EvilNotch.silkspawners;

import java.io.File;
import java.util.ArrayList;

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
	public static ArrayList<ResourceLocation> cmdBlacklist = new ArrayList();
	
	public static void loadConfig(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(new File(event.getModConfigurationDirectory(),"silkspawners.cfg") );
		config.load();
		String[] vars = config.getStringList("blacklistCMDNames", "translations", new String[]{"\"modid:mobname\""}, "Blacklist for command sender names so it always uses general when translating input with quotes \"modid:mobname\" ");
		for(String s : vars)
		{
			if(s.contains("\""))
				cmdBlacklist.add(new ResourceLocation(parseQuotes(s,0)));
			else
				cmdBlacklist.add(new ResourceLocation(toWhiteSpaced(s)));//for idiots who can't read instructions
		}
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
	public static String parseQuotes(String s, int index) 
	{
		String strid = "";
		int quote = 0;
		for(int i=index;i<s.length();i++)
		{
			if(quote == 2)
				break; //if end of parsing object stop loop and return getParts(strid,":");
			
			if(s.substring(i,i+1).equals("\""))
				quote++;
			if(!s.substring(i,i+1).equals("\"") && quote > 0)
				strid += s.substring(i, i+1);
		}
		return strid;
	}
	public static String toWhiteSpaced(String s)
	{
		return s.replaceAll("\\s+", "");
	}

}
