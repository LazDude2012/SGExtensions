//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic Tile Entity
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public class BaseTileEntity extends TileEntity
{

	@Override
	public Packet getDescriptionPacket()
	{
		//System.out.printf("BaseTileEntity.getDescriptionPacket for %s\n", this);
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
	{
		//System.out.printf("BaseTileEntity.onDataPacket for %s\n", this);
		readFromNBT(pkt.customParam1);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public Trans3 localToGlobalTransformation()
	{
		return getBaseBlock().localToGlobalTransformation(worldObj, xCoord, yCoord, zCoord);
	}

	public BaseBlock getBaseBlock()
	{
		return (BaseBlock) getBlockType();
	}

	public void markBlockForUpdate()
	{
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void playSoundEffect(String name, float volume, float pitch)
	{
		worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, name, volume, pitch);
	}

	void onAddedToWorld()
	{
	}

	IInventory getInventory()
	{
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		IInventory inventory = getInventory();
		if (inventory != null)
		{
			NBTTagList list = nbt.getTagList("inventory");
			int n = list.tagCount();
			for (int i = 0; i < n; i++)
			{
				NBTTagCompound item = (NBTTagCompound) list.tagAt(i);
				int slot = item.getInteger("slot");
				ItemStack stack = ItemStack.loadItemStackFromNBT(item);
				inventory.setInventorySlotContents(slot, stack);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		IInventory inventory = getInventory();
		if (inventory != null)
		{
			NBTTagList list = new NBTTagList();
			int n = inventory.getSizeInventory();
			for (int i = 0; i < n; i++)
			{
				ItemStack stack = inventory.getStackInSlot(i);
				if (stack != null)
				{
					NBTTagCompound item = new NBTTagCompound();
					item.setInteger("slot", i);
					stack.writeToNBT(item);
					list.appendTag(item);
				}
			}
			nbt.setTag("inventory", list);
		}
	}

//------------------------------------- IInventory -----------------------------------------

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory()
	{
		IInventory inventory = getInventory();
		return (inventory != null) ? inventory.getSizeInventory() : 0;
	}

	/**
	 * Returns the stack in slot i
	 */
	public ItemStack getStackInSlot(int slot)
	{
		IInventory inventory = getInventory();
		return (inventory != null) ? inventory.getStackInSlot(slot) : null;
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
	 * new stack.
	 */
	public ItemStack decrStackSize(int slot, int amount)
	{
		IInventory inventory = getInventory();
		if (inventory != null)
		{
			ItemStack result = inventory.decrStackSize(slot, amount);
			onInventoryChanged();
			return result;
		} else
			return null;
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
	 * like when you close a workbench GUI.
	 */
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		IInventory inventory = getInventory();
		if (inventory != null)
		{
			ItemStack result = inventory.getStackInSlotOnClosing(slot);
			onInventoryChanged();
			return result;
		} else
			return null;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		IInventory inventory = getInventory();
		if (inventory != null)
		{
			inventory.setInventorySlotContents(slot, stack);
			onInventoryChanged();
		}
	}

	/**
	 * Returns the name of the inventory.
	 */
	public String getInvName()
	{
		IInventory inventory = getInventory();
		return (inventory != null) ? inventory.getInvName() : "";
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
	 * this more of a set than a get?*
	 */
	public int getInventoryStackLimit()
	{
		IInventory inventory = getInventory();
		return (inventory != null) ? inventory.getInventoryStackLimit() : 0;
	}

	/**
	 * Called when an the contents of an Inventory change, usually
	 */
	//void onInventoryChanged();

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		IInventory inventory = getInventory();
		return (inventory != null) ? inventory.isUseableByPlayer(player) : true;
	}

	public void openChest()
	{
		IInventory inventory = getInventory();
		if (inventory != null)
			inventory.openChest();
	}

	public void closeChest()
	{
		IInventory inventory = getInventory();
		if (inventory != null)
			inventory.closeChest();
	}

//------------------------------------- ISidedInventory -----------------------------------------

	/**
	 * Get the start of the side inventory.
	 *
	 * @param side The global side to get the start of range.
	 */
	int getStartInventorySide(ForgeDirection side)
	{
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory) inventory).getStartInventorySide(side);
		else
			return 0;
	}

	/**
	 * Get the size of the side inventory.
	 *
	 * @param side The global side.
	 */
	int getSizeInventorySide(ForgeDirection side)
	{
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory) inventory).getSizeInventorySide(side);
		else if (inventory != null)
			return inventory.getSizeInventory();
		else
			return 0;
	}

}
