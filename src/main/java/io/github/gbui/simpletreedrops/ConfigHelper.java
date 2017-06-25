package io.github.gbui.simpletreedrops;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigHelper {
    public static final String LANGUAGE_KEY_PREFIX = SimpleTreeDrops.MODID + ".configgui.";

    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_DROPS = "drops";
    private static final String CATEGORY_LOOT = "loot";
    public static final String[] GENERAL_CATEGORIES = {CATEGORY_GENERAL};
    public static final String[] SPECIALIZED_CATEGORIES = {CATEGORY_DROPS, CATEGORY_LOOT};

    private static final Map<FruitType, Boolean> SHOULD_DROP_MAP = new HashMap<FruitType, Boolean>();
    private static final Map<ResourceLocation, Boolean> SHOULD_ADD_CUSTOM_LOOT_MAP = new HashMap<ResourceLocation, Boolean>();

    private static Configuration config;
    private static boolean enableVillagerTrades;
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

        property = config.get(CATEGORY_GENERAL, "enableVillagerTrades", true);
        property.setLanguageKey(LANGUAGE_KEY_PREFIX + "enableVillagerTrades");
        enableVillagerTrades = property.getBoolean();

        property = config.get(CATEGORY_GENERAL, "addFruitsToOreDict", true);
        property.setLanguageKey(LANGUAGE_KEY_PREFIX + "addFruitsToOreDict");
        property.setRequiresMcRestart(true);
        addFruitsToOreDict = property.getBoolean();

        property = config.get(CATEGORY_DROPS, "stick", true);
        property.setLanguageKey(Items.STICK.getUnlocalizedName() + ".name");
        dropSticks = property.getBoolean();

        for (FruitType fruitType : FruitType.values()) {
            String fruitName = fruitType.getName();
            Item fruitItem = fruitType.getItem();
            String fruitLanguageKey = fruitItem.getUnlocalizedName() + ".name";

            property = config.get(CATEGORY_DROPS, fruitName, true);
            property.setLanguageKey(fruitLanguageKey);
            SHOULD_DROP_MAP.put(fruitType, property.getBoolean());
        }

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
            SHOULD_ADD_CUSTOM_LOOT_MAP.put(lootName, property.getBoolean());
        }

        config.save();
    }

    public static boolean areVillagerTradesEnabled() {
        return enableVillagerTrades;
    }

    public static boolean shouldAddFruitsToOreDict() {
        return addFruitsToOreDict;
    }

    public static boolean shouldDropSticks() {
        return dropSticks;
    }

    public static boolean shouldDropFruit(FruitType fruitType) {
        Boolean dropFruit = SHOULD_DROP_MAP.get(fruitType);
        return dropFruit != null && dropFruit;
    }

    public static boolean shouldAddCustomLoot(ResourceLocation name) {
        Boolean addCustomLoot = SHOULD_ADD_CUSTOM_LOOT_MAP.get(name);
        return addCustomLoot != null && addCustomLoot;
    }
}
