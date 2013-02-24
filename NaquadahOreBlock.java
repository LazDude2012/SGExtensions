//------------------------------------------------------------------------------------------------
//
//   SG Craft - Naquadah ore block
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.block.BlockOre;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;

import java.util.Random;

public class NaquadahOreBlock extends BlockOre
{

	static int texture = 0x40;

	public NaquadahOreBlock(int id)
	{
		super(id, texture);
		setTextureFile("/sgextensions/resources/textures.png");
		setHardness(5.0F);
		setResistance(10.0F);
		setStepSound(soundStoneFootstep);
		MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 3);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public int idDropped(int par1, Random par2Random, int par3)
	{
		return ConfigHandler.itemNaquadahID;
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 2 + random.nextInt(5);
	}

}
