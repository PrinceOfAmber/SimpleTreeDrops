package io.github.gbui.simpletreedrops;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class ReplaceAppleWithFruitTrade implements ITradeList {
    @Override
    public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
        List<FruitType> fruitTypes = ConfigHelper.getTradableFruitList();
        int fruitCount = fruitTypes.size();

        if (fruitCount == 0)
            return;

        ListIterator<MerchantRecipe> recipeIterator = recipeList.listIterator();

        while (recipeIterator.hasNext()) {
            MerchantRecipe recipe = recipeIterator.next();
            ItemStack itemStack = recipe.getItemToSell();
            Item item = itemStack.getItem();

            if (item != Items.APPLE)
                continue;

            int fruitIndex = random.nextInt(fruitCount + 1); // Add 1 for apple trade

            if (fruitIndex == fruitCount) {
                // Keep apple trade
                break;
            } else {
                FruitType fruitType = fruitTypes.get(fruitIndex);
                ItemStack fruitStack = fruitType.createItemStack(itemStack.stackSize);
                recipeIterator.set(new MerchantRecipe(recipe.getItemToBuy(), recipe.getSecondItemToBuy(), fruitStack));
                break;
            }
        }
    }
}
