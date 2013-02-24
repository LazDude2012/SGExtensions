//------------------------------------------------------------------------------------------------
//
//   SG Craft - Villager trade handler
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.Random;

public class SGTradeHandler implements IVillageTradeHandler
{

	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipes, Random random)
	{
		recipes.add(new MerchantRecipe(
				new ItemStack(Item.emerald, 4),
				new ItemStack(SGExtensions.naquadahIngot, 3),
				new ItemStack(SGExtensions.sgRingBlock, 1, 0)));
		recipes.add(new MerchantRecipe(
				new ItemStack(Item.emerald, 4),
				new ItemStack(Item.enderPearl),
				new ItemStack(SGExtensions.sgRingBlock, 1, 1)));
		recipes.add(new MerchantRecipe(
				new ItemStack(Item.emerald, 16),
				new ItemStack(Item.eyeOfEnder),
				new ItemStack(SGExtensions.sgBaseBlock)));
		recipes.add(new MerchantRecipe(
				new ItemStack(Item.emerald, 16),
				new ItemStack(Block.obsidian, 4),
				new ItemStack(SGExtensions.sgControllerBlock)));
	}

}
