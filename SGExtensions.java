package sgextensions;

import ic2.api.Ic2Recipes;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = "SGExtensions", name = "SG Darkcraft Edition", version = "pre1")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class SGExtensions
{
	@SidedProxy(clientSide = "sgextensions.ClientProxy", serverSide = "sgextensions.CommonProxy")
	public static CommonProxy proxy;
    @Mod.Instance
    public static SGExtensions instance = new SGExtensions();
	public static GuiHandler guiHandler = new GuiHandler();

	public static SGChannel channel;
	public static BaseTEChunkManager chunkManager;

	public static Block sgBaseBlock;
	public static Block sgRingBlock;
	public static Block sgControllerBlock;
	public static Block sgPortalBlock;
	public static Block naquadahBlock, naquadahOre;

	public static Item naquadah, hardNaquadah, naquadahIngot, sgCoreCrystal, sgControllerCrystal;
	public static Item sgDarkUpgrades, sgHardFuel;
	
	public static int safeFuelMod = 4;
	public static int quickFuelMod = 10;

	public static boolean addOresToExistingWorlds;
	public static boolean addOres;
	public static boolean irisKillClearInv;
	
	public static int irisFrames = 10;
	
	public static int bcPowerPerFuel;
	public static int icPowerPerFuel;
	public static int fuelAmount;
	public static int fuelStore = 50;
	public static int maxOpenTime;
	
	public static boolean gateHardMode;
	public static boolean fuelHardMode;
	public static ItemStack stargateFuel;

	public static NaquadahOreWorldGen naquadahOreGenerator;
	public static Block diallerBlock;
	public static Block sgDarkPowerBlock;
	public static final int GUIELEMENT_GATE = 1;
	public static final int GUIELEMENT_DHD = 2;

	@Mod.PreInit()
	public void preInit(FMLPreInitializationEvent e)
	{
		ConfigHandler.loadConfig(e);
	}

	@Mod.Init
	public void load(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
		proxy.registerRenderThings();
		channel = new SGChannel(Info.modID);
		chunkManager = new BaseTEChunkManager(this);
		addOresToExistingWorlds = ConfigHandler.regenOres;
		addOres = ConfigHandler.addOres;
		bcPowerPerFuel = ConfigHandler.bcPower;
		icPowerPerFuel = ConfigHandler.icPower;
		fuelAmount = ConfigHandler.fuelAm;
		maxOpenTime = ConfigHandler.maxOpen;
		irisKillClearInv = ConfigHandler.irisKillClear;
		fuelHardMode = ConfigHandler.fuelHardMode;
		gateHardMode = ConfigHandler.gateHardMode;
		
		registerItems();
        registerBlocks();
        registerDarkRecipes();
        registerRandomItems();
        registerTradeHandlers();
        registerWorldGenerators();
		proxy.ProxyInit();
		NetworkRegistry.instance().registerGuiHandler(this,new GuiHandler());
	}
	
	SGDarkMultiItem registerMI(int ID,String[] sN, int[] icons,String[] info,int StackSize)
	{
		SGDarkMultiItem Temp = new SGDarkMultiItem(ID);
		Temp.setSubNames(sN);
		Temp.setSubIcons(icons);
		Temp.setSubInfo(info);
		Temp.setMaxStackSize(StackSize);
		return Temp;
	}
	
	void registerDarkRecipes()
	{
		ShapedOreRecipe TempRec;
		ItemStack darkUpgradeIris = new ItemStack(sgDarkUpgrades,1,2);
		ItemStack darkUpgradeFast = new ItemStack(sgDarkUpgrades,1,0);
		ItemStack darkUpgradeSafe = new ItemStack(sgDarkUpgrades,1,1);
		ItemStack darkHardUnstable = new ItemStack(sgHardFuel,1,0);
		ItemStack darkHardStable = new ItemStack(sgHardFuel,1,1);
		ItemStack darkHardDust = new ItemStack(sgHardFuel,1,2);
		ItemStack darkHardStableDust = new ItemStack(sgHardFuel,1,3);
		
		
		TempRec = new ShapedOreRecipe(darkUpgradeIris,true,new Object[]{
			"III","IDI","III",
			Character.valueOf('I'),"ingotIron",Character.valueOf('D'),sgControllerCrystal});
		GameRegistry.addRecipe(TempRec);
		
		TempRec = new ShapedOreRecipe(darkUpgradeFast,true,new Object[]{
				"DID","IDI","DID",
				Character.valueOf('I'),darkHardStable,Character.valueOf('D'),sgCoreCrystal});
		GameRegistry.addRecipe(TempRec);
		
		TempRec = new ShapedOreRecipe(darkUpgradeSafe,true,new Object[]{
				"XIX","TDT","XIX",
				Character.valueOf('X'),darkHardStable,Character.valueOf('I'),darkUpgradeFast,
				Character.valueOf('T'),darkUpgradeIris,Character.valueOf('D'),sgCoreCrystal});
		GameRegistry.addRecipe(TempRec);
		
		TempRec = new ShapedOreRecipe(darkUpgradeSafe,true,new Object[]{
				"XIX","TDT","XIX",
				Character.valueOf('X'),darkHardStable,Character.valueOf('T'),darkUpgradeFast,
				Character.valueOf('I'),darkUpgradeIris,Character.valueOf('D'),sgCoreCrystal});
		GameRegistry.addRecipe(TempRec);
		
		TempRec = new ShapedOreRecipe(darkHardUnstable,true,new Object[]{
				"DND","NNN","DND",
				Character.valueOf('D'),Item.diamond,Character.valueOf('N'),naquadahIngot});
		GameRegistry.addRecipe(TempRec);
		
		TempRec = new ShapedOreRecipe(darkHardStable,true,new Object[]{
				"DGD","DND","DGD",
				Character.valueOf('D'),Item.diamond,Character.valueOf('N'),darkHardUnstable,Character.valueOf('G'),Block.glass});
		GameRegistry.addRecipe(TempRec);
		
		if(Loader.isModLoaded("ic2"))
		{
			Ic2Recipes.addMaceratorRecipe(new ItemStack(naquadah,1,0), new ItemStack(sgHardFuel,2,2));
			TempRec = new ShapedOreRecipe(darkHardStableDust,true,new Object[]{
					"DGD","DND","DGD",
					Character.valueOf('D'),"dustDiamond",Character.valueOf('N'),darkHardDust,Character.valueOf('G'),Block.sand});
			GameRegistry.addRecipe(TempRec);
			Ic2Recipes.addCompressorRecipe(darkHardStableDust,darkHardStable);
		}
		
		if(Loader.isModLoaded("ComputerCraft"))
		{
			TempRec = new ShapedOreRecipe(diallerBlock,true,new Object[]{
					"SRS","RCR","SRS",
					Character.valueOf('S'),Block.stone,Character.valueOf('R'),Item.redstone,Character.valueOf('C'),sgControllerCrystal});
			GameRegistry.addRecipe(TempRec);
		}
		
		TempRec = new ShapedOreRecipe(sgDarkPowerBlock,true,new Object[]{
				"GNG","NDN","GNG",
				Character.valueOf('G'),"ingotGold",Character.valueOf('N'),naquadahIngot,Character.valueOf('D'),Item.diamond
		});
	}
	
	void registerMultiItems()
	{
		//UPGRADES
		String[] a = new String[]  {"Stargate Upgrade - Fast Dial", "Stargate Upgrade - Safe Dial", "Stargate Upgrade - Iris"};
		String [] b =  new String[] 
		{	"Allows instant dialling#at 10 * energy cost",
			"Allows dialling with no kawoosh#at 4 * energy cost",
			"Allows computer controlled iris"};
		int[] c = new int[]{71,72,70};
		SGDarkMultiItem TempUpgrades = registerMI(ConfigHandler.itemUpgradesID,a,c,b,1);
		GameRegistry.registerItem((Item) TempUpgrades, "sgDarkUpgrades");
		sgDarkUpgrades = TempUpgrades;
		
		a = new String[] {"Unstable Naquadriah","Naquadriah","Naquadah Dust","Naquadriah Dust"};
		b = new String[] {};
		c = new int[]{80,81,82,83};
		SGDarkMultiItem TempHardFuel = registerMI(ConfigHandler.itemHardID,a,c,b,64);
		GameRegistry.registerItem((Item) TempHardFuel, "sgDarkHardFuel");
		sgHardFuel = TempHardFuel;
	}
	void registerBlocks()
	{
		diallerBlock = new SGDarkDiallerBlock(ConfigHandler.blockDiallerID, 0).setBlockName("diallerblock");
		sgDarkPowerBlock = new SGDarkPowerBlock(ConfigHandler.blockPowererID, 0).setBlockName("powererblock");
		sgBaseBlock = new SGBaseBlock(ConfigHandler.blockSGBaseID).setBlockName("stargateBase");
		sgRingBlock = new SGRingBlock(ConfigHandler.blockSGRingID).setBlockName("stargateRing");
		sgControllerBlock = new SGControllerBlock(ConfigHandler.blockSGControllerID).setBlockName("stargateController");
		sgPortalBlock = new SGPortalBlock(ConfigHandler.blockSGPortalID).setBlockName("stargatePortal");
		naquadahBlock = new NaquadahBlock(ConfigHandler.blockNaquadahID).setBlockName("naquadahBlock");
		naquadahOre = new NaquadahOreBlock(ConfigHandler.blockOreNaquadahID).setBlockName("naquadahOre");

		ItemStack chiselledSandstone = new ItemStack(Block.sandStone, 1, 1);
		ItemStack smoothSandstone = new ItemStack(Block.sandStone, 1, 2);
		ItemStack sgChevronBlock = new ItemStack(sgRingBlock, 1, 1);

		GameRegistry.registerBlock(diallerBlock, "blockDialler");
		GameRegistry.registerTileEntity(SGDarkDiallerTE.class, "tileDialler");
		GameRegistry.addRecipe(new ItemStack(diallerBlock, 1), "III", "RDR", "III", 'I', Item.ingotIron, 'R', Item.redstone, 'D', sgControllerCrystal);
		LanguageRegistry.addName(diallerBlock, "Dialling Computer");

		GameRegistry.registerBlock(sgDarkPowerBlock, "blockPowerer");
		GameRegistry.registerTileEntity(SGDarkPowerTE.class, "sgDarkPowerTE");
		GameRegistry.addRecipe(new ItemStack(sgDarkPowerBlock, 1), "IRI", "GDG", "IRI", 'I', Item.ingotIron, 'R', Item.redstone, 'G', Item.ingotGold, 'D', Item.diamond);
		LanguageRegistry.addName(sgDarkPowerBlock, "Stargate Power Interface");

		GameRegistry.registerBlock(sgRingBlock, SGRingItem.class,"stargateRing");
		GameRegistry.registerTileEntity(SGRingTE.class,"SGRingTE");
		GameRegistry.addRecipe(new ItemStack(sgRingBlock,1),"CCC", "NNN", "SSS",'S', smoothSandstone, 'N', naquadahIngot, 'C', chiselledSandstone);
        LanguageRegistry.addName(sgRingBlock,"Stargate Ring Block");
		GameRegistry.addRecipe(sgChevronBlock, "CgC", "NpN", "SrS",'S', smoothSandstone, 'N', naquadahIngot, 'C', chiselledSandstone,'g', Item.lightStoneDust, 'r', Item.redstone, 'p', Item.enderPearl);
        LanguageRegistry.addName(sgChevronBlock,"Stargate Chevron Block");

		GameRegistry.registerBlock(sgBaseBlock,"stargateBase");
		GameRegistry.registerTileEntity(SGBaseTE.class,"SGBaseTE");
		GameRegistry.addRecipe(new ItemStack(sgBaseBlock,1),"CrC", "NeN", "ScS",'S', smoothSandstone, 'N', naquadahIngot, 'C', chiselledSandstone,'r', Item.redstone, 'e', Item.eyeOfEnder, 'c', sgCoreCrystal);
        LanguageRegistry.addName(sgBaseBlock,"Stargate Base Block");

		GameRegistry.registerBlock(sgControllerBlock,"stargateController");
		GameRegistry.registerTileEntity(SGControllerTE.class,"SGControllerTE");
		GameRegistry.addRecipe(new ItemStack(sgControllerBlock, 1),"bbb", "OpO", "OcO",'b', Block.stoneButton, 'O', Block.obsidian, 'p', Item.enderPearl,'r', Item.redstone, 'c', sgControllerCrystal);
        LanguageRegistry.addName(sgControllerBlock, "Dial-Home Device");

		GameRegistry.registerBlock(naquadahBlock, "naquadahBlock");
		GameRegistry.addRecipe(new ItemStack(naquadahBlock, 1),"nnn","nnn","nnn",'n',naquadahIngot);
        LanguageRegistry.addName(naquadahBlock, "Naquadah Block");

		GameRegistry.registerBlock(naquadahOre, "naquadahOre");
        LanguageRegistry.addName(naquadahOre, "Naquadah Ore");
	}
	void registerItems()
	{
        String blueDye = new String("dyeBlue");
        String orangeDye = new String("dyeOrange");

        naquadah = new BaseItem(ConfigHandler.itemNaquadahID, "/sgextensions/resources/textures.png").setItemName("naquadah").setIconIndex(0x41);
        naquadahIngot = new BaseItem(ConfigHandler.itemNaqIngotID, "/sgextensions/resources/textures.png").setItemName("naquadahIngot").setIconIndex(0x42);
        sgCoreCrystal = new BaseItem(ConfigHandler.itemCrystalCoreID,"/sgextensions/resources/textures.png").setItemName("sgCrystalCore").setIconIndex(0x44);
        sgControllerCrystal = new BaseItem(ConfigHandler.itemCrystalControlID,"/sgextensions/resources/textures.png").setItemName("sgCrystalControl").setIconIndex(0x45);
        
		GameRegistry.registerItem(naquadah,"itemNaquadah");
        LanguageRegistry.addName(naquadah,"Naquadah");

		GameRegistry.registerItem(naquadahIngot, "itemNaqIngot");
        LanguageRegistry.addName(naquadahIngot, "Naquadah Ingot");
        GameRegistry.addShapelessRecipe(new ItemStack(naquadahIngot, 1), new ItemStack(naquadah, 1),new ItemStack(Item.ingotIron, 1));

		GameRegistry.registerItem(sgCoreCrystal, "crystalSGCore");
        LanguageRegistry.addName(sgCoreCrystal, "Stargate Core Crystal");
        GameRegistry.addRecipe(new ShapedOreRecipe(sgCoreCrystal, true, new Object[]{
        		"bbr", "rdb", "brb",
        		Character.valueOf('b'), blueDye, Character.valueOf('r'), Item.redstone, Character.valueOf('d'), Item.diamond
        }));

		GameRegistry.registerItem(sgControllerCrystal, "crystalSGControl");
        LanguageRegistry.addName(sgControllerCrystal, "DHD Control Crystal");
        GameRegistry.addRecipe(new ShapedOreRecipe(sgControllerCrystal, true, new Object[]{
        		"roo", "odr", "oor",
        		Character.valueOf('o'), orangeDye, Character.valueOf('r'), Item.redstone, Character.valueOf('d'), Item.diamond
        }));
        
        registerMultiItems();
        if(fuelHardMode)
        {
        	ItemStack tv = new ItemStack(sgHardFuel);
        	tv.setItemDamage(1);
        	stargateFuel = tv;
        }
        else
        {
        	stargateFuel = new ItemStack(naquadah);
        }
	}
	void registerRandomItems()
	{
		String[] categories = {ChestGenHooks.MINESHAFT_CORRIDOR,
				ChestGenHooks.PYRAMID_DESERT_CHEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST,
				ChestGenHooks.STRONGHOLD_LIBRARY};
		addRandomChestItem(new ItemStack(sgBaseBlock), 1, 1, 1, categories);
		addRandomChestItem(new ItemStack(sgRingBlock, 1, 0), 1, 15, 1, categories);
		addRandomChestItem(new ItemStack(sgRingBlock, 1, 1), 1, 13, 1, categories);
	}
    void addRandomChestItem(ItemStack stack, int minQty, int maxQty, int weight, String... category)
    {
        WeightedRandomChestContent item = new WeightedRandomChestContent(stack, minQty, maxQty, weight);
        for (int i = 0; i < category.length; i++)
            ChestGenHooks.addItem(category[i], item);
    }
	void registerWorldGenerators()
	{
		naquadahOreGenerator = new NaquadahOreWorldGen();
		GameRegistry.registerWorldGenerator(naquadahOreGenerator);
	}

	void registerTradeHandlers()
	{
		VillagerRegistry reg = VillagerRegistry.instance();
		SGTradeHandler handler = new SGTradeHandler();
		for (int i = 0; i < 5; i++)
			reg.registerVillageTradeHandler(i, handler);
	}

	@ForgeSubscribe
	public void onChunkLoad(ChunkDataEvent.Load e)
	{
		Chunk chunk = e.getChunk();
		//System.out.printf("SGCraft.onChunkLoad: (%d, %d)\n", chunk.xPosition, chunk.zPosition);
		SGChunkData.onChunkLoad(e);
	}

	@ForgeSubscribe
	public void onChunkSave(ChunkDataEvent.Save e)
	{
		Chunk chunk = e.getChunk();
		//System.out.printf("SGCraft.onChunkSave: (%d, %d)\n", chunk.xPosition, chunk.zPosition);
		SGChunkData.onChunkSave(e);
	}
}