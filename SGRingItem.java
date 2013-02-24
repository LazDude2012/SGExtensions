//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate ring block item
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class SGRingItem extends ItemBlock
{

	public SGRingItem(int id)
	{
		super(id);
		setHasSubtypes(true);
	}

	@Override
	public int getIconFromDamage(int i)
	{
		return SGExtensions.sgRingBlock.getBlockTextureFromSideAndMetadata(0, i);
	}

	@Override
	public int getMetadata(int i)
	{
		return i;
	}

	@Override
	public String getItemNameIS(ItemStack stack)
	{
		String result = subItemName(stack.getItemDamage());
		//System.out.printf("SGRingItem.getItemNameIS: %s --> %s\n", stack, result);
		return result;
	}

	public static String subItemName(int i)
	{
		return "tile.gcewing.sg.stargateRing." + i;
	}

//	@Override
//	public String getLocalItemName(ItemStack par1ItemStack) {
//		String var2 = this.getItemNameIS(par1ItemStack);
//		System.out.printf("SGRingItem.getLocalItemName: key = %s\n", var2);
//		if (var2 == null)
//			return "";
//		String result = StatCollector.translateToLocal(var2);
//		System.out.printf("SGRingItem.getLocalItemName: result = %s\n", result);
//		return result;
//	}

}
