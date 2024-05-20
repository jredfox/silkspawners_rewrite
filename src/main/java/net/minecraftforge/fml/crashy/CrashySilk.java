package net.minecraftforge.fml.crashy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.crash.CrashReport;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Copy this class and GuiCrashReport.jar into your project to use
 */
public class CrashySilk {
	
    public static void crash(String msg, Throwable t)
    {
		CrashReport crashreport = CrashReport.makeCrashReport(t, msg);
		crashreport.makeCategory(msg);
		
        File file1 = new File("crash-reports").getAbsoluteFile();
        File file2 = new File(file1, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
        System.out.println(crashreport.getCompleteReport());

        int retVal;
        if (crashreport.getFile() != null)
        {
            System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + crashreport.getFile());
            retVal = -1;
        }
        else if (crashreport.saveToFile(file2))
        {
            System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
            retVal = -1;
        }
        else
        {
            System.out.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            retVal = -2;
        }
        
        exit(retVal);
    }
	
	public static void exit(int i) 
	{
		FMLCommonHandler fml = FMLCommonHandler.instance();
		if(fml != null)
		{
			fml.handleExit(i);
		}
		else
		{
			System.exit(i);
		}
	}

}
