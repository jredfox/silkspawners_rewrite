package com.evilnotch.silkspawners.compat;

import net.journey.util.EnumHexColor;
import net.journey.util.JourneyBossStatus;

public class JITL {

	public static float healthScale;
	public static int statusBarTime;
	public static boolean hasColorModifier;
	public static String bar;
	public static String bossName;
	public static EnumHexColor stringTextColor;
	public static EnumHexColor stringOutlineColor;

	public static void pre() 
	{
		healthScale = JourneyBossStatus.healthScale;
		statusBarTime = JourneyBossStatus.statusBarTime;
		hasColorModifier = JourneyBossStatus.hasColorModifier;
		bar = JourneyBossStatus.bar;
		bossName = JourneyBossStatus.bossName;
		stringTextColor = JourneyBossStatus.stringTextColor;
		stringOutlineColor = JourneyBossStatus.stringOutlineColor;
	}

	public static void post() 
	{
		JourneyBossStatus.healthScale = healthScale;
		JourneyBossStatus.statusBarTime = statusBarTime;
		JourneyBossStatus.hasColorModifier = hasColorModifier;
		JourneyBossStatus.bar = bar;
		JourneyBossStatus.bossName = bossName;
		JourneyBossStatus.stringTextColor = stringTextColor;
		JourneyBossStatus.stringOutlineColor = stringOutlineColor;
	}

}
