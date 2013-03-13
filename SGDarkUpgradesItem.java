package sgextensions;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SGDarkUpgradesItem extends Item
{
	private final static String[] subNames = {"Stargate Upgrade - Fast Dial", "Stargate Upgrade - Safe Dial", "Stargate Upgrade - Iris"};
	private final static int[] subIcons = {71,72,70};
	private final static String[] subInfo = {
		"Allows instant dialling#at 10 * energy cost",
		"Allows dialling with no kawoosh#at 4 * energy cost",
		"Allows computer controlled iris"
	};
	
	public boolean isUpgradeType(String Name, int Damage)
	{
		if(subNames[Damage] == Name)
		{
			return true;
		}
		return false;
	}
	
	public ItemStack getUpgrade(String Name)
	{
		ItemStack retIS = new ItemStack(this);
		for(int i = 0;i<subNames.length;i++)
		{
			if(Name == subNames[i])
			{
				retIS.setItemDamage(i);
			}
		}
		return retIS;
	}
	
	public SGDarkUpgradesItem(int par1)
	{
		super(par1);
		setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabMisc);
	}
	
	@Override
	public int getIconFromDamage(int meta)
	{
		return subIcons[meta];
	}
	
	@Override
	public int getMetadata (int damageValue)
	{
		return damageValue;
	}
	
	@Override
	public String getItemNameIS(ItemStack IS)
	{
		IS.setItemName(subNames[IS.getItemDamage()]);
		return subNames[IS.getItemDamage()];
	}
	
	@Override
	public String getTextureFile()
	{
		return "/sgextensions/resources/textures.png";
	}
	
	@Override
	public void addInformation(ItemStack IS, EntityPlayer player, List data,boolean Huh)
	{
		int damage = IS.getItemDamage();
		String text = subInfo[damage];
		String[] result = text.split("#");
		for (int i=0;i<result.length;i++)
		{
			data.add(result[i]);
		}
	}
	
	public void getSubItems(int par1, CreativeTabs tab, List subItems)
	{
		for (int ix = 0; ix < subNames.length; ix++)
		{
			subItems.add(new ItemStack(this, 1, ix));
		}
	}

}
