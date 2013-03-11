package sgextensions;

import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerFramework;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;

public class SGDarkPowerTE extends TileEntity implements IPowerReceptor
{
	boolean isFull, isGateActive;
	private int bcPowerReq;
	private IPowerProvider powerVProvider;
	private PowerFramework powerFrame;
	private SGBaseTE connectedGate;
	private int BCFuel = SGExtensions.bcPowerPerFuel;
	private float TimeToRepeat;
	public int storedEnergy;

	public SGDarkPowerTE()
	{
		powerFrame = PowerFramework.currentFramework;
		if(powerFrame != null)
		{
			powerVProvider = powerFrame.createPowerProvider();
			powerVProvider.configure(25, 2, BCFuel, BCFuel, BCFuel * 1000);
			InitGateIntegration();
		}
	}

	public void InitGateIntegration()
	{
		if(xCoord == 0 && yCoord == 0 && zCoord == 0)
		{
			System.out.printf("InitGateInt: Pos (%d, %d) Try later\n", this.xCoord, this.zCoord);
			TimeToRepeat = 10;
		}
		else
		{
			TimeToRepeat = 600;
			System.out.printf("InitGateInt: Pos (%d, %d)\n", this.xCoord, this.zCoord);
			Chunk chunkvar = worldObj.getChunkFromBlockCoords(xCoord, zCoord);
			for (Object x : chunkvar.chunkTileEntityMap.values())
			{
				if (x instanceof SGBaseTE)
				{
					if(((SGBaseTE)x).isMerged)
					{
						TimeToRepeat = 600;
						System.out.printf("InitGateInt: GATE FOUND!");
						connectedGate = (SGBaseTE) x;
						isGateActive = connectedGate.isConnected();
					}
				}
			}
		}
	}

	@Override
	public void updateEntity()
	{
		if(TimeToRepeat > 0)
		{
			TimeToRepeat--;
		}
		else if(TimeToRepeat == 0)
		{
			InitGateIntegration();
		}
		if(powerFrame != null)
		{
			if (powerVProvider.getEnergyStored() < powerVProvider.getMaxEnergyStored())
				isFull = false;
			else isFull = true;
			
			if (connectedGate != null)
			{
				float energyCapability = powerVProvider.getEnergyStored();
				if(energyCapability != 0)
				{
					//System.out.printf("Powerer: Power capability (%f)\n", energyCapability);
					if (energyCapability >= BCFuel)
					{
						//System.out.printf("Gate: Power (%d) Out of (%d)\n", connectedGate.fuelBuffer, connectedGate.maxFuelBuffer);
						//int energyMul = (int) Math.floor(energyCapability / BCFuel);
						int energyMul = 1;
						int toMaxFuel = connectedGate.maxFuelBuffer - connectedGate.fuelBuffer;
						if(toMaxFuel > 0)
						{
							energyMul = Math.min(energyMul, toMaxFuel);
							int energyToUse = (int) Math.floor(BCFuel * energyMul);
							powerVProvider.useEnergy(energyToUse, energyToUse, true);
							connectedGate.fuelBuffer += energyMul;
						}
					}
				}
			}
		}
	}

	@Override
	public void setPowerProvider(IPowerProvider provider)
	{
		this.powerVProvider = provider;
	}

	@Override
	public IPowerProvider getPowerProvider()
	{
		return this.powerVProvider;
	}

	@Override
	public void doWork()
	{
	}

	@Override
	public int powerRequest()
	{
		if (!isFull || isGateActive)
		{
			return 120;
		}
		return 0;
	}
}
