package sgextensions;

import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerFramework;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;

public class SGDarkPowerTE extends TileEntity implements IPowerReceptor
{
	boolean isFull, isGateActive;
	private IPowerProvider powerVProvider;
	private PowerFramework powerFrame;
	private SGBaseTE connectedGate;
	private float TimeToRepeat;
	public int storedEnergy;

	public SGDarkPowerTE()
	{
		powerFrame = PowerFramework.currentFramework;
		if(powerFrame != null)
		{
			powerVProvider = powerFrame.createPowerProvider();
			powerVProvider.configure(25, 2, 100, 120, 120000);
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
			TimeToRepeat = -1;
			System.out.printf("InitGateInt: Pos (%d, %d)\n", this.xCoord, this.zCoord);
			Chunk chunkvar = worldObj.getChunkFromBlockCoords(xCoord, zCoord);
			for (Object x : chunkvar.chunkTileEntityMap.values())
			{
				if (x instanceof SGBaseTE)
				{
					System.out.printf("InitGateInt: GATE FOUND!");
					connectedGate = (SGBaseTE) x;
					isGateActive = connectedGate.isConnected();
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
				float energyCapability = powerVProvider.useEnergy(120, 120, false);
				if (energyCapability >= 120)
				{
					System.out.printf("Gate: Power (%d)\n", connectedGate.fuelBuffer);
					powerVProvider.useEnergy(120, 120, true);
					connectedGate.fuelBuffer += 1;
					
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
