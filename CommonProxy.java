package sgextensions;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class CommonProxy implements IGuiHandler
{
	public void registerRenderThings()
	{

	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (id)
		{
			case SGExtensions.GUIELEMENT_GATE:
				return new SGBaseContainer(player, getGatefromCoords(world, x, y, z));
			default:
				return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		throw new UnsupportedOperationException();
	}

	public SGBaseTE getGatefromCoords(World world, int x, int y, int z)
	{
		Chunk chunk = world.getChunkFromBlockCoords(x, z);
		for (Object te : chunk.chunkTileEntityMap.values())
		{
			if (te instanceof SGBaseTE)
				return (SGBaseTE) te;
		}
		return null;
	}
}
