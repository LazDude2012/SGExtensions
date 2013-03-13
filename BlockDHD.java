//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate Controller Block
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockDHD extends Base4WayBlock<TileDHD>
{

	public BlockDHD(int id)
	{
		super(id, Material.rock /*SGRingBlock.ringMaterial*/, TileDHD.class);
		setHardness(1.5F);
		blockIndexInTexture = 0x0a;
		setCreativeTab(CreativeTabs.tabMisc);
	}

//	@Override
//	public void onBlockAdded(World world, int x, int y, int z) {
//		getTileEntity(world, x, y, z).checkForLink();
//	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta)
	{
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player)
	{
		super.onBlockPlacedBy(world, x, y, z, player);
		getTileEntity(world, x, y, z).checkForLink();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int data)
	{
		TileDHD cte = getTileEntity(world, x, y, z);
		if (cte == null)
		{
			System.out.printf("BlockDHD.breakBlock: No tile entity at (%d,%d,%d)\n",
					x, y, z);
			return;
		}
		if (cte.isLinkedToStargate)
		{
			SGBaseTE gte = cte.getLinkedStargateTE();
			if (gte != null)
				gte.clearLinkToController();
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
	                                int side, float cx, float cy, float cz)
	{
		player.openGui(SGExtensions.instance, SGExtensions.GUIELEMENT_DHD, world, x, y, z);
		return true;
	}

}
