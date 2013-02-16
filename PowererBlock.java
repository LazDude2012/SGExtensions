package sgextensions;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PowererBlock extends BlockContainer {

	public PowererBlock(int i,int j)
	{
		super(i,j, Material.rock);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}
	@Override
	public TileEntity createNewTileEntity(World var1)
	{
		return new TilePowerer();
	}
	@Override
	public int getBlockTextureFromSide(int i){
		return 1;
	}
}
