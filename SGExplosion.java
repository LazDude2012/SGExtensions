//------------------------------------------------------------------------------------------------
//
//	 SG Craft - Explosion with increased entity damage
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;

public class SGExplosion extends Explosion
{

	public double damageMultiplier = 100;
	int h = 16;
	World world;

	public SGExplosion(World world, Entity entity, double x, double y, double z, float size)
	{
		super(world, entity, x, y, z, size);
		this.world = world;
	}

	/**
	 * Does the first part of the explosion (destroy blocks)
	 */
	@Override
	public void doExplosionA()
	{
		float var1 = this.explosionSize;
		HashSet var2 = new HashSet();
		int var3;
		int var4;
		int var5;
		double var15;
		double var17;
		double var19;

		for (var3 = 0; var3 < this.h; ++var3)
		{
			for (var4 = 0; var4 < this.h; ++var4)
			{
				for (var5 = 0; var5 < this.h; ++var5)
				{
					if (var3 == 0 || var3 == this.h - 1 || var4 == 0 || var4 == this.h - 1 || var5 == 0 || var5 == this.h - 1)
					{
						double var6 = (double) ((float) var3 / ((float) this.h - 1.0F) * 2.0F - 1.0F);
						double var8 = (double) ((float) var4 / ((float) this.h - 1.0F) * 2.0F - 1.0F);
						double var10 = (double) ((float) var5 / ((float) this.h - 1.0F) * 2.0F - 1.0F);
						double var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
						var6 /= var12;
						var8 /= var12;
						var10 /= var12;
						float var14 = this.explosionSize * (0.7F + this.world.rand.nextFloat() * 0.6F);
						var15 = this.explosionX;
						var17 = this.explosionY;
						var19 = this.explosionZ;

						for (float var21 = 0.3F; var14 > 0.0F; var14 -= var21 * 0.75F)
						{
							int var22 = MathHelper.floor_double(var15);
							int var23 = MathHelper.floor_double(var17);
							int var24 = MathHelper.floor_double(var19);
							int var25 = this.world.getBlockId(var22, var23, var24);

							if (var25 > 0)
							{
								Block var26 = Block.blocksList[var25];
								float var27 = this.exploder != null ? this.exploder.func_82146_a(this, var26, var22, var23, var24) : var26.getExplosionResistance(this.exploder, world, var22, var23, var24, explosionX, explosionY, explosionZ);
								var14 -= (var27 + 0.3F) * var21;
							}

							if (var14 > 0.0F)
							{
								var2.add(new ChunkPosition(var22, var23, var24));
							}

							var15 += var6 * (double) var21;
							var17 += var8 * (double) var21;
							var19 += var10 * (double) var21;
						}
					}
				}
			}
		}

		this.affectedBlockPositions.addAll(var2);
		this.explosionSize *= 2.0F;
		var3 = MathHelper.floor_double(this.explosionX - (double) this.explosionSize - 1.0D);
		var4 = MathHelper.floor_double(this.explosionX + (double) this.explosionSize + 1.0D);
		var5 = MathHelper.floor_double(this.explosionY - (double) this.explosionSize - 1.0D);
		int var29 = MathHelper.floor_double(this.explosionY + (double) this.explosionSize + 1.0D);
		int var7 = MathHelper.floor_double(this.explosionZ - (double) this.explosionSize - 1.0D);
		int var30 = MathHelper.floor_double(this.explosionZ + (double) this.explosionSize + 1.0D);
		List var9 = this.world.getEntitiesWithinAABBExcludingEntity(this.exploder, AxisAlignedBB.getAABBPool().addOrModifyAABBInPool((double) var3, (double) var5, (double) var7, (double) var4, (double) var29, (double) var30));
		Vec3 var31 = this.world.getWorldVec3Pool().getVecFromPool(this.explosionX, this.explosionY, this.explosionZ);

		for (int var11 = 0; var11 < var9.size(); ++var11)
		{
			Entity var32 = (Entity) var9.get(var11);
			double var13 = var32.getDistance(this.explosionX, this.explosionY, this.explosionZ) / (double) this.explosionSize;

			if (var13 <= 1.0D)
			{
				var15 = var32.posX - this.explosionX;
				var17 = var32.posY + (double) var32.getEyeHeight() - this.explosionY;
				var19 = var32.posZ - this.explosionZ;
				double var34 = (double) MathHelper.sqrt_double(var15 * var15 + var17 * var17 + var19 * var19);

				if (var34 != 0.0D)
				{
					var15 /= var34;
					var17 /= var34;
					var19 /= var34;
					double var33 = (double) this.world.getBlockDensity(var31, var32.boundingBox);
					double var35 = (1.0D - var13) * var33;
					var32.attackEntityFrom(DamageSource.explosion, (int) (damageMultiplier * (var35 * var35 + var35) / 2.0D * 8.0D * (double) this.explosionSize + 1.0D));
					double var36 = EnchantmentProtection.func_92092_a(var32, var35);
					var32.motionX += var15 * var36;
					var32.motionY += var17 * var36;
					var32.motionZ += var19 * var36;

					if (var32 instanceof EntityPlayer)
					{
						this.func_77277_b().put((EntityPlayer) var32, this.world.getWorldVec3Pool().getVecFromPool(var15 * var35, var17 * var35, var19 * var35));
					}
				}
			}
		}

		this.explosionSize = var1;
	}

}
