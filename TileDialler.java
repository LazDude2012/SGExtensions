package sgextensions;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;

public class TileDialler extends TileEntity implements IPeripheral
{
	public int x, y, z;
	public SGBaseTE ownedGate = null;


	public final String symbolChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public final int numSymbols = symbolChars.length();
	public final int addressLength = 7;
	public final int numDimensionSymbols = 2;
	public final int numCoordSymbols = addressLength - numDimensionSymbols;
	public final int coordPower = (int) Math.pow(numSymbols, numCoordSymbols);
	public final int dimensionPower = (int) Math.pow(numSymbols, numDimensionSymbols);
	public final int maxCoord = ((int) Math.floor(Math.sqrt(coordPower - 1))) / 2;
	public final int minCoord = -maxCoord;
	public final int coordRange = maxCoord - minCoord + 1;
	public final int minDimension = -1;
	public final int maxDimension = minDimension + dimensionPower - 1;
	//Bits of the SGAddressing class required for the Dialling Computer to work its magic.

	@Override
	public String getType()
	{
		return "Dialing Computer";
	}

	@Override
	public String[] getMethodNames()
	{
		return new String[]{"dialGate", "hasGate", "findAddress"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception
	{
		switch (method)
		{
			case 0:
				return new Object[]{DialGate(arguments[0].toString())};
			case 1:
				return new Object[]{hasGate()};
			case 2:
				return new Object[]{findAddressedStargate(arguments[0].toString())};
			default:
				return new Object[0];
		}
	}

	@Override
	public boolean canAttachToSide(int side)
	{
		return true;
	}

	@Override
	public void attach(IComputerAccess computer)
	{
	}

	@Override
	public void detach(IComputerAccess computer)
	{
	}

	public boolean DialGate(String address)
	{
		if (!hasGate()) return false;
		ownedGate.connectOrDisconnect(address, worldObj.getClosestPlayer(x, y, z, 4.00));
		return true;
	}

	public boolean hasGate()
	{
		x = this.xCoord;
		y = this.yCoord;
		z = this.zCoord;
		Chunk chunk = worldObj.getChunkFromBlockCoords(x, z);
		if (chunk != null)
		{
			for (Object te : chunk.chunkTileEntityMap.values())
			{
				if (te instanceof SGBaseTE)
				{
					this.ownedGate = (SGBaseTE) te;
					return true;
				}
			}
		}
		return false;
	}

	public String findAddressedStargate(String address)
	{
		String csyms = address.substring(0, 5);
		String dsyms = address.substring(5, 5 + 2);
		int s = intFromSymbols(csyms);
		int chunkx = minCoord + s / coordRange;
		int chunkz = minCoord + s % coordRange;
		int dimension = minDimension + intFromSymbols(dsyms);
		World world = DimensionManager.getWorld(dimension);
		if (world != null)
		{
			return "The address leads to chunk coords X: " + chunkx + " Z: " + chunkz + " in dimension " + dimension + ".";
		} else return "The address is in a non-existent dimension " + dimension + ".";
	}

	int intFromSymbols(String s)
	{
		int i = 0;
		int n = s.length();
		for (int j = n - 1; j >= 0; j--)
		{
			char c = s.charAt(j);
			i = i * numSymbols + charToSymbol(c);
		}
		return i;
	}

	int charToSymbol(char c)
	{
		return charToSymbol(String.valueOf(c));
	}

	int charToSymbol(String c)
	{
		return symbolChars.indexOf(c);
	}
}
