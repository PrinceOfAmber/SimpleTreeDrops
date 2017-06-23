package io.github.gbui.simpletreedrops;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.ListIterator;
import java.util.Random;

public class ReplaceAppleWithFruitTrade implements ITradeList {
    @Override
    public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
        ListIterator<MerchantRecipe> recipeIterator = recipeList.listIterator();

        while (recipeIterator.hasNext()) {
            MerchantRecipe recipe = recipeIterator.next();
            ItemStack itemStack = recipe.getItemToSell();
            Item item = itemStack.getItem();

            if (item != Items.APPLE)
                continue;

            FruitType[] fruitTypes = FruitType.values();
            int fruitIndex = random.nextInt(fruitTypes.length + 1);

            if (fruitIndex == fruitTypes.length)
                break; // Keep apple trade

            FruitType fruitType = fruitTypes[fruitIndex];
            recipeIterator.set(new MerchantRecipe(recipe.getItemToBuy(), recipe.getSecondItemToBuy(), fruitType.createItemStack(itemStack.getCount())));

            break;
        }
    }
}
