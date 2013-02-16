package sgextensions;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ConfigHandler
{
	public static int blockDiallerID;

	public static void loadConfig(FMLPreInitializationEvent e){
		Configuration config = new Configuration(e.getSuggestedConfigurationFile());
		config.load();

		Property dialler;
		dialler = config.getBlock("DialComputerID",3745,"The Block ID for the Dialling Computer.");

		config.save();
	}
}
