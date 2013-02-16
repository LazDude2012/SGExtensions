package sgextensions;

import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import net.minecraft.tileentity.TileEntity;

public class TilePowerer extends TileEntity implements IPowerReceptor
{
	boolean isFull, isGateActive;
	private IPowerProvider powerProvider;
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
	public void doWork() {}

	@Override
	public int powerRequest()
	{
		if(!isFull || isGateActive){
					return 120;
		}
		return 0;
	}
}
