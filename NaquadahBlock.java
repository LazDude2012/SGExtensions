//------------------------------------------------------------------------------------------------
//
//   SG Craft - Naquadah alloy block
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.block.BlockOreStorage;

public class NaquadahBlock extends BlockOreStorage
{

	static int texture = 0x43;

	public NaquadahBlock(int id)
	{
		super(id, texture);
		setTextureFile("/sgextensions/resources/textures.png");
		setHardness(5.0F);
		setResistance(10.0F);
		setStepSound(soundMetalFootstep);
	}

}
