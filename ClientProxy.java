package sgextensions;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundPool;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.src.ModLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

public class ClientProxy extends CommonProxy
{

	Minecraft mc;

	@Override
	public void registerRenderThings()
	{
		MinecraftForgeClient.preloadTexture("/sgextensions/resources/blocks.png");
		MinecraftForgeClient.preloadTexture("/sgextensions/resources/textures.png");
		MinecraftForgeClient.preloadTexture("/sgextensions/resources/iris.png");
	}

	@Override
	public void ProxyInit()
	{
		registerSounds();
		registerRenderers();
	}

	void registerSounds()
	{
		try
		{
			FMLLog.log("SGExtensions", Level.INFO, "Loading SGExtensions sounds...");
			SoundPool pool = ModLoader.getMinecraftInstance().sndManager.soundPoolSounds;
			URL resourceURL = getClass().getClassLoader().getResource("sgextensions/resources/");
			pool.addSound("sgextensions/sg_abort.ogg", new URL(resourceURL, "sounds/sg_abort.ogg"));
			pool.addSound("sgextensions/sg_close.ogg", new URL(resourceURL, "sounds/sg_close.ogg"));
			pool.addSound("sgextensions/sg_dial.ogg", new URL(resourceURL, "sounds/sg_dial.ogg"));
			pool.addSound("sgextensions/sg_open.ogg", new URL(resourceURL, "sounds/sg_open.ogg"));
			FMLLog.log("SGExtensions", Level.INFO, "... complete!");
		}
		catch (MalformedURLException e)
		{
			throw new RuntimeException("The hell?",e);
		}
	}

	void registerRenderers()
	{
		addBlockRenderer((SGRingBlock)SGExtensions.sgRingBlock, new SGRingBlockRenderer());
		addBlockRenderer((SGBaseBlock)SGExtensions.sgBaseBlock, new SGBaseBlockRenderer());
		addBlockRenderer((SGControllerBlock)SGExtensions.sgControllerBlock, new BaseBlockRenderer());
		addTileEntityRenderer(SGBaseTE.class, new SGBaseTERenderer());
	}
	void addBlockRenderer(BaseIRenderType block, BaseBlockRenderer renderer)
	{
		int renderID = RenderingRegistry.getNextAvailableRenderId();
		block.setRenderType(renderID);
		renderer.renderID = renderID;
		RenderingRegistry.registerBlockHandler(renderID, renderer);
	}

	void addItemRenderer(Item item, IItemRenderer renderer)
	{
		MinecraftForgeClient.registerItemRenderer(item.itemID, renderer);
	}

	void addItemRenderer(Block block, IItemRenderer renderer)
	{
		MinecraftForgeClient.registerItemRenderer(block.idDropped(0, null, 0), renderer);
	}

	void addTileEntityRenderer(Class<? extends TileEntity> teClass, TileEntitySpecialRenderer renderer)
	{
		ClientRegistry.bindTileEntitySpecialRenderer(teClass, renderer);
	}
}
