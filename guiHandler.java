package sgextensions;

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 2/24/13
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity ringTE = world.getBlockTileEntity(x,y,z);
		if(ringTE instanceof SGBaseTE)
		{
			return new SGBaseContainer(player, (SGBaseTE) ringTE);
		}
		else if(ringTE instanceof SGRingTE)
		{
			int baseX, baseY, baseZ;
			baseX = ((SGRingTE) ringTE).baseX;
			baseY = ((SGRingTE) ringTE).baseY;
			baseZ = ((SGRingTE) ringTE).baseZ;
			TileEntity ringBaseTE = world.getBlockTileEntity(baseX, baseY, baseZ);
			if(ringTE instanceof SGBaseTE)
			{
				return new SGBaseContainer(player, (SGBaseTE) ringBaseTE);
			}
		}
		return null;
	}
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity ringTE = world.getBlockTileEntity(x,y,z);
		if(ringTE instanceof SGBaseTE)
		{
			return new SGBaseScreen(player, (SGBaseTE) ringTE);
		}
		else if(ringTE instanceof SGRingTE)
		{
			int baseX, baseY, baseZ;
			baseX = ((SGRingTE) ringTE).baseX;
			baseY = ((SGRingTE) ringTE).baseY;
			baseZ = ((SGRingTE) ringTE).baseZ;
			TileEntity ringBaseTE = world.getBlockTileEntity(baseX, baseY, baseZ);
			if(ringTE instanceof SGBaseTE)
			{
				return new SGBaseScreen(player, (SGBaseTE) ringBaseTE);
			}
		}
		else if(ringTE instanceof SGControllerTE)
		{
			return new SGControllerScreen(player, world, x, y, z);
		}
		return null;
	}
}
