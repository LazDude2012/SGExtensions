//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate base gui screen
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SGBaseScreen extends SGScreen
{

	static String screenTitle = "Stargate Address";
	static final int guiWidth = 256;
	static final int guiHeight = 208;
	static final int fuelGaugeWidth = 16;
	static final int fuelGaugeHeight = 34;
	static final int fuelGaugeX = 214;
	static final int fuelGaugeY = 84;
	static final int fuelGaugeU = 0;
	static final int fuelGaugeV = 208;
	static int drawFuel = 0;
	//static int getTE = 0;
	private String fuelLevel;
	private String fuelMax;

	SGBaseTE te;

	public static SGBaseScreen create(EntityPlayer player, World world, int x, int y, int z)
	{
		SGBaseTE te = SGBaseTE.at(world, x, y, z);
		if (te != null)
			return new SGBaseScreen(player, te);
		else
			return null;
	}

	public SGBaseScreen(EntityPlayer player, SGBaseTE te)
	{
		super(new SGBaseContainer(player, te), guiWidth, guiHeight);
		this.te = te;
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
	/*
	@Override
	public void updateScreen()
	{
		super.updateScreen();

        if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead)
        {
            this.mc.thePlayer.closeScreen();
        }
        else
        {
        	drawFuelGauge();
        }
	}*/


//	@Override
//	protected void keyTyped(char c, int key) {
//		if (key == Keyboard.KEY_ESCAPE)
//			close();
//		else if (key == Keyboard.KEY_BACK || key == Keyboard.KEY_DELETE) {
//			int n = te.homeAddress.length();
//			if (n > 0)
//				setAddress(te.homeAddress.substring(0, n - 1));
//		}
//		else {
//			String s = String.valueOf(c).toUpperCase();
//			if (SGBaseTE.isValidSymbolChar(s) && te.homeAddress.length() < 7)
//				setAddress(te.homeAddress + s);
//		}
//	}
	
	protected void drawGuiContainerBackgroundLayer()
	{
		drawBackgroundLayer();
	}

	@Override
	void drawBackgroundLayer()
	{
		bindTexture("/sgextensions/resources/sg_gui.png", 256, 256);
		drawTexturedRect(0, 0, guiWidth, guiHeight, 0, 0);
		drawFuelGauge();
//	}
//
//	@Override
//	void drawForegroundLayer() {
		String address = getAddress();
		int cx = xSize / 2;
		int color = 0x52aeff;
		drawCenteredString(fontRenderer, screenTitle, cx, 8, color, false);
		drawAddressSymbols(cx, 22, address);
		drawCenteredString(fontRenderer, address, cx, 72, color, false);
		drawString(fontRenderer, "Fuel", 150, 96, color, false);
	}
	
	String ITS(int Value,int rounding)
	{
		String suffix = "";
		double RV = 1;
		double Rounder = Math.pow(10, rounding);
		if(Value >= Math.pow(10,12)){suffix="T";RV=Math.pow(10,12);}
		else if(Value >= Math.pow(10,9)){suffix="G";RV=Math.pow(10,9);}
		else if(Value >= Math.pow(10,6)){suffix="M";RV=Math.pow(10,6);}
		else if(Value >= Math.pow(10,3)){suffix="K";RV=Math.pow(10,3);}
		double NewVal = (double) (Math.floor(Rounder * Value / RV) / Rounder);
		String SVal = Double.toString(NewVal) + suffix;
		return SVal;
	}

	void drawFuelGauge()
	{
		if(drawFuel == 0)
		{
			drawFuel = 10;
			fuelLevel = ITS(te.fuelBuffer,1);
			fuelMax = ITS(te.maxFuelBuffer,1);
		}
		else
		{
			drawFuel --;
		}
		int level = fuelGaugeHeight * te.fuelBuffer / te.maxFuelBuffer;
		GL11.glEnable(GL11.GL_BLEND);
		drawTexturedRect(fuelGaugeX, fuelGaugeY + fuelGaugeHeight - level,
				fuelGaugeWidth, level, fuelGaugeU, fuelGaugeV);
		GL11.glDisable(GL11.GL_BLEND);
		int color = 0x52aeff;
		drawRightString(fontRenderer, fuelLevel + "/" + fuelMax,169,112,color,false);
		
	}

	String getAddress()
	{
		//return te.homeAddress;
		//return "TESTING";
		try
		{
			return te.getHomeAddress();
		}
		catch (SGAddressing.CoordRangeError e)
		{
			return "Coordinates out of stargate range";
		}
		catch (SGAddressing.DimensionRangeError e)
		{
			return "Dimension not reachable by stargate";
		}
		catch (SGAddressing.AddressingError e)
		{
			throw new RuntimeException(e);
		}
	}
	
	
	void setAddress(String newAddress)
	{
		//te.setHomeAddress(newAddress);
	}

}
