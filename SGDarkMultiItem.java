package sgextensions;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SGDarkMultiItem extends Item
{
	private String[] subNames;
	private int[] subIcons;
	private String[] subInfo;
	
	public void setSubNames(String[] names)
	{
		subNames = names;
	}
	
	public void setSubIcons(int[] icons)
	{
		subIcons = icons;
	}
	
	public void setSubInfo(String[] info)
	{
		subInfo = info;
	}
	
	public boolean isUpgradeType(String Name, int Damage)
	{
		if(subNames[Damage] == Name)
		{
			return true;
		}
		return false;
	}
	
	public boolean isUpgradeType(String Name, ItemStack IS)
	{
		if(IS.getItem() instanceof SGDarkMultiItem)
		{
			int damage = IS.getItemDamage();
			if(subNames[damage] == Name)
			{
				return true;
			}
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
	
	public SGDarkMultiItem(int par1)
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
		if(subInfo.length > 0)
		{
			int damage = IS.getItemDamage();
			String text = subInfo[damage];
			if(text != null)
			{
				String[] result = text.split("#");
				for (int i=0;i<result.length;i++)
				{
					data.add(result[i]);
				}
			}
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
