//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic Block Renderer
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeDirection;

public class BaseBlockRenderer<BLOCK extends BaseBlock<TE>, TE extends TileEntity>
		implements ISimpleBlockRenderingHandler
{

	public int renderID;

	Tessellator tess;
	double x0, y0, z0, u0, v0, us, vs;
	boolean textureOverridden;
	IBlockAccess world;
	BLOCK block;
	int metadata;
	TE te;

	@Override
	public int getRenderId()
	{
		return renderID;
	}

	@Override
	public boolean shouldRender3DInInventory()
	{
		return true;
	}

	@Override
	public void renderInventoryBlock(Block blk, int data, int modelID, RenderBlocks rb)
	{
		world = null;
		block = (BLOCK) blk;
		metadata = data;
		te = null;
		setUpTextureOverride(-1);
		int facing = block.facingInInventory(metadata);
		int rotation = block.rotationInInventory(metadata);
		ForgeHooksClient.bindTexture(block.getTextureFile(), 0);
		tess = Tessellator.instance;
		tess.setBrightness(0xf000f0);
		tess.startDrawingQuads();
		renderGlobal(0, 0, 0, facing, rotation);
		tess.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
	                                Block blk, int renderID, RenderBlocks rb)
	{
		//System.out.printf("BaseBlockRenderer.renderWorldBlock: (%d,%d,%d) %s\n", x, y, z, blk);
		this.world = world;
		block = (BLOCK) blk;
		metadata = world.getBlockMetadata(x, y, z);
		te = block.getTileEntity(world, x, y, z);
		setUpTextureOverride(rb.overrideBlockTexture);
		int facing = block.facingInWorld(metadata, te);
		int rotation = block.rotationInWorld(metadata, te);
		tess = Tessellator.instance;
		tess.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		return renderGlobal(x + 0.5, y + 0.5, z + 0.5, facing, rotation);
	}

	boolean renderGlobal(double x, double y, double z, int facing, int rotation)
	{
		//System.out.printf("BaseBlockRenderer.renderGlobal: (%s, %s, %s) facing %d rotation %d\n",
		//	x, y, z, facing, rotation);
		tess.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		Trans3 t = new Trans3(x, y, z).side(facing).turn(rotation);
		return renderLocal(t);
	}

	boolean renderLocal(Trans3 t)
	{
		renderCube(t);
		return true;
	}

	void setUpTextureOverride(int index)
	{
		if (index >= 0)
		{
			textureOverridden = true;
			u0 = (index & 0xf) / 16.0;
			v0 = (index >> 4) / 16.0;
		} else
			textureOverridden = false;
	}

	void selectTile(int index)
	{
		selectTile(index >> 4, index & 0xf);
	}

	void selectTile(int row, int col)
	{
		selectTile(row, col, 16, 16);
	}

	void selectTile(int row, int col, int width, int height)
	{
		if (!textureOverridden)
		{
			u0 = col * (1 / 16.0);
			v0 = row * (1 / 16.0);
		}
		us = 16.0 / width;
		vs = 16.0 / height;
	}

	static double cubeFaces[][] = {
			{-0.5, -0.5, 0.5, 0, 0, -1, 1, 0, 0, 0, -1, 0}, // DOWN
			{-0.5, 0.5, -0.5, 0, 0, 1, 1, 0, 0, 0, 1, 0}, // UP
			{0.5, 0.5, -0.5, 0, -1, 0, -1, 0, 0, 0, 0, -1}, // NORTH
			{-0.5, 0.5, 0.5, 0, -1, 0, 1, 0, 0, 0, 0, 1}, // SOUTH
			{-0.5, 0.5, -0.5, 0, -1, 0, 0, 0, 1, -1, 0, 0}, // WEST
			{0.5, 0.5, 0.5, 0, -1, 0, 0, 0, -1, 1, 0, 0}, // EAST
	};

	void renderCube(Trans3 t)
	{
		//System.out.printf("BaseBlockRenderer.renderCube\n");
		for (int i = 0; i < 6; i++)
		{
			selectTileForSide(i);
			setBrightnessForSide(t, i);
			cubeFace(t, cubeFaces[i]);
		}
	}

	void selectTileForSide(int side)
	{
		selectTile(block.getBlockTextureFromSideAndMetadata(side, metadata));
	}

	static float[] shadeTable = {
			0.5F, // DOWN
			1.0F, // UP
			0.6F, // NORTH
			0.6F, // SOUTH
			0.8F, // WEST
			0.8F, // EAST
	};

	void setBrightnessForSide(Trans3 t, int side)
	{
		if (world != null)
		{
			ForgeDirection d = ForgeDirection.getOrientation(side);
			Vector3 p = t.p(d.offsetX, d.offsetY, d.offsetZ);
			tess.setBrightness(block.getMixedBrightnessForBlock(world, p.floorX(), p.floorY(), p.floorZ()));
			float shade = shadeTable[side];
			tess.setColorOpaque_F(shade, shade, shade);
		}
	}

	void cubeFace(Trans3 t, double[] c)
	{
		tess.setNormal((float) c[9], (float) c[10], (float) c[11]);
		face(t, c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7], c[8], 0, 0, 16, 16);
	}

	void face(Trans3 t,
	          double x, double y, double z,
	          double dx1, double dy1, double dz1,
	          double dx2, double dy2, double dz2,
	          double u, double v, double du, double dv)
	{
		vertex(t, x, y, z, u, v);
		vertex(t, x + dx1, y + dy1, z + dz1, u, v + dv);
		vertex(t, x + dx1 + dx2, y + dy1 + dy2, z + dz1 + dz2, u + du, v + dv);
		vertex(t, x + dx2, y + dy2, z + dz2, u + du, v);
	}

	void vertex(Trans3 t, double x, double y, double z, double u, double v)
	{
		Vector3 p = t.p(x, y, z);
//		if (textureOverridden) {
//			u *= us;
//			v *= vs;
//		}
		tess.addVertexWithUV(p.x, p.y, p.z, u0 + u * (1 / 256.0), v0 + v * (1 / 256.0));
	}

}
