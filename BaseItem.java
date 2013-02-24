//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic Textured Item
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class BaseItem extends Item
{

	String textureFile;

	public BaseItem(int id, String texture)
	{
		super(id);
		textureFile = texture;
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public String getTextureFile()
	{
		return textureFile;
	}

}
