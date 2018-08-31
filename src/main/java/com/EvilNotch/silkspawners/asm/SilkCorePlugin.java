package com.EvilNotch.silkspawners.asm;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class SilkCorePlugin implements IFMLLoadingPlugin{

	@Override
	public String[] getASMTransformerClass() {
		 return new String[] {"com.EvilNotch.silkspawners.asm.SilkTransformer"};
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
