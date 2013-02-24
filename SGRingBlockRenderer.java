//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate ring block renderer
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class SGRingBlockRenderer extends BaseBlockRenderer
{

	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block,
	                                int modelId, RenderBlocks rb)
	{
		SGRingBlock ringBlock = (SGRingBlock) block;
		if (rb.overrideBlockTexture >= 0 || !ringBlock.isMerged(world, x, y, z))
			return super.renderWorldBlock(world, x, y, z, block, modelId, rb);
		else
			return false;
	}

}
