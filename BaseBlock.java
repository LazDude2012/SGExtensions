//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic Block with Tile Entity
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BaseBlock<TE extends TileEntity> extends BlockContainer implements BaseIRenderType
{

	static Random random = new Random();

	public int renderID = 0;
	Class<? extends TileEntity> tileEntityClass = null;

	public BaseBlock(int id, Material material, Class<TE> teClass)
	{
		super(id, material);
		setTextureFile("/sgextensions/resources/textures.png");
		tileEntityClass = teClass;
		if (teClass != null)
			GameRegistry.registerTileEntity(teClass, teClass.getName());
	}

	@Override
	public boolean hasTileEntity(int metadata)
	{
		return tileEntityClass != null;
	}

	@Override
	public int getRenderType()
	{
		return renderID;
	}

	@Override
	public void setRenderType(int id)
	{
		renderID = id;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return renderID == 0;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z)
	{
		super.onBlockAdded(world, x, y, z);
		TileEntity te = getTileEntity(world, x, y, z);
		if (te instanceof BaseTileEntity)
			((BaseTileEntity) te).onAddedToWorld();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof BaseTileEntity)
		{
			IInventory var7 = ((BaseTileEntity) te).getInventory();
			if (var7 != null)
			{
				for (int var8 = 0; var8 < var7.getSizeInventory(); ++var8)
				{
					ItemStack var9 = var7.getStackInSlot(var8);
					if (var9 != null)
					{
						float var10 = this.random.nextFloat() * 0.8F + 0.1F;
						float var11 = this.random.nextFloat() * 0.8F + 0.1F;
						EntityItem var14;
						for (float var12 = this.random.nextFloat() * 0.8F + 0.1F; var9.stackSize > 0; world.spawnEntityInWorld(var14))
						{
							int var13 = this.random.nextInt(21) + 10;
							if (var13 > var9.stackSize)
								var13 = var9.stackSize;
							var9.stackSize -= var13;
							var14 = new EntityItem(world, (double) ((float) x + var10), (double) ((float) y + var11), (double) ((float) z + var12), new ItemStack(var9.itemID, var13, var9.getItemDamage()));
							float var15 = 0.05F;
							var14.motionX = (double) ((float) this.random.nextGaussian() * var15);
							var14.motionY = (double) ((float) this.random.nextGaussian() * var15 + 0.2F);
							var14.motionZ = (double) ((float) this.random.nextGaussian() * var15);
							if (var9.hasTagCompound())
								var14.getEntityItem().setTagCompound((NBTTagCompound) var9.getTagCompound().copy());
						}
					}
				}
			}
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}

	public TE getTileEntity(IBlockAccess world, int x, int y, int z)
	{
		if (hasTileEntity())
			return (TE) world.getBlockTileEntity(x, y, z);
		else
			return null;
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		if (tileEntityClass != null)
		{
			try
			{
				return tileEntityClass.newInstance();
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		} else
			return null;
	}

	public void setMetadata(World world, int x, int y, int z, int data, boolean notify)
	{
		if (notify)
			world.setBlockMetadataWithNotify(x, y, z, data);
		else
			world.setBlockMetadata(x, y, z, data);
	}

	public int getFacing(IBlockAccess world, int x, int y, int z)
	{
		return facingInWorld(world.getBlockMetadata(x, y, z), getTileEntity(world, x, y, z));
	}

	public int getRotation(IBlockAccess world, int x, int y, int z)
	{
		return rotationInWorld(world.getBlockMetadata(x, y, z), getTileEntity(world, x, y, z));
	}

	public int facingInInventory(int metadata)
	{
		return 0;
	}

	public int rotationInInventory(int metadata)
	{
		return 0;
	}

	public int facingInWorld(int metadata, TE te)
	{
		return 0;
	}

	public int rotationInWorld(int metadata, TE te)
	{
		return 0;
	}

	Trans3 localToGlobalTransformation(World world, int x, int y, int z)
	{
		int data = world.getBlockMetadata(x, y, z);
		TE te = getTileEntity(world, x, y, z);
		int facing = facingInWorld(data, te);
		int rotation = rotationInWorld(data, te);
		return new Trans3(x + 0.5, y + 0.5, z + 0.5).side(facing).turn(rotation);
	}

}
