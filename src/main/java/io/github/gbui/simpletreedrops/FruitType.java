package io.github.gbui.simpletreedrops;

import net.minecraft.block.BlockPlanks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public enum FruitType {
    WALNUTS("walnuts", BlockPlanks.EnumType.SPRUCE, "cropWalnut"),
    PEACH("peach", BlockPlanks.EnumType.BIRCH, "cropPeach"),
    BANANA("banana", BlockPlanks.EnumType.JUNGLE, "cropBanana"),
    ORANGE("orange", BlockPlanks.EnumType.ACACIA, "cropOrange");

    private static final Map<BlockPlanks.EnumType, FruitType> WOOD_TYPE_LOOKUP = new HashMap<BlockPlanks.EnumType, FruitType>();
    private static final Map<ItemFood, FruitType> ITEM_LOOKUP = new HashMap<ItemFood, FruitType>();

    private final String name;
    private final BlockPlanks.EnumType woodType;
    private final ItemFood item;
    private final String[] oreDictNames;

    FruitType(String name, BlockPlanks.EnumType woodType, String... oreDictNames) {
        this.name = name;
        this.woodType = woodType;
        this.oreDictNames = oreDictNames;

        item = new ItemFood(4, 0.3F, false);
        item.setUnlocalizedName(SimpleTreeDrops.MODID + "." + name);
        item.setRegistryName(SimpleTreeDrops.MODID, name);
    }

    public String getName() {
        return name;
    }

    public BlockPlanks.EnumType getWoodType() {
        return woodType;
    }

    public ItemFood getItem() {
        return item;
    }

    public ItemStack createItemStack() {
        return new ItemStack(item);
    }

    public ItemStack createItemStack(int amount) {
        return new ItemStack(item, amount);
    }

    public String[] getOreDictNames() {
        return oreDictNames;
    }

    public static FruitType byWoodType(BlockPlanks.EnumType woodType) {
        return WOOD_TYPE_LOOKUP.get(woodType);
    }

    public static FruitType byItemStack(ItemStack itemStack) {
        return itemStack != null ? byItem(itemStack.getItem()) : null;
    }

    public static FruitType byItem(Item item) {
        return item instanceof ItemFood ? ITEM_LOOKUP.get(item) : null;
    }

    static {
        for (FruitType fruitType : values()) {
            WOOD_TYPE_LOOKUP.put(fruitType.getWoodType(), fruitType);
            ITEM_LOOKUP.put(fruitType.getItem(), fruitType);
        }
    }
}
