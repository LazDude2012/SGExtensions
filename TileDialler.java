package sgextensions;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;
import gcewing.sg.SGBaseTE;
import gcewing.sg.SGAddressing;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;

public class TileDialler extends TileEntity implements IPeripheral {
	public int x,y,z;
	public SGBaseTE ownedGate = null;
	@Override
	public String getType()
	{
		return "Dialing Computer";
	}

	@Override
	public String[] getMethodNames()
	{
		return new String[]{"dialGate","hasGate"};
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, int method, Object[] arguments) throws Exception
	{
		switch(method)
		{
			case 0:
				return new Object[]{DialGate(arguments[0].toString())};
			case 1:
				return new Object[]{hasGate()};
			default:
				return new Object[0];
		}
	}

	@Override
	public boolean canAttachToSide(int side){ return true; }
	@Override
	public void attach(IComputerAccess computer){}
	@Override
	public void detach(IComputerAccess computer){}

	public boolean DialGate(String address){
		if(!hasGate()) return false;
		ownedGate.connectOrDisconnect(address,worldObj.getClosestPlayer(x,y,z,4.00));
		return true;
	}
	public boolean hasGate(){
		x = this.xCoord; y=this.yCoord; z=this.zCoord;
		Chunk chunk = worldObj.getChunkFromBlockCoords(x,z);
		if(chunk != null)
		{
			for (Object te : chunk.chunkTileEntityMap.values()) {
				if (te instanceof SGBaseTE){
					this.ownedGate = (SGBaseTE)te;
					return true;
				}
			}
		}
		return false;
	}
}
