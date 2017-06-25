package io.github.gbui.simpletreedrops;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class DropHelper {
    public static void addDrops(World world, IBlockState blockState, int fortune, List<ItemStack> drops) {
        Block block = blockState.getBlock();
        BlockPlanks.EnumType woodType = null;

        if (block == Blocks.LEAVES) {
            woodType = blockState.getValue(BlockOldLeaf.VARIANT);
        } else if (block == Blocks.LEAVES2) {
            woodType = blockState.getValue(BlockNewLeaf.VARIANT);
        }

        if (woodType != null) {
            Random random = world.rand;
            addStickDrop(woodType, random, fortune, drops);
            addFruitDrop(woodType, random, fortune, drops);
        }
    }

    private static void addStickDrop(BlockPlanks.EnumType woodType, Random random, int fortune, List<ItemStack> drops) {
        if (ConfigHelper.shouldDropSticks()) {
            int saplingDropChance = woodType == BlockPlanks.EnumType.JUNGLE ? 40 : 20;
            int dropChance = saplingDropChance * 3 / 4;
            addDrop(new ItemStack(Items.STICK), random, dropChance, 10, fortune, 2, drops);
        }
    }

    private static void addFruitDrop(BlockPlanks.EnumType woodType, Random random, int fortune, List<ItemStack> drops) {
        FruitType fruitType = FruitType.byWoodType(woodType);
        if (fruitType != null && ConfigHelper.shouldDropFruit(fruitType)) {
            addDrop(fruitType.createItemStack(), random, 200, 40, fortune, 10, drops);
        }
    }

    private static void addDrop(ItemStack itemStack, Random random, int baseChance, int minChance, int fortune, int baseFortuneChanceOffset, List<ItemStack> drops) {
        int chance = baseChance;

        if (fortune > 0) {
            chance -= baseFortuneChanceOffset << fortune;
            chance = Math.max(chance, minChance);
        }

        if (random.nextInt(chance) == 0) {
            drops.add(itemStack);
        }
    }
}
