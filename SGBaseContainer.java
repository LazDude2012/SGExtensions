//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate base gui container
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SGBaseContainer extends BaseContainer
{

	static final int numUpdradeSlotColumns = 1;
	static final int upgradeSlotsX = 10;
	static final int upgradeSlotsY = 124;		
	static final int numFuelSlotColumns = 2;
	static final int fuelSlotsX = 174;
	static final int fuelSlotsY = 84;
	static final int playerSlotsX = 48;
	static final int playerSlotsY = 124;
	static final String[] upgradeList = {"Stargate Upgrade - Iris","Stargate Upgrade - Safe Dial","Stargate Upgrade - Fast Dial"};
	
	private int tx = 0;

	SGBaseTE te;
	
	public SGBaseContainer(EntityPlayer player, SGBaseTE te)
	{
		this.te = te;
		tx = 0;
		addFuelSlots();
		addUpgradeSlots();
		addPlayerSlots(player, playerSlotsX, playerSlotsY);
	}


	public static SGBaseContainer create(EntityPlayer player, World world, int x, int y, int z)
	{
		SGBaseTE te = SGBaseTE.at(world, x, y, z);
		if (te != null)
			return new SGBaseContainer(player, te);
		else
			return null;
	}
	
	void addFuelSlots()
	{
		int n = te.fuelSlots;
		//System.out.printf("SGBaseContainer: %s fuel slots\n", n);
		for (int i = 0; i < n; i++)
		{
			int row = i / numFuelSlotColumns;
			int col = i % numFuelSlotColumns;
			int x = fuelSlotsX + col * 18;
			int y = fuelSlotsY + row * 18;
			//System.out.printf("SGBaseContainer: adding fuel slot %s at (%s, %s)\n", i, x, y);
			Slot TSlot = new SGDarkUpgradeSlot(te, tx, x, y);
			((SGDarkUpgradeSlot)TSlot).setAllowedItemArray(new ItemStack[] {new ItemStack(SGExtensions.stargateFuel)});
			addSlotToContainer(TSlot);
			tx++;
		}
	}
	
	void addUpgradeSlots()
	{
		int n = te.upgradeSlots;
		for (int i = 0; i < n; i++)
		{
			int row = i / numUpdradeSlotColumns;
			int col = i % numUpdradeSlotColumns;
			int x = upgradeSlotsX + col * 18;
			int y = upgradeSlotsY + row * 18;
			Slot TSlot = new SGDarkUpgradeSlot(te, tx, x, y);
			ItemStack TI = ((SGDarkUpgradesItem) (SGExtensions.sgDarkUpgrades)).getUpgrade(upgradeList[i]);
			((SGDarkUpgradeSlot)TSlot).setAllowedItemArray(new ItemStack[] {TI});
			addSlotToContainer(TSlot);
			tx++;
		}
	}

	@Override
	void sendStateTo(ICrafting crafter)
	{
		crafter.sendProgressBarUpdate(this, 0, te.fuelBuffer);
	}

	@Override
	public void updateProgressBar(int i, int value)
	{
		switch (1)
		{
			case 0:
				te.fuelBuffer = value;
				break;
		}
	}

}
