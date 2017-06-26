package io.github.gbui.simpletreedrops;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class ConfigHelper {
    public static final String LANGUAGE_KEY_PREFIX = SimpleTreeDrops.MODID + ".configgui.";

    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_DROPS = "drops";
    private static final String CATEGORY_TRADES = "trades";
    private static final String CATEGORY_LOOT = "loot";
    public static final String[] GENERAL_CATEGORIES = {CATEGORY_GENERAL};
    public static final String[] SPECIALIZED_CATEGORIES = {CATEGORY_DROPS, CATEGORY_TRADES, CATEGORY_LOOT};

    private static List<FruitType> droppedFruitList;
    private static List<FruitType> tradableFruitList;
    private static List<ResourceLocation> customLootList;

    private static Configuration config;
    private static boolean addFruitsToOreDict;
    private static boolean dropSticks;

    public static Configuration getConfig() {
        return config;
    }

    public static void initConfig(File configFile) {
        config = new Configuration(configFile);
        syncConfig();
    }

    public static void syncConfig(String modID) {
        if (modID.equals(SimpleTreeDrops.MODID)) {
            syncConfig();
        }
    }

    public static void syncConfig() {
        for (String categoryName : SPECIALIZED_CATEGORIES) {
            ConfigCategory category = config.getCategory(categoryName);
            category.setLanguageKey(LANGUAGE_KEY_PREFIX + "ctgy." + categoryName);
        }

        Property property;

        property = config.get(CATEGORY_GENERAL, "addFruitsToOreDict", true);
        property.setLanguageKey(LANGUAGE_KEY_PREFIX + "addFruitsToOreDict");
        property.setRequiresMcRestart(true);
        addFruitsToOreDict = property.getBoolean();

        property = config.get(CATEGORY_DROPS, "stick", true);
        property.setLanguageKey(Items.STICK.getUnlocalizedName() + ".name");
        dropSticks = property.getBoolean();

        FruitType[] fruitTypes = FruitType.values();
        droppedFruitList = new ArrayList<FruitType>(fruitTypes.length);
        tradableFruitList = new ArrayList<FruitType>(fruitTypes.length);
        for (FruitType fruitType : fruitTypes) {
            String fruitName = fruitType.getName();
            Item fruitItem = fruitType.getItem();
            String fruitLanguageKey = fruitItem.getUnlocalizedName() + ".name";

            property = config.get(CATEGORY_DROPS, fruitName, true);
            property.setLanguageKey(fruitLanguageKey);
            if (property.getBoolean()) {
                droppedFruitList.add(fruitType);
            }

            property = config.get(CATEGORY_TRADES, fruitName, true);
            property.setLanguageKey(fruitLanguageKey);
            if (property.getBoolean()) {
                tradableFruitList.add(fruitType);
            }
        }
        droppedFruitList = Collections.unmodifiableList(droppedFruitList);
        tradableFruitList = Collections.unmodifiableList(tradableFruitList);

        customLootList = new ArrayList<ResourceLocation>(SimpleTreeDrops.CUSTOM_LOOT_TABLE_NAMES.length);
        for (ResourceLocation lootName : SimpleTreeDrops.CUSTOM_LOOT_TABLE_NAMES) {
            String lootKey;
            if (lootName.getResourceDomain().equals("minecraft")) {
                lootKey = lootName.getResourcePath();
            } else {
                lootKey = lootName.getResourceDomain() + ":" + lootName.getResourcePath();
            }
            lootKey = lootKey.replaceAll("[:/]", ".");
            property = config.get(CATEGORY_LOOT, lootKey, true);
            property.setLanguageKey(LANGUAGE_KEY_PREFIX + "loot." + lootKey);
            property.setRequiresWorldRestart(true);
            if (property.getBoolean()) {
                customLootList.add(lootName);
            }
        }
        customLootList = Collections.unmodifiableList(customLootList);

        config.save();
    }

    public static boolean shouldAddFruitsToOreDict() {
        return addFruitsToOreDict;
    }

    public static boolean shouldDropSticks() {
        return dropSticks;
    }

    public static List<FruitType> getDroppedFruitList() {
        return droppedFruitList;
    }

    public static List<FruitType> getTradableFruitList() {
        return tradableFruitList;
    }

    public static List<ResourceLocation> getCustomLootList() {
        return customLootList;
    }
}
