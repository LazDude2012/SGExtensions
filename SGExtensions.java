package sgextensions;

import cpw.mods.fml.client.registry.ClientRegistry;
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
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkDataEvent;

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

	public static Item naquadah, naquadahIngot, sgCoreCrystal, sgControllerCrystal;

	public static boolean addOresToExistingWorlds;
	public static boolean addOres;

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
		registerItems();
        registerBlocks();
        registerRandomItems();
        registerTradeHandlers();
        registerWorldGenerators();
		proxy.ProxyInit();
		NetworkRegistry.instance().registerGuiHandler(this,new GuiHandler());
	}
	void registerBlocks()
	{
		diallerBlock = new DiallerBlock(ConfigHandler.blockDiallerID, 0).setBlockName("diallerblock");
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
		GameRegistry.registerTileEntity(TileDialler.class, "tileDialler");
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
        ItemStack blueDye = new ItemStack(Item.dyePowder, 1, 4);
        ItemStack orangeDye = new ItemStack(Item.dyePowder, 1, 14);

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
        GameRegistry.addRecipe(new ItemStack(sgCoreCrystal, 1), "bbr", "rdb", "brb", 'b', blueDye, 'r', Item.redstone, 'd', Item.diamond);

		GameRegistry.registerItem(sgControllerCrystal, "crystalSGControl");
        LanguageRegistry.addName(sgControllerCrystal, "DHD Control Crystal");
        GameRegistry.addRecipe(new ItemStack(sgControllerCrystal, 1), "roo", "odr", "oor", 'o', orangeDye, 'r', Item.redstone, 'd', Item.diamond );
	}
	void registerRandomItems()
	{
		String[] categories = {ChestGenHooks.MINESHAFT_CORRIDOR,
				ChestGenHooks.PYRAMID_DESERT_CHEST, ChestGenHooks.PYRAMID_JUNGLE_CHEST,
				ChestGenHooks.STRONGHOLD_LIBRARY, ChestGenHooks.VILLAGE_BLACKSMITH};
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