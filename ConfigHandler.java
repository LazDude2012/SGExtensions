package sgextensions;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ConfigHandler
{
	public static int blockDiallerID;
	public static int blockPowererID;

	public static void loadConfig(FMLPreInitializationEvent e){
		Configuration config = new Configuration(e.getSuggestedConfigurationFile());
		config.load();

		Property dialler;
		dialler = config.getBlock("DialComputerID",3745,"The Block ID for the Dialling Computer.");
		blockDiallerID=dialler.getInt();
		Property powerInterface;
		powerInterface = config.getBlock("PowerInterfaceID",3746,"The BlockID for the Gate Power Interface.");
		blockPowererID = powerInterface.getInt();

		config.save();
	}
}
