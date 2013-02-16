package sgextensions;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class DiallerBlock extends Block
{
	public DiallerBlock(int i, int j)
	{
		super(i,j, Material.rock);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}
}
