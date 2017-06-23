package io.github.gbui.simpletreedrops;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;

import java.util.List;
import java.util.Random;

@Mod(modid = SimpleTreeDrops.MODID, name = SimpleTreeDrops.NAME, version = SimpleTreeDrops.VERSION, acceptedMinecraftVersions = "*")
public class SimpleTreeDrops {
    public static final String MODID = "simpletreedrops";
    public static final String NAME = "Simple Tree Drops";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        for (FruitType fruitType : FruitType.values()) {
            Item fruitItem = fruitType.getItem();
            GameRegistry.register(fruitItem);
            if (event.getSide().isClient()) {
                ModelLoader.setCustomModelResourceLocation(fruitItem, 0, new ModelResourceLocation(MODID + ":" + fruitType.getName(), "inventory"));
            }
        }

        addVillagerTrade("minecraft:farmer", 0, 3, new ReplaceAppleWithFruitTrade());

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void addVillagerTrade(String name, int careerID, int careerLevel, EntityVillager.ITradeList... trades) {
        IForgeRegistry<VillagerProfession> registry = VillagerRegistry.instance().getRegistry();
        VillagerProfession profession = registry.getValue(new ResourceLocation(name));
        VillagerCareer career = profession.getCareer(careerID);
        career.addTrade(careerLevel, trades);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {}

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {}

    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        IBlockState blockState = event.getState();
        Block block = blockState.getBlock();
        BlockPlanks.EnumType woodType = null;

        if (block == Blocks.LEAVES) {
            woodType = blockState.getValue(BlockOldLeaf.VARIANT);
        } else if (block == Blocks.LEAVES2) {
            woodType = blockState.getValue(BlockNewLeaf.VARIANT);
        }

        if (woodType != null) {
            Random random = event.getWorld().rand;
            List<ItemStack> drops = event.getDrops();
            int fortune = event.getFortuneLevel();
            addStickDrop(woodType, random, fortune, drops);
            addFruitDrop(woodType, random, fortune, drops);
        }
    }

    private void addStickDrop(BlockPlanks.EnumType woodType, Random random, int fortune, List<ItemStack> drops) {
        int saplingDropChance = woodType == BlockPlanks.EnumType.JUNGLE ? 40 : 20;
        int dropChance = saplingDropChance * 3 / 4;
        addDrop(new ItemStack(Items.STICK), random, dropChance, 10, fortune, 2, drops);
    }

    private void addFruitDrop(BlockPlanks.EnumType woodType, Random random, int fortune, List<ItemStack> drops) {
        FruitType fruitType = FruitType.byWoodType(woodType);
        if (fruitType != null) {
            addDrop(fruitType.createItemStack(), random, 200, 40, fortune, 10, drops);
        }
    }

    private void addDrop(ItemStack itemStack, Random random, int baseChance, int minChance, int fortune, int baseFortuneChanceOffset, List<ItemStack> drops) {
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
