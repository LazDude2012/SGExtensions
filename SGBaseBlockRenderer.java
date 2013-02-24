//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate base block renderer
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class SGBaseBlockRenderer extends BaseBlockRenderer
{

	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block,
	                                int modelId, RenderBlocks rb)
	{
		SGBaseBlock baseBlock = (SGBaseBlock) block;
		if (rb.overrideBlockTexture >= 0 || !baseBlock.isMerged(world, x, y, z))
			return super.renderWorldBlock(world, x, y, z, block, modelId, rb);
		else
			return false;
	}

}
