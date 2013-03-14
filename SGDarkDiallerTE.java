package sgextensions;

import java.util.HashMap;
import java.util.Map;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;

public class SGDarkDiallerTE extends TileEntity implements IPeripheral
{
	public int x, y, z;
	public SGBaseTE ownedGate = null;
	public String gateAddress;
	
	public boolean isLinkedToStargate;
	public int linkedX, linkedY, linkedZ;

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
		return new String[]{"dialGate", "hasGate", "thisAddress", "findAddress","gateInfo","closeIris","openIris","toggleIris","magicDial"};
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
				return new Object[]{closeIris()};
			case 6:
				return new Object[]{openIris()};
			case 7:
				return new Object[]{toggleIris()};
			case 8:
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
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		isLinkedToStargate = nbt.getBoolean("isLinkedToStargate");
		linkedX = nbt.getInteger("linkedX");
		linkedY = nbt.getInteger("linkedY");
		linkedZ = nbt.getInteger("linkedZ");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setBoolean("isLinkedToStargate",isLinkedToStargate);
		nbt.setInteger("linkedX", linkedX);
		nbt.setInteger("linkedY", linkedY);
		nbt.setInteger("linkedZ", linkedZ);
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
	
	public String openIris()
	{
		if(hasGate())
		{
			return ownedGate.openIris();
		}
		return "Error - No gate";
	}
	
	public String closeIris()
	{
		if(hasGate())
		{
			return ownedGate.closeIris();
		}
		return "Error - No gate";
	}
	
	public String toggleIris()
	{
		if(hasGate())
		{
			return ownedGate.toggleIris();
		}
		return "Error - No gate";
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
			SGState state = ownedGate.state;
			String outState = "Unknown";
			if(state == SGState.Connected) 		outState = "Connected";
			if(state == SGState.Dialling) 		outState = "Dialling";
			if(state == SGState.Disconnecting) 	outState = "Disconnecting";
			if(state == SGState.Idle) 			outState = "Idle";
			if(state == SGState.InterDialling) 	outState = "Interdialling";
			if(state == SGState.Transient) 		outState = "Transient";
			retMap.put(1, getThisAddress());
			retMap.put(2, ownedGate.numEngagedChevrons);
			retMap.put(3, outState);
			retMap.put(4, ownedGate.fuelBuffer);
			retMap.put(5, ownedGate.getIrisType());
			retMap.put(6, ownedGate.irisState());
			
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
	
	public double getTEDistance(TileEntity A, TileEntity B)
	{
		if(A != null && B != null)
		{
			Vector3 APos = new Vector3(A.xCoord,A.yCoord,A.zCoord);
			Vector3 BPos = new Vector3(B.xCoord,B.yCoord,B.zCoord);
			Vector3 InvalidPos = new Vector3(0,0,0);
			if(APos != InvalidPos)
			{
				if(BPos != InvalidPos)
				{
					return APos.distance(BPos);
				}
			}
		}
		return -1;
	}
	
	private boolean linkGate(SGBaseTE gate)
	{
		if(gate.isMerged)
		{
			double Distance = getTEDistance((TileEntity) gate,(TileEntity) this);
			if(Distance > 0 && Distance < 6)
			{
				if(gate.isLinkedToController == false)
				{
					ownedGate = gate;
					linkedX = ((SGBaseTE) gate).xCoord;
					linkedY = ((SGBaseTE) gate).yCoord;
					linkedZ = ((SGBaseTE) gate).zCoord;
					isLinkedToStargate = true;
					gate.linkedX = this.xCoord;
					gate.linkedY = this.yCoord;
					gate.linkedZ = this.zCoord;
					gate.isLinkedToController = true;
					gateAddress =  ownedGate.findHomeAddress();
					return true;
				}
				else
				{
					if(gate.linkedX == this.xCoord && gate.linkedY == this.yCoord && gate.linkedZ == this.zCoord)
					{
						linkedX = gate.xCoord;
						linkedY = gate.yCoord;
						linkedZ = gate.zCoord;
						isLinkedToStargate = true;
						ownedGate = gate;
						gateAddress =  ownedGate.findHomeAddress();
						return true;
					}
				}
			}
		}
		this.ownedGate = null;
		this.isLinkedToStargate = false;
		return false;
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
			TileEntity gate;
			if(isLinkedToStargate)
			{
				gate = worldObj.getBlockTileEntity(linkedX, linkedY, linkedZ);
				if(gate instanceof SGBaseTE)
				{
					return linkGate((SGBaseTE) gate);
				}
			}
			if (chunk != null)
			{
				for (Object te : chunk.chunkTileEntityMap.values())
				{
					if (te instanceof SGBaseTE)
					{
						return linkGate((SGBaseTE) te);
					}
				}
			}
		}
		return false;
	}

	public void unlinkStargate()
	{
		if(isLinkedToStargate)
		{
			ownedGate.clearLinkToController();
			ownedGate = null;
			isLinkedToStargate = false;
		}
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
