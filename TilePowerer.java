package sgextensions;

import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerFramework;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;

public class TilePowerer extends TileEntity implements IPowerReceptor
{
	boolean isFull, isGateActive;
	private IPowerProvider powerProvider;
	private SGBaseTE connectedGate;
	public int storedEnergy;

	public TilePowerer()
	{
		powerProvider = PowerFramework.currentFramework.createPowerProvider();
		powerProvider.configure(25, 2, 100, 120, 120000);
		InitGateIntegration();
	}

	public void InitGateIntegration()
	{
		Chunk chunk = worldObj.getChunkFromBlockCoords(xCoord, zCoord);
		for (Object x : chunk.chunkTileEntityMap.values())
		{
			if (x instanceof SGBaseTE)
			{
				connectedGate = (SGBaseTE) x;
				isGateActive = connectedGate.isConnected();
			}
		}
	}

	@Override
	public void updateEntity()
	{
		if (powerProvider.getEnergyStored() < powerProvider.getMaxEnergyStored())
			isFull = false;
		else isFull = true;
		if (connectedGate != null)
		{
			float energyCapability = powerProvider.useEnergy(120, 120, false);
			if (energyCapability >= 120)
			{
				powerProvider.useEnergy(120, 120, true);
				connectedGate.fuelBuffer += 1;
			}
		}
	}

	@Override
	public void setPowerProvider(IPowerProvider provider)
	{
		this.powerProvider = provider;
	}

	@Override
	public IPowerProvider getPowerProvider()
	{
		return this.powerProvider;
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
