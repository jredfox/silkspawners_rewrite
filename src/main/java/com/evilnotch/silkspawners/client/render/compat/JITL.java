package com.evilnotch.silkspawners.client.render.compat;

import com.evilnotch.silkspawners.client.render.util.RenderUtil;

import net.journey.util.EnumHexColor;
import net.journey.util.JourneyBossStatus;

public class JITL {

	//since beginning of 1.12.2 it seems like (2019+)
	public static float healthScale;
	public static int statusBarTime;
	public static boolean hasColorModifier;
	public static String bar;
	
	//latest additions
	public static String bossName;
	public static EnumHexColor stringTextColor;
	public static EnumHexColor stringOutlineColor;
	public static boolean hasName, hasText, hasOutline;
	
	static
	{
		hasName = RenderUtil.getField(null, JourneyBossStatus.class, "bossName") != null;
		hasText = RenderUtil.getField(null, JourneyBossStatus.class, "stringTextColor") != null;
		hasOutline = RenderUtil.getField(null, JourneyBossStatus.class, "stringOutlineColor") != null;
	}

	public static void pre() 
	{
		healthScale = JourneyBossStatus.healthScale;
		statusBarTime = JourneyBossStatus.statusBarTime;
		hasColorModifier = JourneyBossStatus.hasColorModifier;
		bar = JourneyBossStatus.bar;
		
		if(hasName)
			bossName = JourneyBossStatus.bossName;
		if(hasText)
			stringTextColor = JourneyBossStatus.stringTextColor;
		if(hasOutline)
			stringOutlineColor = JourneyBossStatus.stringOutlineColor;
	}

	public static void post() 
	{
		JourneyBossStatus.healthScale = healthScale;
		JourneyBossStatus.statusBarTime = statusBarTime;
		JourneyBossStatus.hasColorModifier = hasColorModifier;
		JourneyBossStatus.bar = bar;
		
		if(hasName)
			JourneyBossStatus.bossName = bossName;
		if(hasText)
			JourneyBossStatus.stringTextColor = stringTextColor;
		if(hasOutline)
			JourneyBossStatus.stringOutlineColor = stringOutlineColor;
	}

}
