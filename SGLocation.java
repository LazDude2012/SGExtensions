//------------------------------------------------------------------------------------------------
//
//   SG Craft - Structure representing the location of a stargate
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class SGLocation
{

	public int dimension;
	public int x, y, z;

	public SGLocation(TileEntity te)
	{
		this(te.worldObj.provider.dimensionId, te.xCoord, te.yCoord, te.zCoord);
	}

	public SGLocation(int dimension, int x, int y, int z)
	{
		this.dimension = dimension;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public SGLocation(NBTTagCompound nbt)
	{
		dimension = nbt.getInteger("dimension");
		x = nbt.getInteger("x");
		y = nbt.getInteger("y");
		z = nbt.getInteger("z");
	}

	NBTTagCompound toNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("dimension", dimension);
		nbt.setInteger("x", x);
		nbt.setInteger("y", y);
		nbt.setInteger("z", z);
		return nbt;
	}

	SGBaseTE getStargateTE()
	{
		World world = DimensionManager.getWorld(dimension);
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof SGBaseTE)
			return (SGBaseTE) te;
		else
			return null;
	}

}
