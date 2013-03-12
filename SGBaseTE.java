//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate base tile entity
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet41EntityEffect;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SGBaseTE extends BaseChunkLoadingTE implements IInventory
{

//	public final static String symbolChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ-";
//	public final static int numRingSymbols = 27;
//	public final static double ringAcceleration = 1.0;
//	final static double diallingRelaxationRate = 0.1;

	public final static String symbolChars = SGAddressing.symbolChars;
	public final static int numRingSymbols = SGAddressing.numSymbols;
	public final static double ringSymbolAngle = 360.0 / numRingSymbols;

	final static int diallingTime = 40; // ticks
	final static int quickDiallingTime = 5;
	final static int interDiallingTime = 10; // ticks
	final static int quickInterDiallingTime = 2;
	final static int transientDuration = 20; // ticks
	final static int disconnectTime = 30; // ticks

	final static double openingTransientIntensity = 1.3; //2.0;
	final static double openingTransientRandomness = 0.25;
	final static double closingTransientRandomness = 0.25;
	final static double transientDamageRate = 50;
	final static int fuelPerItem = SGExtensions.fuelAmount;
	final static int maxFuelBuffer = 2 * fuelPerItem;
	final static int fuelToOpen = fuelPerItem;
	final static int irisTimerVal = 2;

	static Random random = new Random();
	static DamageSource transientDamage = new TransientDamageSource();
	static DamageSource irisDamage = new irisDamageSource();

	public boolean isMerged;
	
	public int irisVarState;
	public String irisType = "iris";
	public int irisSlide;
	private int irisTimer;
	
	public SGState state = SGState.Idle;
	public double ringAngle, lastRingAngle, targetRingAngle; // degrees
	public int numEngagedChevrons;
	public String dialledAddress = "";

	//public String dialledAddress =  "MYNCRFT"; // "AAAAAAA";
	public boolean isLinkedToController;
	public int linkedX, linkedY, linkedZ;
	SGLocation connectedLocation;
	boolean isInitiator;
	int timeout;
	public int fuelBuffer;

	public boolean safeDial = false;
	public boolean quickDial = false;
	
	IInventory inventory = new InventoryBasic("Stargate", 4);
	final static int fuelSlot = 0;

	//ArrayList<PendingTeleportation> pendingTeleportations = new ArrayList<PendingTeleportation>();
	//public String homeAddress = "";

	double ehGrid[][][];
//	double ringVelocity; // degrees per tick

	@Override
	void onAddedToWorld()
	{
		//System.out.printf("SGBaseTE.onAddedToWorld\n");
		setForcedChunkRange(0, 0, 0, 0);
	}

	public static SGBaseTE at(IBlockAccess world, int x, int y, int z)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof SGBaseTE)
			return (SGBaseTE) te;
		else
			return null;
	}
	
	public String irisState()
	{
		//System.out.printf("SGBaseTE Iris State - %d\n", irisVarState);
		if(irisType == null)
		{
			return "Error - No Iris";
		}
		else
		{
			if(irisVarState == 0)
				return "Iris - Open";
			else if(irisVarState == 1)
				return "Iris - Closing";
			else if(irisVarState == 2)
				return "Iris - Closed";
			else if(irisVarState == 3)
				return "Iris - Opening";
		}
		return "Error - Unknown state";
	}
	
	public String openIris()
	{
		if(irisType != null)
		{
			if(irisVarState == 2)
			{
				irisVarState = 3;
				irisSlide = 0;
				irisTimer = irisTimerVal;
			}
			return "Iris opened";
		}
		return "Error - No iris";
	}
	
	public String closeIris()
	{
		System.out.printf("Stargate - Iris closing\n");
		if(irisType != null)
		{
			if(irisVarState == 0)
			{
				irisVarState = 1;
				irisSlide = 9;
				irisTimer = irisTimerVal;
			}
			return "Iris closed";
		}
		return "Error - No iris";
	}
	
	public String toggleIris()
	{
		if(irisType != null)
		{
			if(irisState() == "Iris - Open")
			{
				return closeIris();
			}
			else if(irisState() == "Iris - Closed")
			{
				return openIris();
			}
			else
			{
				return "Error - Iris moving";
			}
		}
		return "Error - No iris";
	}

	public static SGBaseTE at(SGLocation loc)
	{
		if (loc != null)
		{
			World world = DimensionManager.getWorld(loc.dimension);
			if (world != null)
				return SGBaseTE.at(world, loc.x, loc.y, loc.z);
		}
		return null;
	}

	public static SGBaseTE at(IBlockAccess world, NBTTagCompound nbt)
	{
		return SGBaseTE.at(world, nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
	}

	public int dimension()
	{
		if (worldObj != null)
			return worldObj.provider.dimensionId;
		else
			return -999;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		isMerged = nbt.getBoolean("isMerged");
		state = SGState.valueOf(nbt.getInteger("state"));
		targetRingAngle = nbt.getDouble("targetRingAngle");
		numEngagedChevrons = nbt.getInteger("numEngagedChevrons");
		//homeAddress = nbt.getString("homeAddress");
		dialledAddress = nbt.getString("dialledAddress");
		isLinkedToController = nbt.getBoolean("isLinkedToController");
		linkedX = nbt.getInteger("linkedX");
		linkedY = nbt.getInteger("linkedY");
		linkedZ = nbt.getInteger("linkedZ");
		irisVarState = nbt.getInteger("irisState");
		if (nbt.hasKey("connectedLocation"))
			connectedLocation = new SGLocation(nbt.getCompoundTag("connectedLocation"));
		isInitiator = nbt.getBoolean("isInitiator");
		timeout = nbt.getInteger("timeout");
		fuelBuffer = nbt.getInteger("fuelBuffer");
		//System.out.printf("SGBaseTE.readFromNBT: (%d; %d, %d, %d) state = %s(%d)\n",
		//	dimension(), xCoord, yCoord, zCoord, state, state.ordinal());
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		nbt.setBoolean("isMerged", isMerged);
		nbt.setInteger("state", state.ordinal());
		nbt.setDouble("targetRingAngle", targetRingAngle);
		nbt.setInteger("irisState", irisVarState);
		nbt.setInteger("numEngagedChevrons", numEngagedChevrons);
		//nbt.setString("homeAddress", homeAddress);
		nbt.setString("dialledAddress", dialledAddress);
		nbt.setBoolean("isLinkedToController", isLinkedToController);
		nbt.setInteger("linkedX", linkedX);
		nbt.setInteger("linkedY", linkedY);
		nbt.setInteger("linkedZ", linkedZ);
		if (connectedLocation != null)
			nbt.setCompoundTag("connectedLocation", connectedLocation.toNBT());
		nbt.setBoolean("isInitiator", isInitiator);
		nbt.setInteger("timeout", timeout);
		nbt.setInteger("fuelBuffer", fuelBuffer);
		if (!worldObj.isRemote)
		{
			//System.out.printf("SGBaseTE.writeToNBT: (%d; %d, %d, %d) state = %s\n",
			//	dimension(), xCoord, yCoord, zCoord, nbt.getInteger("state"));
		}
	}

	public NBTTagCompound nbtWithCoords()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("x", xCoord);
		nbt.setInteger("y", yCoord);
		nbt.setInteger("z", zCoord);
		return nbt;
	}

	static boolean isValidSymbolChar(String c)
	{
		return SGAddressing.isValidSymbolChar(c);
	}

	static char symbolToChar(int i)
	{
		return SGAddressing.symbolToChar(i);
	}

	static int charToSymbol(char c)
	{
		return SGAddressing.charToSymbol(c);
	}

	static int charToSymbol(String c)
	{
		return SGAddressing.charToSymbol(c);
	}

	public String getHomeAddress() throws SGAddressing.AddressingError
	{
		return SGAddressing.addressForLocation(new SGLocation(this));
	}

	public SGBaseBlock getBlock()
	{
		return (SGBaseBlock) getBlockType();
	}

	public int getRotation()
	{
		return getBlockMetadata() & SGBaseBlock.rotationMask;
	}

	public double interpolatedRingAngle(double t)
	{
		return Utils.interpolateAngle(lastRingAngle, ringAngle, t);
	}

	@Override
	public boolean canUpdate()
	{
		return true;
	}

	@Override
	public void updateEntity()
	{
		if (worldObj.isRemote)
			clientUpdate();
		else
			serverUpdate();
	}

	String side()
	{
		return worldObj.isRemote ? "Client" : "Server";
	}

	void enterState(SGState newState, int newTimeout)
	{
		//System.out.printf("SGBaseTE: %s entering state %s with timeout %s\n",
				//side(), newState, newTimeout);
		state = newState;
		timeout = newTimeout;
		onInventoryChanged();
		markBlockForUpdate();
	}

	public boolean isConnected()
	{
		return state == SGState.Transient || state == SGState.Connected || state == SGState.Disconnecting;
	}

//	public void setHomeAddress(String newAddress) {
//		System.out.printf("SGBaseTE.setHomeAddress to %s in %s\n", newAddress, worldObj);
//		if (worldObj.isRemote) {
//			homeAddress = newAddress;
//			SGChannel.sendSetHomeAddressToServer(this, newAddress);
//		}
//		else {
//			if (homeAddress != newAddress) {
//				System.out.printf("SGBaseTE.setHomeAddress: Updating address\n");
//				homeAddress = newAddress;
//				SGLocation location = new SGLocation(this);
//				SGGlobal.setLocationForAddress(homeAddress, location);
//				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
//			}
//		}
//	}

	SGControllerTE getLinkedControllerTE()
	{
		if (isLinkedToController)
		{
			TileEntity cte = worldObj.getBlockTileEntity(linkedX, linkedY, linkedZ);
			if (cte instanceof SGControllerTE)
				return (SGControllerTE) cte;
		}
		return null;
	}

	void checkForLink()
	{
		int range = SGControllerTE.linkRangeZ;
		for (int i = -range; i <= range; i++)
			for (int j = -range; j <= range; j++)
				for (int k = -range; k <= range; k++)
				{
					TileEntity te = worldObj.getBlockTileEntity(xCoord + i, yCoord + j, zCoord + k);
					if (te instanceof SGControllerTE)
						((SGControllerTE) te).checkForLink();
				}
	}

	public void unlinkFromController()
	{
		if (isLinkedToController)
		{
			SGControllerTE cte = getLinkedControllerTE();
			if (cte != null)
				cte.clearLinkToStargate();
			clearLinkToController();
		}
	}

	public void clearLinkToController()
	{
		//System.out.printf("SGBaseTE: Unlinking stargate at (%d, %d, %d) from controller\n",
				//xCoord, yCoord, zCoord);
		isLinkedToController = false;
		//markBlockForUpdate();
		onInventoryChanged();
	}

	//------------------------------------   Server   --------------------------------------------
	
	public String ControlledDisconnect()
	{
		if(state != SGState.Idle)
		{
			boolean canDisconnect = true; //isInitiator;
			SGBaseTE dte = getConnectedStargateTE();
			boolean validConnection =
					(dte != null) && (dte.getConnectedStargateTE() == this);
			if (canDisconnect || !validConnection)
			{
				if (state != SGState.Disconnecting)
					 return disconnect();
			} 
			else if (!canDisconnect)
				return "Error - Not initiator";
		}
		return "Error - Not active";
	}
	
	public String connectOrDisconnect(String address, EntityPlayer player)
	{
		//System.out.printf("SGBaseTE: %s: connectOrDisconnect('%s') in state %s by %s\n",
				//side(), address, state, player);
		if (state == SGState.Idle)
		{
			if (address.length() == SGAddressing.addressLength)
				return connect(address, player);
			else
				return "Error - Invalid Address";
		} 
		else
		{
			boolean canDisconnect = true; //isInitiator;
			SGBaseTE dte = getConnectedStargateTE();
			boolean validConnection =
					(dte != null) && (dte.getConnectedStargateTE() == this);
			if (canDisconnect || !validConnection)
			{
				if (state != SGState.Disconnecting)
					 return disconnect();
			} 
			else if (!canDisconnect)
				return "Error - Not initiator";
				//System.out.printf("SGBaseTE.connectOrDisconnect: Not initiator\n");
		}
		return "Error - Unknown #001";
	}

	String connect(String address, EntityPlayer player)
	{
		String homeAddress = findHomeAddress();
		SGBaseTE dte = SGAddressing.findAddressedStargate(address);
		//System.out.printf("SGBaseTE.connect: addressed TE = %s\n", dte);
		if (dte == null)
		{
			diallingFailure(player, "No stargate at address " + address);
			return "Error - No stargate at address " + address;
		}
		//System.out.printf("SGBaseTE.connect: addressed TE state = %s\n", dte.state);
		if (dte.state != SGState.Idle)
		{
			diallingFailure(player, "Stargate at address " + address + " is busy");
			return "Error - Stargate at address " + address + " is busy";
		}
		if (!reloadFuel(fuelToOpen))
		{
			diallingFailure(player, "Stargate has insufficient fuel");
			return "Error - Stargate has insufficient fuel";
		}
		safeDial = safeDial || shouldSafeDial();
		quickDial = quickDial || shouldQuickDial();
		dte.safeDial = this.safeDial || dte.shouldSafeDial();
		dte.quickDial = this.quickDial;
		startDiallingStargate(address, dte, true);
		dte.startDiallingStargate(homeAddress, this, false);
		return "Dialling";
	}

	void diallingFailure(EntityPlayer player, String mess)
	{
		if(player != null)
			player.addChatMessage(mess);
		playSoundEffect("sgextensions.sg_abort", 1.0F, 1.0F);
	}

	String findHomeAddress()
	{
		String homeAddress;
		try
		{
			return getHomeAddress();
		}
		catch (SGAddressing.AddressingError e)
		{
			//System.out.printf("SGBaseTE.findHomeAddress: %s\n", e);
			return "";
		}
	}

	public String disconnect()
	{
		//System.out.printf("SGBaseTE: %s: disconnect()\n", side());
		SGBaseTE dte = SGBaseTE.at(connectedLocation);
		if (dte != null)
			dte.clearConnection();
		clearConnection();
		return "Disconncted";
	}

	public void clearConnection()
	{
		if (state != SGState.Idle || connectedLocation != null)
		{
			//System.out.printf("SGBaseTE.clearConnection: Resetting state\n");
			dialledAddress = "";
			connectedLocation = null;
			isInitiator = false;
			numEngagedChevrons = 0;
			onInventoryChanged();
			markBlockForUpdate();
			if (state == SGState.Connected)
			{
				enterState(SGState.Disconnecting, disconnectTime);
				//sendClientEvent(SGEvent.StartDisconnecting, 0);
				playSoundEffect("sgextensions.sg_close", 1.0F, 1.0F);
			} 
			else
			{
				if (state != SGState.Idle && state != SGState.Disconnecting)
					playSoundEffect("sgextensions.sg_abort", 1.0F, 1.0F);
				enterState(SGState.Idle, 0);
				//sendClientEvent(SGEvent.FinishDisconnecting, 0);
			}
		}
	}

	void startDiallingStargate(String address, SGBaseTE dte, boolean initiator)
	{
		//System.out.printf("SGBaseTE.startDiallingStargate %s, initiator = %s\n",
				//dte, initiator);
		dialledAddress = address;
		connectedLocation = new SGLocation(dte);
		isInitiator = initiator;
		//markBlockForUpdate();
		onInventoryChanged();
		if(quickDial == false)
		{
			startDiallingNextSymbol();
		}
		else
		{
			numEngagedChevrons = SGAddressing.addressLength;
			finishDiallingAddress();
		}
	}

	void serverUpdate()
	{
		if (isMerged)
		{
			//performPendingTeleportations();
			fuelUsage();
			
			if(irisState() == "Iris - Opening" || irisState() == "Iris - Closing")
			{
				irisTimer--;
				if(irisTimer <= 0)
				{
					System.out.printf("Iris Slide: (%d)\n", irisSlide);
					irisTimer = irisTimerVal;
					if(irisState() == "Iris - Opening")
					{
						irisSlide++;
						if(irisSlide >= 10)
						{
							irisVarState = 0;
						}
					}
					else
					{
						irisSlide --;
						if(irisSlide == 0)
						{
							irisVarState = 2;
						}
					}
				}
			}
			
			if (timeout > 0)
			{
				//int dimension = worldObj.provider.dimensionId;
				//System.out.printf(
				//	"SGBaseTE.serverUpdate: (%d, %d, %d, %d) state %s, timeout %s\n",
				//		dimension, xCoord, yCoord, zCoord, state, timeout);
				if (state == SGState.Transient)
					performTransientDamage();
				--timeout;
			} 
			else switch (state)
			{
				case Idle:
					if (undialledDigitsRemaining())
						startDiallingNextSymbol();
					break;
				case Dialling:
					finishDiallingSymbol();
					break;
				case InterDialling:
					startDiallingNextSymbol();
					break;
				case Transient:
					enterState(SGState.Connected, 20*60*SGExtensions.maxOpenTime);
					//markBlockForUpdate();
					break;
				case Disconnecting:
					//sendClientEvent(SGEvent.FinishDisconnecting, 0);
					enterState(SGState.Idle, 0);
					//markBlockForUpdate();
					break;
				case Connected:
					disconnect();
					break;
			}
		}
	}

	void fuelUsage()
	{
		if (state == SGState.Connected && isInitiator)
			if (!useFuel(1))
				disconnect();
	}

	boolean useFuel(int amount)
	{
		//System.out.printf("SGBaseTE.useFuel: %d\n", amount);
		if (reloadFuel(amount))
		{
			setFuelBuffer(fuelBuffer - amount);
			return true;
		} else
			return false;
	}

	boolean reloadFuel(int amount)
	{
		while (fuelBuffer < amount && fuelBuffer + fuelPerItem <= maxFuelBuffer)
		{
			if (useFuelItem())
				setFuelBuffer(fuelBuffer + fuelPerItem);
			else
				break;
		}
		return fuelBuffer >= amount;
	}

	boolean useFuelItem()
	{
		int n = getSizeInventory();
		for (int i = n - 1; i >= 0; i--)
		{
			ItemStack stack = getStackInSlot(i);
			if (stack != null && stack.getItem() == SGExtensions.naquadah && stack.stackSize > 0)
			{
				decrStackSize(i, 1);
				return true;
			}
		}
		return false;
	}

	void setFuelBuffer(int amount)
	{
		if (fuelBuffer != amount)
		{
			fuelBuffer = amount;
			onInventoryChanged();
			//System.out.printf("SGBaseTE: Fuel level now %d\n", fuelBuffer);
		}
	}

	void performTransientDamage()
	{
		Trans3 t = localToGlobalTransformation();
		Vector3 p0 = t.p(-1.5, 0.5, 0.5);
		Vector3 p1 = t.p(1.5, 3.5, 5.5);
		Vector3 q0 = p0.min(p1);
		Vector3 q1 = p0.max(p1);
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(q0.x, q0.y, q0.z, q1.x, q1.y, q1.z);
		//System.out.printf("SGBaseTE.performTransientDamage: players in world:\n");
		//for (Entity ent : (List<Entity>)worldObj.loadedEntityList)
		//	if (ent instanceof EntityPlayer)
		//		System.out.printf("--- %s\n", ent);
		//System.out.printf("SGBaseTE.performTransientDamage: box = %s\n", box);
		List<EntityLiving> ents = worldObj.getEntitiesWithinAABB(EntityLiving.class, box);
		//System.out.printf("SGBaseTE.performTransientDamage: entities in box:\n", box);
		for (EntityLiving ent : ents)
		{
			Vector3 ep = new Vector3(ent.posX, ent.posY, ent.posZ);
			Vector3 gp = t.p(0, 2, 0.5);
			double dist = ep.distance(gp);
			//System.out.printf("SGBaseTE.performTransientDamage: found %s\n", ent);
			if (dist > 1.0)
				dist = 1.0;
			int damage = (int) Math.ceil(dist * transientDamageRate);
			//System.out.printf("SGBaseTE.performTransientDamage: distance = %s, damage = %s\n",
					//dist, damage);
			ent.attackEntityFrom(transientDamage, damage);
		}
	}

//	void performPendingTeleportations() {
//		for (PendingTeleportation port : pendingTeleportations)
//			port.perform();
//		pendingTeleportations.clear();
//	}

	boolean undialledDigitsRemaining()
	{
		int n = numEngagedChevrons;
		return n < 7 && n < dialledAddress.length();
	}

	void startDiallingNextSymbol()
	{
		startDiallingSymbol(dialledAddress.charAt(numEngagedChevrons));
	}

	void startDiallingSymbol(char c)
	{
		int i = Character.getNumericValue(c) - Character.getNumericValue('A');
		if (i >= 0 && i < numRingSymbols)
		{
			startDiallingToAngle(i * ringSymbolAngle - 45 * numEngagedChevrons);
			playSoundEffect("sgextensions.sg_dial", 1.0F, 1.0F);
		}
	}

	void startDiallingToAngle(double a)
	{
		targetRingAngle = Utils.normaliseAngle(a);
		//sendClientEvent(SGEvent.StartDialling, (int)(targetRingAngle * 1000));
		if(quickDial == true)
		{
			enterState(SGState.Dialling, quickDiallingTime);
		}
		else
		{
			enterState(SGState.Dialling, diallingTime);
		}
	}

	void finishDiallingSymbol()
	{
		++numEngagedChevrons;
		//sendClientEvent(SGEvent.FinishDialling, numEngagedChevrons);
		if (numEngagedChevrons == SGAddressing.addressLength)
			finishDiallingAddress();
		else if (undialledDigitsRemaining())
			//startDiallingNextSymbol();
			if(quickDial == true)
			{
				enterState(SGState.InterDialling, quickInterDiallingTime);
			}
			else
			{
				enterState(SGState.InterDialling, interDiallingTime);
			}
		else
			enterState(SGState.Idle, 0);
	}
	
	boolean shouldSafeDial()
	{
		if(irisState() != "Iris - Open" && irisState() != "Error - No iris")
		{
			return true;
		}
		return false;
	}
	
	boolean shouldQuickDial()
	{
		return false;
	}

	void finishDiallingAddress()
	{
		//System.out.printf("SGBaseTE: Connecting to '%s'\n", dialledAddress);
		if (!isInitiator || useFuel(fuelToOpen))
		{
			//sendClientEvent(SGEvent.Connect, 0);
			if(safeDial == true)
			{
				System.out.printf("SGBaseTE: Safe dial\n");
				enterState(SGState.Connected, 20*60*SGExtensions.maxOpenTime);
				safeDial = shouldSafeDial();
				quickDial = shouldQuickDial();
			}
			else
			{
				System.out.printf("SGBaseTE: Unsafe dial\n");
				enterState(SGState.Transient, transientDuration);
			}
			playSoundEffect("sgextensions.sg_open", 1.0F, 1.0F);
		}
		else
		{
			//enterState(SGState.Idle, 0);
			//playSoundEffect("gcewing.sg.sg_abort", 1.0F, 1.0F);
			disconnect();
		}
	}

//	void sendClientEvent(SGEvent type, int data) {
//		System.out.printf("SGBaseTE.sendClientEvent: %s, %d\n", type, data);
//		worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType().blockID, type.ordinal(), data);
//	}

	public void entityInPortal(Entity entity)
	{
		if (state == SGState.Connected && irisState() != "Iris - Closed")
		{
			//System.out.printf("SGBaseTE.entityInPortal: global (%.3f, %.3f, %.3f)\n",
			//	entity.posX, entity.posY, entity.posZ);
			Trans3 t = localToGlobalTransformation();
			//System.out.printf("SGBaseTE.entityInPortal: Transformation:\n");
			//t.dump();
			Vector3 p1 = t.ip(entity.posX, entity.posY, entity.posZ);
			Vector3 p0 = t.ip(entity.prevPosX, entity.prevPosY, entity.prevPosZ);
			//System.out.printf("SGBaseTE.entityInPortal: local (%.3f, %.3f, %.3f)\n", p1.x, p1.y, p1.z);
			//System.out.printf("SGBaseTE.entityInPortal: prev local position = %s\n", p0);
			//System.out.printf("SGBaseTE.entityInPortal: z0 = %.3f z1 = %.3f\n", p0.z, p1.z);
			if (p0.z >= 0.0 && p1.z < 0.0)
			{
				//System.out.printf("SGBaseTE.entityInPortal: Passed through event horizon of stargate at (%d,%d,%d) in %s\n",
						//xCoord, yCoord, zCoord, worldObj);
				SGBaseTE dte = getConnectedStargateTE();
				if (dte != null)
				{
					if(dte.irisState() != "Iris - Closed")
					{
						Trans3 dt = dte.localToGlobalTransformation();
						teleportEntity(entity, t, dt, connectedLocation.dimension);
					}
					else if(entity instanceof EntityPlayerMP)
					{
						if(SGExtensions.irisKillClearInv)
						{
							((EntityPlayerMP)entity).inventory.clearInventory(-1, -1);
						}
						((EntityPlayerMP)entity).attackEntityFrom(irisDamage, 1000);
					}
					else
					{
						entity.setDead();
					}
				}
			}
		}
	}

	void teleportEntity(Entity entity, Trans3 t1, Trans3 t2, int dimension)
	{
		//System.out.printf(
		//	"SGBaseTE.teleportEntity: old (%.3f, %.3f, %.3f) velocity (%.3f, %.3f, %.3f) yaw %.2f\n",
		//	entity.posX, entity.posY, entity.posZ, entity.motionX, entity.motionY, entity.motionZ,
		//	entity.rotationYaw);
		Vector3 p = t1.ip(entity.posX, entity.posY, entity.posZ); // local position
		Vector3 v = t1.iv(-entity.motionX, -entity.motionY, -entity.motionZ); // new local velocity
		Vector3 r = t1.iv(yawVector(entity)); // local facing
		Vector3 q = t2.p(p); // new global position
		Vector3 u = t2.v(v); // new global velocity
		Vector3 s = t2.v(r.mul(-1)); // new global facing
		double a = yawAngle(s); // new global yaw angle
		//System.out.printf(
		//	"SGBaseTE.teleportEntity: new (%.3f, %.3f, %.3f) velocity (%.3f, %.3f, %.3f) yaw %.2f\n",
		//	q.x, q.y, q.z, u.x, u.y, u.z, a);
		//pendingTeleportations.add(new PendingTeleportation(entity, q, u, a));
		if (entity.dimension == dimension)
			teleportWithinDimension(entity, q, u, a);
		else
			teleportToOtherDimension(entity, q, u, a, dimension);
	}

	void teleportWithinDimension(Entity entity, Vector3 p, Vector3 v, double a)
	{
		setVelocity(entity, v);
		if (entity instanceof EntityLiving)
		{
			entity.rotationYaw = (float) a;
			((EntityLiving) entity).setPositionAndUpdate(p.x, p.y, p.z);
		} else
			entity.setLocationAndAngles(p.x, p.y, p.z, (float) a, entity.rotationPitch);
	}

	void teleportToOtherDimension(Entity entity, Vector3 p, Vector3 v, double a, int dimension)
	{
		if (entity instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP) entity;
			Vector3 q = p.add(yawVector(a));
			transferPlayerToDimension(player, dimension, q, a);
			//player.rotationYaw = (float)a;
			//player.setVelocity(v.x, v.y, v.z);
			//player.setPositionAndUpdate(q.x, q.y, q.z);
		} else
			teleportEntityToDimension(entity, p, v, a, dimension);
	}

	//<<<

	static void transferPlayerToDimension(EntityPlayerMP player, int newDimension, Vector3 p, double a)
	{
		MinecraftServer server = MinecraftServer.getServer();
		ServerConfigurationManager scm = server.getConfigurationManager();
		int oldDimension = player.dimension;
		player.dimension = newDimension;
		WorldServer oldWorld = server.worldServerForDimension(oldDimension);
		WorldServer newWorld = server.worldServerForDimension(newDimension);
		player.playerNetServerHandler.sendPacketToPlayer(new Packet9Respawn(player.dimension,
				(byte) player.worldObj.difficultySetting, newWorld.getWorldInfo().getTerrainType(),
				newWorld.getHeight(), player.theItemInWorldManager.getGameType()));
		oldWorld.removeEntity(player);
		player.isDead = false;
		newWorld.spawnEntityInWorld(player);
		player.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		newWorld.updateEntityWithOptionalForce(player, false);
		player.setWorld(newWorld);
		scm.func_72375_a(player, oldWorld);
		player.playerNetServerHandler.setPlayerLocation(p.x, p.y, p.z, (float) a, player.rotationPitch);
		player.theItemInWorldManager.setWorld(newWorld);
		scm.updateTimeAndWeatherForPlayer(player, newWorld);
		scm.syncPlayerInventory(player);
		Iterator var6 = player.getActivePotionEffects().iterator();
		while (var6.hasNext())
		{
			PotionEffect effect = (PotionEffect) var6.next();
			player.playerNetServerHandler.sendPacketToPlayer(new Packet41EntityEffect(player.entityId, effect));
		}
		GameRegistry.onPlayerChangedDimension(player);
	}

	//>>>

	void teleportEntityToDimension(Entity oldEntity, Vector3 p, Vector3 v, double a, int dimension)
	{
		MinecraftServer server = MinecraftServer.getServer();
		WorldServer oldWorld = server.worldServerForDimension(oldEntity.dimension);
		WorldServer newWorld = server.worldServerForDimension(dimension);
		oldEntity.setDead();
		Entity newEntity = EntityList.createEntityByName(EntityList.getEntityString(oldEntity), newWorld);
		//System.out.printf("SGBaseTE.teleportEntityToDimension: newEntity = %s\n", newEntity);
		if (newEntity != null)
		{
			newEntity.copyDataFrom(oldEntity, true);
			setVelocity(newEntity, v);
			newEntity.setLocationAndAngles(p.x, p.y, p.z, (float) a, oldEntity.rotationPitch);
			checkChunk(newWorld, newEntity);
			newWorld.spawnEntityInWorld(newEntity);
		}
		oldWorld.resetUpdateEntityTick();
		newWorld.resetUpdateEntityTick();
	}

	static void setVelocity(Entity entity, Vector3 v)
	{
		entity.motionX = v.x;
		entity.motionY = v.y;
		entity.motionZ = v.z;
	}

	void checkChunk(World world, Entity entity)
	{
		int cx = MathHelper.floor_double(entity.posX / 16.0D);
		int cy = MathHelper.floor_double(entity.posZ / 16.0D);
		Chunk chunk = world.getChunkFromChunkCoords(cx, cy);
		//SGCraft.forceChunk(chunk);
		//System.out.printf("SGBaseTE.checkChunk: (%d, %d) chunk = %s\n", cx, cy, chunk);
	}

	Vector3 yawVector(Entity entity)
	{
		return yawVector(entity.rotationYaw);
	}

	Vector3 yawVector(double yaw)
	{
		double a = Math.toRadians(yaw);
		Vector3 v = new Vector3(-Math.sin(a), 0, Math.cos(a));
		//System.out.printf("SGBaseTE.yawVector: %.2f --> (%.3f, %.3f)\n", yaw, v.x, v.z);
		return v;
	}

	double yawAngle(Vector3 v)
	{
		double a = Math.atan2(-v.x, v.z);
		double d = Math.toDegrees(a);
		//System.out.printf("SGBaseTE.yawAngle: (%.3f, %.3f) --> %.2f\n", v.x, v.z, d);
		return d;
	}

	SGBaseTE getConnectedStargateTE()
	{
		if (connectedLocation != null)
			return connectedLocation.getStargateTE();
		else
			return null;
	}

	//------------------------------------   Client   --------------------------------------------

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt)
	{
		//System.out.printf("SGBaseTE.onDataPacket: with state %s numEngagedChevrons %s\n",
		//	SGState.valueOf(pkt.customParam1.getInteger("state")),
		//	pkt.customParam1.getInteger("numEngagedChevrons"));
		SGState oldState = state;
		super.onDataPacket(net, pkt);
		if (isMerged && state != oldState)
		{
			switch (state)
			{
				case Transient:
					initiateOpeningTransient();
					break;
				case Disconnecting:
					initiateClosingTransient();
					break;
			}
		}
	}

//	@Override
//	public void receiveClientEvent(int id, int data) {
//		if (worldObj.isRemote) {
//			SGEvent type = SGEvent.valueOf(id);
//			System.out.printf("SGBaseTE.receiveClientEvent: %s, %d in %s\n", type, data, worldObj);
//			switch (type) {
//				case StartDialling:
//					targetRingAngle = data / 1000.0;
//					enterState(SGState.Dialling, diallingTime);
//					break;
//				case FinishDialling:
//					numEngagedChevrons = data;
//					setRingAngle(targetRingAngle);
//					enterState(SGState.Idle, 0);
//					break;
//				case Connect:
//					initiateOpeningTransient();
//					enterState(SGState.Connected, 0);
//					break;
//				case StartDisconnecting:
//					numEngagedChevrons = 0;
//					initiateClosingTransient();
//					enterState(SGState.Disconnecting, 0);
//					break;
//				case FinishDisconnecting:
//					numEngagedChevrons = 0;
//					enterState(SGState.Idle, 0);
//					break;
//			}
//		}
//	}

	void clientUpdate()
	{
		lastRingAngle = ringAngle;
		applyRandomImpulse();
		updateEventHorizon();
		switch (state)
		{
			case Dialling:
				//System.out.printf("SGBaseTe: Relaxing angle %s towards %s at rate %s\n",
				//	ringAngle, targetRingAngle, diallingRelaxationRate);
				//setRingAngle(Utils.relaxAngle(ringAngle, targetRingAngle, diallingRelaxationRate));
				updateRingAngle();
				//System.out.printf("SGBaseTe: Ring angle now %s\n", ringAngle);
				break;
		}
	}

	void setRingAngle(double a)
	{
//		lastRingAngle = ringAngle;
		ringAngle = a;
	}

	void updateRingAngle()
	{
		if (timeout > 0)
		{
			double da = Utils.diffAngle(ringAngle, targetRingAngle) / timeout;
			setRingAngle(Utils.addAngle(ringAngle, da));
			--timeout;
		} else
			setRingAngle(targetRingAngle);
	}

	public double[][][] getEventHorizonGrid()
	{
		if (ehGrid == null)
		{
			int m = SGBaseTERenderer.ehGridRadialSize;
			int n = SGBaseTERenderer.ehGridPolarSize;
			ehGrid = new double[2][n + 2][m + 1];
			for (int i = 0; i < 2; i++)
			{
				ehGrid[i][0] = ehGrid[i][n];
				ehGrid[i][n + 1] = ehGrid[i][1];
			}
		}
		return ehGrid;
	}

	void initiateOpeningTransient()
	{
		double v[][] = getEventHorizonGrid()[1];
		int n = SGBaseTERenderer.ehGridPolarSize;
		for (int j = 0; j <= n + 1; j++)
		{
			v[j][0] = openingTransientIntensity;
			v[j][1] = v[j][0] + openingTransientRandomness * random.nextGaussian();
		}
	}

	void initiateClosingTransient()
	{
		double v[][] = getEventHorizonGrid()[1];
		int m = SGBaseTERenderer.ehGridRadialSize;
		int n = SGBaseTERenderer.ehGridPolarSize;
		for (int i = 1; i < m; i++)
			for (int j = 1; j <= n; j++)
				v[j][i] += closingTransientRandomness * random.nextGaussian();
	}

	void applyRandomImpulse()
	{
		double v[][] = getEventHorizonGrid()[1];
		int m = SGBaseTERenderer.ehGridRadialSize;
		int n = SGBaseTERenderer.ehGridPolarSize;
		int i = random.nextInt(m - 1) + 1;
		int j = random.nextInt(n) + 1;
		v[j][i] += 0.05 * random.nextGaussian();
	}

	void updateEventHorizon()
	{
		double grid[][][] = getEventHorizonGrid();
		double u[][] = grid[0];
		double v[][] = grid[1];
		int m = SGBaseTERenderer.ehGridRadialSize;
		int n = SGBaseTERenderer.ehGridPolarSize;
		double dt = 1.0;
		double asq = 0.03;
		double d = 0.95;
		for (int i = 1; i < m; i++)
			for (int j = 1; j <= n; j++)
			{
				double du_dr = 0.5 * (u[j][i + 1] - u[j][i - 1]);
				double d2u_drsq = u[j][i + 1] - 2 * u[j][i] + u[j][i - 1];
				double d2u_dthsq = u[j + 1][i] - 2 * u[j][i] + u[j - 1][i];
				v[j][i] = d * v[j][i] + (asq * dt) * (d2u_drsq + du_dr / i + d2u_dthsq / (i * i));
			}
		for (int i = 1; i < m; i++)
			for (int j = 1; j <= n; j++)
				u[j][i] += v[j][i] * dt;
		double u0 = 0, v0 = 0;
		for (int j = 1; j <= n; j++)
		{
			u0 += u[j][1];
			v0 += v[j][1];
		}
		u0 /= n;
		v0 /= n;
		for (int j = 1; j <= n; j++)
		{
			u[j][0] = u0;
			v[j][0] = v0;
		}
		//dumpGrid("u", u);
		//dumpGrid("v", v);
	}

	void dumpGrid(String label, double g[][])
	{
		//System.out.printf("SGBaseTE: %s:\n", label);
		int m = SGBaseTERenderer.ehGridRadialSize;
		int n = SGBaseTERenderer.ehGridPolarSize;
		for (int j = 0; j <= n + 1; j++)
		{
			for (int i = 0; i <= m; i++)
				System.out.printf(" %6.3f", g[j][i]);
			System.out.printf("\n");
		}
	}

	@Override
	BaseTEChunkManager getChunkManager()
	{
		return SGExtensions.chunkManager;
	}

	@Override
	IInventory getInventory()
	{
		return inventory;
	}

}

//------------------------------------------------------------------------------------------------

//enum SGEvent {
//	Unknown, StartDialling, FinishDialling, Connect, StartDisconnecting, FinishDisconnecting;
//	
//	static SGEvent[] VALUES = values();
//	
//	public static SGEvent valueOf(int i) {
//		try {
//			return VALUES[i];
//		}
//		catch (IndexOutOfBoundsException e) {
//			return Unknown;
//		}
//	}
//
//}

//------------------------------------------------------------------------------------------------

class DummyTeleporter extends Teleporter
{

	public DummyTeleporter(WorldServer world)
	{
		super(world);
	}

	public void placeInPortal(Entity par1Entity, double par2, double par4, double par6, float par8)
	{
	}

	public boolean placeInExistingPortal(Entity par1Entity, double par2, double par4, double par6, float par8)
	{
		return true;
	}

}

//------------------------------------------------------------------------------------------------

class TransientDamageSource extends DamageSource
{

	public TransientDamageSource()
	{
		super("sgTransient");
	}

	public String getDeathMessage(EntityPlayer player)
	{
		return player.username + " was torn apart by an event horizon";
	}

}

class irisDamageSource extends DamageSource
{

	public irisDamageSource()
	{
		super("sgIris");
	}

	public String getDeathMessage(EntityPlayer player)
	{
		return player.username + " walked into an iris";
	}

}

//------------------------------------------------------------------------------------------------

//class PendingTeleportation {
//
//	Entity entity;
//	Vector3 v;
//	Vector3 p;
//	double a;
//	
//	public PendingTeleportation(Entity ent, Vector3 pos, Vector3 vel, double ang) {
//		entity = ent;
//		p = pos;
//		v = vel;
//		a = ang;
//	}
//
//	public void perform() {
//		System.out.printf("PendingTeleportation.perform: to pos (%.3f, %.3f, %.3f) vel (%.3f, %.3f, %.3f) yaw %.2f\n",
//			p.x, p.y, p.z, v.x, v.y, v.z, a);
//		entity.setVelocity(p.x, p.y, p.z);
//		if (entity instanceof EntityLiving) {
//			entity.rotationYaw = (float)a;
//			((EntityLiving)entity).setPositionAndUpdate(p.x, p.y, p.z);
//		}
//		else
//			entity.setLocationAndAngles(p.x, p.y, p.z, (float)a, entity.rotationPitch);
//	}
//	
//}
