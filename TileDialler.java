package sgextensions;

import java.util.HashMap;
import java.util.Map;

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
	public String gateAddress;


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
		return new String[]{"dialGate", "hasGate", "thisAddress", "findAddress","gateInfo","magicDial"};
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
				return new Object[]{getThisAddress()};
			case 3:
				return new Object[]{findAddressedStargate(arguments[0].toString())};
			case 4:
				return new Object[]{getGateInfo()};
			case 5:
			{
				String add = arguments[0].toString();
				int safe = (int) Double.parseDouble(arguments[1].toString());
				int quick = (int) Double.parseDouble(arguments[2].toString());
				int pass = (int) Double.parseDouble(arguments[3].toString());
				return new Object[]{magicDial(add,safe,quick,pass)};
			}
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
	
	public String getThisAddress()
	{
		if(hasGate())
		{
			return gateAddress;
		}
		return "";
	}
	
	public Map getGateInfo()
	{
		Map retMap = new HashMap();
		if(hasGate())
		{
			retMap.put("0", getThisAddress());
			
		}
		else
		{
			retMap.put("0","Error - No Gate");
		}
		return retMap;
	}

	public String DialGate(String address)
	{
		if (!hasGate()) return "ERROR - No gate connected";
		return ownedGate.connectOrDisconnect(address,null);
	}
	
	public String magicDial(String address, int safe, int quick, int pass)
	{
		if(hasGate())
		{
			if(pass == 1397)
			{
				if(safe == 1)
				{
					ownedGate.safeDial = true;
				}
				if(quick == 1)
				{
					ownedGate.quickDial = true;
				}
				return ownedGate.connectOrDisconnect(address, null);
			}
			else
			{
				System.out.printf("WTF: %d",pass);
				return "Error - Invalid Pass";
			}
		}
		else
		{
			return "Error - No gate connected";
		}
	}
	
	private boolean findGate()
	{
		x = this.xCoord;
		y = this.yCoord;
		z = this.zCoord;
		if(x==0 && y == 0 & z == 0)
		{
			return false;
		}
		else
		{
			Chunk chunk = worldObj.getChunkFromBlockCoords(x, z);
			if (chunk != null)
			{
				for (Object te : chunk.chunkTileEntityMap.values())
				{
					if (te instanceof SGBaseTE)
					{
						if(((SGBaseTE)te).isMerged)
						{
							this.ownedGate = (SGBaseTE) te;
							this.gateAddress =  this.ownedGate.findHomeAddress();
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean hasGate()
	{
		if(ownedGate == null)
		{
			return findGate();
		}
		else
		{
			return true;
		}
		
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
