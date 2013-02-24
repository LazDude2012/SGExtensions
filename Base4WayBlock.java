//------------------------------------------------------------------------------------------------
//
//   Mod Base - 4-way rotatable block
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class Base4WayBlock<TE extends TileEntity> extends BaseBlock<TE>
{

	static int rotationShift = 0;
	static int rotationMask = 0x3;

	public Base4WayBlock(int id, Material material)
	{
		super(id, material, null);
	}

	public Base4WayBlock(int id, Material material, Class<TE> teClass)
	{
		super(id, material, teClass);
	}

	public void setRotation(World world, int x, int y, int z, int rotation, boolean notify)
	{
		int data = world.getBlockMetadata(x, y, z);
		data = insertRotation(data, rotation);
		setMetadata(world, x, y, z, data, notify);
	}

	@Override
	public int rotationInWorld(int data, TE te)
	{
		return extractRotation(data);
	}

	public int extractRotation(int data)
	{
		return (data & rotationMask) >> rotationShift;
	}

	public int insertRotation(int data, int rotation)
	{
		return (data & ~rotationMask) | (rotation << rotationShift);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player)
	{
		int rotation = Math.round((180 - player.rotationYaw) / 90) & 3;
		setRotation(world, x, y, z, rotation, true);
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int data)
	{
		int rotation = extractRotation(data);
		int localSide = Directions.globalToLocalSide(side, rotation);
		return getBlockTextureFromLocalSideAndMetadata(localSide, data);
	}

	int getBlockTextureFromLocalSideAndMetadata(int side, int data)
	{
		return blockIndexInTexture + side;
	}

}
