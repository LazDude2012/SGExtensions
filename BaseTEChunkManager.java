//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Chunk manager for tile entities
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;

import java.util.List;

public class BaseTEChunkManager implements ForgeChunkManager.LoadingCallback
{

	static SGExtensions base;

	public BaseTEChunkManager(SGExtensions mod)
	{
		base = mod;
		ForgeChunkManager.setForcedChunkLoadingCallback(mod, this);
	}

	Ticket newTicket(World world)
	{
		return ForgeChunkManager.requestTicket(base, world, Type.NORMAL);
	}

	@Override
	public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
	{
		for (Ticket ticket : tickets)
		{
			NBTTagCompound nbt = ticket.getModData();
			if (nbt != null)
				if (nbt.getString("type").equals("TileEntity"))
				{
					int x = nbt.getInteger("xCoord");
					int y = nbt.getInteger("yCoord");
					int z = nbt.getInteger("zCoord");
					TileEntity te = world.getBlockTileEntity(x, y, z);
					if (te instanceof BaseChunkLoadingTE)
						if (!((BaseChunkLoadingTE) te).reinstateChunkTicket(ticket))
							ForgeChunkManager.releaseTicket(ticket);
				}
		}
	}

}
