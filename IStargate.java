package sgextensions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public interface IStargate
{
	TileEntity getTE();
	SGState state = SGState.Idle;
	boolean getIrisState();
	void setIrisState(boolean state);
	boolean isConnected();
	TileDHD getLinkedDHD();
	void connectOrDisconnect(String address,EntityPlayer player);
	String getHomeAddress() throws SGAddressing.AddressingError;
	void entityInPortal(Entity entity);
	void startDiallingStargate(String address, IStargate destination, boolean isInitiator);
	IStargate getConnectedStargateTE();
	Trans3 localToGlobalTransformation();
}