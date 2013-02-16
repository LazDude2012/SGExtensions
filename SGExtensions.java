package sgextensions;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;

@Mod(modid="teamautomod_SGExtensions",name="SG Extensions",version = "pre1")
@NetworkMod(clientSideRequired = true,serverSideRequired = true)
public class SGExtensions
{
	@SidedProxy(clientSide="sgextensions.ClientProxy",serverSide = "sgextensions.CommonProxy")
	public static CommonProxy proxy;
	public static Block diallerBlock;
	@Mod.Init
	public void load(FMLInitializationEvent event)
	{
		proxy.registerRenderThings();
		diallerBlock=new DiallerBlock(4242,0).setBlockName("diallerBlock");
		GameRegistry.registerBlock(diallerBlock, "Dialler");
		LanguageRegistry.addName(diallerBlock,"Gate Dialing Device");
	}
}
