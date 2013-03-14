//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate portal block
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

import java.util.Random;

public class SGPortalBlock extends Block
{

	public SGPortalBlock(int id)
	{
		super(id, Material.rock);
		this.setBlockUnbreakable();
		setBlockBounds(0, 0, 0, 0, 0, 0);
	}

	@Override
	public int getRenderType()
	{
		return -1;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World A, int X, int Y, int Z)
	{
		/*
		//System.out.println("TEST: "+ X + "," + Y + "," + Z);
		SGBaseTE te = getStargateTE(A,X,Y,Z);
		if(te != null)
		{
			String IS = te.irisState();
			//System.out.println(IS);
			if(IS != "Error - No Iris" && IS != "Error - Unknown state" && IS != "Iris - Open")
			{
				return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double)X + 0, (double)Y + 0, (double)Z + 0, (double)X + 1, (double)Y + 1, (double)Z + 1);
			}
		}
		return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(0, 0, 0, 0, 0, 0);*/
		return null;
	}
	
	@Override
	public int quantityDropped(Random par1Random)
	{
		return 0;
	}
	
	@Override
	public void updateTick(World A,int X,int Y, int Z, Random Huh)
	{
		
	}
	
	@Override
	public int tickRate()
	{
		return 3;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if (!world.isRemote)
		{
			//System.out.printf("SGPortalBlock.onEntityCollidedWithBlock (%d,%d,%d) in %s\n",
			//	x, y, z, world);
			SGBaseTE te = getStargateTE(world, x, y, z);
			if (te != null)
				te.entityInPortal(entity);
		}
	}

	SGBaseTE getStargateTE(World world, int x, int y, int z)
	{
		for (int i = -1; i <= 1; i++)
			for (int j = -3; j <= -1; j++)
				for (int k = -1; k <= 1; k++)
				{
					TileEntity te = world.getBlockTileEntity(x + i, y + j, z + k);
					if (te instanceof SGBaseTE)
						return (SGBaseTE) te;
				}
		return null;
	}

}
