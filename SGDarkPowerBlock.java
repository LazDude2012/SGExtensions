package sgextensions;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class SGDarkPowerBlock extends BlockContainer
{

	public SGDarkPowerBlock(int i, int j)
	{
		super(i, j, Material.rock);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	public String getTextureFile()
	{
		return "/sgextensions/resources/blocks.png";
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new SGDarkPowerTE();
	}

	@Override
	public int getBlockTextureFromSide(int i)
	{
		return 3;
	}
}
