package com.evilnotch.silkspawners.asm;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("silkspawners-spawner_core")
@IFMLLoadingPlugin.SortingIndex(1002)
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class SilkCorePlugin implements IFMLLoadingPlugin{
	
	public static boolean hasChecked = false;
	public static boolean hasLib = true;

	@Override
	public String[] getASMTransformerClass() 
	{
		if(!hasChecked)
		{
			try
			{
				Class.forName("com.evilnotch.lib.asm.util.ASMHelper");
			}
			catch(Throwable t)
			{
				t.printStackTrace();
				hasLib = false;
			}
			hasChecked = true;
		}
		return hasLib ? new String[] {"com.evilnotch.silkspawners.asm.SilkTransformer"} : null;
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
