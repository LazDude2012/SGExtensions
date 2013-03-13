package sgextensions;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ConfigHandler
{
	public static int blockDiallerID;
	public static int blockPowererID;
	public static boolean regenOres;
	public static boolean addOres;
	public static int bcPower;
	public static int icPower;
	public static int fuelAm;
	public static int maxOpen;
	public static boolean irisKillClear;
	public static int blockSGRingID;
	public static int blockSGBaseID;
	public static int blockSGPortalID;
	public static int blockSGControllerID;
	public static int blockOreNaquadahID;
	public static int blockNaquadahID;
	public static int itemNaquadahID;
	public static int itemNaqIngotID;
	public static int itemCrystalCoreID;
	public static int itemCrystalControlID;

	public static void loadConfig(FMLPreInitializationEvent e)
	{
		Configuration config = new Configuration(e.getSuggestedConfigurationFile());
		config.load();

		Property dialler = config.getBlock("DialComputerID", 3745, "The Block ID for the Dialling Computer.");
		blockDiallerID = dialler.getInt();

		Property powerInterface = config.getBlock("PowerInterfaceID", 3746, "The BlockID for the Gate Power Interface.");
		blockPowererID = powerInterface.getInt();

		Property oreRegen = config.get("WorldGen","OreRegen",false);
		regenOres = oreRegen.getBoolean(false);
		
		Property addOresToWorld = config.get("WorldGen","AddOres",true);
		addOres = addOresToWorld.getBoolean(true);
		
		Property bcPowerFuel = config.get("Power Configuration","BC Power per Fuel Value",120);
		bcPower = bcPowerFuel.getInt();
		
		Property icPowerFuel = config.get("Power Configuration","IC2 Power per Fuel Value",300);
		icPower = icPowerFuel.getInt();
		
		Property fuelAmount = config.get("Power Configuration","Fuel Values per Naquadah",20*60*20);
		fuelAm = fuelAmount.getInt();
		
		Property maxOpenT = config.get("Gate Configuration","Maximum open time (Minutes)",38);
		maxOpen = maxOpenT.getInt();
		
		Property irisClear = config.get("Gate Configuration","Iris kill clears inventory",true);
		irisKillClear = irisClear.getBoolean(true);
		
		Property ring= config.getBlock("SGRingID",3747,"The BlockID for the SG Ring and Chevron Blocks.");
		blockSGRingID = ring.getInt();

		Property base = config.getBlock("SGBaseID",3748,"The BlockID for the SG Base Block.");
		blockSGBaseID = base.getInt();

		Property portal = config.getBlock("SGPortalID",3749,"The BlockID for the SG Portal Block.");
		blockSGPortalID = portal.getInt();

		Property controller = config.getBlock("SGControlID",3750,"The BlockID for the DHD.");
		blockSGControllerID = controller.getInt();

		Property oreNaquadah = config.getBlock("SGOreNaquadahID",3751,"The BlockID for the Naquadah Ore.");
		blockOreNaquadahID = oreNaquadah.getInt();

		Property blockNaquadah = config.getBlock("SGNaquadahBlockID",3752,"The BlockID for the Naquadah block.");
		blockNaquadahID = blockNaquadah.getInt();

		Property itemNaquadah = config.getItem("SGNaquadahID",5501,"The ItemID for the Naquadah item.");
		itemNaquadahID = itemNaquadah.getInt();

		Property itemNaqIngot = config.getItem("SGNaqIngotID",5502,"The ItemID for the Naquadah Ingot item.");
		itemNaqIngotID = itemNaqIngot.getInt();

		Property crystalCore = config.getItem("SGCrystalCoreID",5503,"The ItemID for the Stargate Core Crystal.");
		itemCrystalCoreID = crystalCore.getInt();

		Property crystalControl = config.getItem("SGCrystalControlID",5504,"The ItemID for the Stargate Controller Crystal.");
		itemCrystalControlID = crystalControl.getInt();
		
		

		config.save();
	}

}
