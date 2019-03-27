package com.evilnotch.silkspawners;

import java.io.File;
import java.util.HashMap;

import com.evilnotch.lib.minecraft.util.EnumChatFormatting;
import com.evilnotch.lib.util.JavaUtil;

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
	
	public static boolean grPosSpawner = true;
	public static boolean grCurrentIndex = false;
	public static boolean grDelay = false;
	
	public static boolean animationSpawner = true;
	public static boolean animationItem = true;
	public static boolean mobItemRender = true;
	public static boolean dynamicScalingBlock = false;
	public static boolean dynamicScalingItem = false;
	public static boolean dynamicLightingItem = true;
	public static boolean dynamicLightingBlock = true;
	public static boolean dynamicPositioning = true;
	
	public static boolean tooltip_spawncount = true;
	public static boolean tooltip_maxnearbyents = true;
	public static boolean tooltip_delay = true;
	public static boolean tooltip_SpawnRange = false;
	public static boolean tooltip_MinSpawnDelay = false;
	public static boolean tooltip_MaxSpawnDelay = false;
	public static boolean tooltip_RequiredPlayerRange = false;
	public static boolean tooltip_CustomNames = false;
	public static boolean tooltip_CustomPos = false;
	
	/**
	 * gets the array list of types then returns them in an order which makes since
	 */
	public static HashMap<String,String> colorToConfig = new HashMap();
	public static HashMap<String,String> configToColor = new HashMap();
	public static String text_boss = EnumChatFormatting.DARK_PURPLE + EnumChatFormatting.BOLD;
	public static String text_tameable = EnumChatFormatting.DARK_BLUE;
	public static String text_ender = EnumChatFormatting.DARK_PURPLE;
	public static String text_areaeffectcloud = EnumChatFormatting.DARK_AQUA;
	public static String text_flying = EnumChatFormatting.YELLOW;
	public static String text_ambient = EnumChatFormatting.DARK_GRAY;
	public static String text_ranged = EnumChatFormatting.DARK_RED;
	public static String text_fire = EnumChatFormatting.GOLD;
	public static String text_water = EnumChatFormatting.AQUA;
	public static String text_monster = EnumChatFormatting.RED;
	public static String text_npc = EnumChatFormatting.GREEN;
	public static String text_creature = EnumChatFormatting.LIGHT_PURPLE;
	public static String text_default = EnumChatFormatting.WHITE;
	
	public static void loadConfig(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(new File(event.getModConfigurationDirectory(),"silkspawners.cfg") );
		config.load();
		isDev = config.get("general", "isDev", false).getBoolean();
		maxSpawnerName = config.get("general", "maxCharSpawnerName", maxSpawnerName).getInt();
		default_Delay = config.get("general", "defaultDelay", default_Delay).getInt();
		spawnerBlockName =  config.get("general", "spawnerBlockName", spawnerBlockName).getString();
		nonLivingTab = config.get("general", "nonLivingEntsInTab", nonLivingTab).getBoolean();
		
		config.addCustomCategoryComment("gamerules", "this is the default gamerule values for when a world is created it's not set in stone here");
		grPosSpawner =  config.get("gamerules", "CustomPosSpawner", grPosSpawner).getBoolean();
		grCurrentIndex =  config.get("gamerules", "MultiIndexCurrent", grCurrentIndex).getBoolean();
		grDelay =  config.get("gamerules", "SpawnerSaveDelay", grDelay).getBoolean();
		
		coloredSpawners = config.get("render", "coloredSpawnerNames", coloredSpawners).getBoolean();
		creativeTabSpawners = config.get("render", "creativeTabSpawners", creativeTabSpawners).getBoolean();
		renderUseInitSpawn = config.get("render", "renderUseInitSpawn", renderUseInitSpawn).getBoolean();
		spawnerCacheItem = config.get("render", "spawnerCacheItemTime", spawnerCacheItem).getInt();
		slimeSize = config.get("render", "slimeSize", 2).getInt();
		animationSpawner = config.get("render", "animationSpawnerBlock", animationSpawner).getBoolean();
		animationItem = config.get("render", "animationSpawnerItem", animationItem).getBoolean();
		mobItemRender = config.get("render", "mobItemRender", mobItemRender).getBoolean();
		dynamicScalingBlock = config.get("render", "dynamicScalingBlock", dynamicScalingBlock).getBoolean();
		dynamicScalingItem = config.get("render", "dynamicScalingItem", dynamicScalingItem).getBoolean();
		dynamicLightingItem = config.get("render", "dynamicLightingItem", dynamicLightingItem).getBoolean();
		dynamicLightingBlock = config.get("render", "dynamicLightingBlock", dynamicLightingBlock).getBoolean();
		dynamicPositioning = config.get("render", "dynamicPositioning", dynamicPositioning).getBoolean();
		if(!JavaUtil.toWhiteSpaced(spawnerBlockName).equals(""))
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
		
		text_boss = config.get("text", "type_boss", text_boss).getString();
		text_tameable = config.get("text", "type_tameable", text_tameable).getString();
		text_ender = config.get("text", "type_ender", text_ender).getString();
		text_areaeffectcloud = config.get("text", "type_areaeffectcloud", text_areaeffectcloud).getString();
		text_flying =  config.get("text", "type_flying", text_flying).getString();
		text_ambient =  config.get("text", "type_ambient", text_ambient).getString();
		text_ranged =  config.get("text", "type_ranged", text_ranged).getString();
		text_fire =  config.get("text", "type_fire", text_fire).getString();
		text_water =  config.get("text", "type_water", text_water).getString();
		text_monster = config.get("text", "type_monster", text_monster).getString();
		text_npc =  config.get("text", "type_npc", text_npc).getString();
		text_creature = config.get("text", "type_creature", text_creature).getString();
		text_default =  config.get("text", "type_default", text_default).getString();
		
		colorToConfig.put(EnumChatFormatting.DARK_PURPLE + EnumChatFormatting.BOLD, "type_boss");
		colorToConfig.put(EnumChatFormatting.DARK_BLUE, "type_tameable");
		colorToConfig.put(EnumChatFormatting.DARK_PURPLE, "type_ender");
		colorToConfig.put(EnumChatFormatting.DARK_AQUA, "type_areaeffectcloud");
		colorToConfig.put(EnumChatFormatting.YELLOW, "type_flying");
		colorToConfig.put(EnumChatFormatting.DARK_GRAY, "type_ambient");
		colorToConfig.put(EnumChatFormatting.DARK_RED, "type_ranged");
		colorToConfig.put(EnumChatFormatting.GOLD, "type_fire");
		colorToConfig.put(EnumChatFormatting.AQUA, "type_water");
		colorToConfig.put(EnumChatFormatting.RED, "type_monster");
		colorToConfig.put(EnumChatFormatting.GREEN, "type_npc");
		colorToConfig.put(EnumChatFormatting.LIGHT_PURPLE, "type_creature");
		colorToConfig.put(EnumChatFormatting.WHITE, "type_default");
		
		configToColor.put("type_boss", Config.text_boss);
		configToColor.put("type_tameable", Config.text_tameable);
		configToColor.put("type_ender", Config.text_ender);
		configToColor.put("type_areaeffectcloud", Config.text_areaeffectcloud);
		configToColor.put("type_flying", Config.text_flying);
		configToColor.put("type_ambient", Config.text_ambient);
		configToColor.put("type_ranged", Config.text_ranged);
		configToColor.put("type_fire", Config.text_fire);
		configToColor.put("type_water", Config.text_water);
		configToColor.put("type_monster", Config.text_monster);
		configToColor.put("type_npc", Config.text_npc);
		configToColor.put("type_creature", Config.text_creature);
		configToColor.put("type_default", Config.text_default);
		
		config.save();
	}
}
