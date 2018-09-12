package com.EvilNotch.silkspawners.asm;

import java.io.IOException;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.EvilNotch.lib.asm.ASMHelper;
import com.EvilNotch.lib.asm.FMLCorePlugin;
import com.EvilNotch.lib.util.JavaUtil;

import net.minecraft.launchwrapper.IClassTransformer;

public class SilkTransformer implements IClassTransformer{
	
	public static final List<String> clazzes = (List<String>)JavaUtil.<String>asArray2(new Object[]
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
			String inputBase = "assets/silkspawners/asm/";
			switch(index)
			{
				case 0:
					if(FMLCorePlugin.isObf)
						return ASMHelper.replaceClass(inputBase + "MobSpawnerBaseLogic");
				break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return classToTransform;
	}

}
