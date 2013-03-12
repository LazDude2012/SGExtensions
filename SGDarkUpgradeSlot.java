package sgextensions;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SGDarkUpgradeSlot extends Slot {
	
	private ItemStack[] AllowedItems;

	public SGDarkUpgradeSlot(IInventory par1iInventory, int par2, int par3, int par4)
	{
		super(par1iInventory, par2, par3, par4);
	}
	
	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
	{
		int itemType = par1ItemStack.itemID;
    	int itemMeta = par1ItemStack.getItemDamage();
    	if(AllowedItems.length >= 0)
    	{
    		for(int i=0;i<AllowedItems.length;i++)
    		{
    			if(itemType == AllowedItems[i].itemID && itemMeta == AllowedItems[i].getItemDamage())
    			{
    				return true;
    			}
    		}
    		return false;
    	}
    	else
    	{
    		return true;
    	}
	}
	
	public void setAllowedItemArray(ItemStack[] Allowed)
	{
		AllowedItems = Allowed;
	}

}
