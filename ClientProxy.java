package sgextensions;

import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderThings()
	{
		MinecraftForgeClient.preloadTexture("/sgextensions/blocks.png");
	}
}
