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
import java.util.logging.Level;

public class ClientProxy extends CommonProxy
{

	Minecraft mc;

	@Override
	public void registerRenderThings()
	{
		MinecraftForgeClient.preloadTexture("/sgextensions/blocks.png");
		MinecraftForgeClient.preloadTexture("/sgextensions/resources/textures.png");
	}

	public void ProxyInit()
	{
		registerSounds();
		registerRenderers();
	}


	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (id)
		{
			case SGExtensions.GUIELEMENT_GATE:
				return SGBaseScreen.create(player, world, x, y, z);
			case SGExtensions.GUIELEMENT_DHD:
				return new SGControllerScreen(player, world, x, y, z);
			default:
				return null;
		}
	}

	void registerSounds()
	{
		FMLLog.log("SGExtensions", Level.INFO, "Loading SGExtensions sounds...");
		SoundPool pool = ModLoader.getMinecraftInstance().sndManager.soundPoolSounds;
		pool.addSound("sgextensions/resources/sounds/sg_abort.ogg", new File("sgextensions/resources/sounds/sg_abort.ogg"));
		pool.addSound("sgextensions/resources/sounds/sg_close.ogg", new File("sgextensions/resources/sounds/sg_close.ogg"));
		pool.addSound("sgextensions/resources/sounds/sg_dial.ogg", new File("sgextensions/resources/sounds/sg_dial.ogg"));
		pool.addSound("sgextensions/resources/sounds/sg_open.ogg", new File("sgextensions/resources/sounds/sg_open.ogg"));
		FMLLog.log("SGExtensions", Level.INFO, "... complete!");
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
