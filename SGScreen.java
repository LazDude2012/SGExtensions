//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate gui base class
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

//------------------------------------------------------------------------------------------------

public class SGScreen extends GuiContainer
{

	final static String padding = "-------";

	double uscale, vscale;
	float red = 1.0F, green = 1.0F, blue = 1.0F;

	public SGScreen()
	{
		super(new BaseContainer());
	}

	public SGScreen(Container container, int width, int height)
	{
		super(container);
		xSize = width;
		ySize = height;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
	{
		//System.out.printf("SGScreen.drawGuiContainerBackgroundLayer: guiLeft = %s, guiTop = %s\n",
		//	guiLeft, guiTop);
		GL11.glPushMatrix();
		GL11.glTranslatef(guiLeft, guiTop, 0.0F);
		drawBackgroundLayer();
		GL11.glPopMatrix();
	}

	void drawBackgroundLayer()
	{
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		drawForegroundLayer();
	}

	void drawForegroundLayer()
	{
	}

	void close()
	{
		//mc.displayGuiScreen((GuiScreen)null);
		mc.thePlayer.closeScreen();
	}

	void drawAddressSymbols(int x, int y, String address)
	{
		int symbolsPerRow = 8;
		int scale = 2;
		int frameWidth = 472 / scale;
		int frameHeight = 88 / scale;
		int borderSize = 12 / scale;
		int cellSize = 64 / scale;
		int x0 = x - frameWidth / 2;
		bindSGTexture("symbol_frame.png", 512 / scale, 128 / scale);
		drawTexturedRect(x0, y, frameWidth, frameHeight, 0, 0);
		bindSGTexture("symbols.png", 512 / scale, 256 / scale);
		int n = address.length();
		for (int i = 0; i < n; i++)
		{
			int s = SGBaseTE.charToSymbol(address.charAt(i));
			int row = s / symbolsPerRow;
			int col = s % symbolsPerRow;
			drawTexturedRect(x0 + borderSize + i * cellSize, y + borderSize, cellSize, cellSize,
					col * cellSize, row * cellSize);
		}
	}

	void drawAddressString(int x, int y, String address, String caret)
	{
		drawCenteredString(this.fontRenderer, padAddress(address, caret), x, y, 0xffffff);
	}

	String padAddress(String address, String caret)
	{
		return address + caret + padding.substring(address.length(), 7);
	}

	void bindSGTexture(String name)
	{
		bindSGTexture(name, 1, 1);
	}

	void bindSGTexture(String name, int usize, int vsize)
	{
		bindTexture("/sgextensions/resources/"+name, usize, vsize);
	}

	void bindTexture(String path)
	{
		bindTexture(path, 1, 1);
	}

	void bindTexture(String path, int usize, int vsize)
	{
		ForgeHooksClient.bindTexture(path, 0);
		uscale = 1.0 / usize;
		vscale = 1.0 / vsize;
	}

	void drawTexturedRect(double x, double y, double w, double h)
	{
		drawTexturedRectUV(x, y, w, h, 0, 0, 1, 1);
	}

	void drawTexturedRect(double x, double y, double w, double h, double u, double v)
	{
		drawTexturedRect(x, y, w, h, u, v, w, h);
	}

	void drawTexturedRect(double x, double y, double w, double h, double u, double v, double us, double vs)
	{
		drawTexturedRectUV(x, y, w, h, u * uscale, v * vscale, us * uscale, vs * vscale);
	}

	void drawTexturedRectUV(double x, double y, double w, double h, double u, double v, double us, double vs)
	{
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.setColorOpaque_F(red, green, blue);
		tess.addVertexWithUV(x, y + h, zLevel, u, v + vs);
		tess.addVertexWithUV(x + w, y + h, zLevel, u + us, v + vs);
		tess.addVertexWithUV(x + w, y, zLevel, u + us, v);
		tess.addVertexWithUV(x, y, zLevel, u, v);
		tess.draw();
	}

	public void setColor(double r, double g, double b)
	{
		red = (float) r;
		green = (float) g;
		blue = (float) b;
	}

	public void resetColor()
	{
		setColor(1.0, 1.0, 1.0);
	}

	public void drawString(FontRenderer fr, String s, int x, int y, int color, boolean shadow)
	{
		fr.drawString(s, x, y, color, shadow);
	}

	public void drawCenteredString(FontRenderer fr, String s, int x, int y, int color, boolean shadow)
	{
		fr.drawString(s, x - fr.getStringWidth(s) / 2, y, color, shadow);
	}

}

//------------------------------------------------------------------------------------------------

//class DummyContainer extends Container {
//
//	public boolean canInteractWith(EntityPlayer var1) {
//		return true;
//	}
//
//}
