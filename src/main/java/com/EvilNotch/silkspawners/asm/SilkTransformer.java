package com.evilnotch.silkspawners.asm;

import java.util.List;

import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.asm.util.ASMHelper;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.launchwrapper.IClassTransformer;

public class SilkTransformer implements IClassTransformer{
	
	public static final List<String> clazzes = (List<String>)JavaUtil.<String>asArray(new Object[]
	{
	   	"net.minecraft.tileentity.MobSpawnerBaseLogic"
	});

	@Override
	public byte[] transform(String name, String transformedName, byte[] classToTransform) 
	{
	    int index = clazzes.indexOf(transformedName);
	    return index != -1 ? transform(index, classToTransform, FMLCorePlugin.isObf) : classToTransform;
	}
	public static byte[] transform(int index, byte[] classToTransform,boolean obfuscated)
	{
		try
		{
			String inputBase = "assets/silkspawners/asm/" + (obfuscated ? "srg/" : "deob/");
			switch(index)
			{
				case 0:
						System.out.println("TransForming :" + clazzes.get(index));
						return ASMHelper.replaceClass(inputBase + "MobSpawnerBaseLogic");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return classToTransform;
	}

}
