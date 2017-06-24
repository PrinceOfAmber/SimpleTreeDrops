package io.github.gbui.simpletreedrops;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLootHandler {
    private static final int ROLLS_SKIP_VALUE = 0;

    private static final Gson GSON_INSTANCE = ReflectionHelper.getPrivateValue(LootTableManager.class, null, "field_186526_b", "GSON_INSTANCE");
    private static final Field POOLS_FIELD = ReflectionHelper.findField(LootTable.class, "field_186466_c", "pools");
    private static final Field LOOT_ENTRIES_FIELD = ReflectionHelper.findField(LootPool.class, "field_186453_a", "lootEntries");

    private final Map<ResourceLocation, ResourceLocation> customLootTableMap = new HashMap<ResourceLocation, ResourceLocation>();
    private final String customDomain;
    private final Logger logger;

    public CustomLootHandler(String customDomain, Logger logger) {
        this.customDomain = customDomain;
        this.logger = logger;
    }

    public void registerCustomLootTable(ResourceLocation originalName) {
        customLootTableMap.put(originalName, new ResourceLocation(customDomain, originalName.getResourcePath()));
    }

    @SubscribeEvent
    public void onLoadLootTable(LootTableLoadEvent event) {
        ResourceLocation originalName = event.getName();
        ResourceLocation customName = customLootTableMap.get(originalName);
        if (customName != null) {
            LootTable originalTable = event.getTable();
            LootTable customTable = loadCustomLootTable(customName);
            if (customTable != null && customTable != LootTable.EMPTY_LOOT_TABLE) {
                try {
                    List<LootPool> customPools = (List<LootPool>) POOLS_FIELD.get(customTable);
                    for (LootPool customPool : customPools) {
                        LootPool originalPool = originalTable.getPool(customPool.getName());
                        if (originalPool != null) {
                            RandomValueRange customRolls = customPool.getRolls();
                            if (customRolls.getMin() != ROLLS_SKIP_VALUE && customRolls.getMax() != ROLLS_SKIP_VALUE) {
                                customPool.setRolls(customRolls);
                            }

                            RandomValueRange customBonusRolls = customPool.getBonusRolls();
                            if (customBonusRolls.getMin() != ROLLS_SKIP_VALUE && customBonusRolls.getMax() != ROLLS_SKIP_VALUE) {
                                customPool.setBonusRolls(customBonusRolls);
                            }

                            List<LootEntry> customEntries = (List<LootEntry>) LOOT_ENTRIES_FIELD.get(customPool);
                            for (LootEntry customEntry : customEntries) {
                                originalPool.removeEntry(customEntry.getEntryName());
                                originalPool.addEntry(customEntry);
                            }
                        } else {
                            originalTable.addPool(customPool);
                        }
                    }
                } catch (IllegalAccessException e) {
                    logger.warn("Couldn't add custom loot from loot table {} to {}", customName, originalName);
                }
            }
        }
    }

    // See LootTableManager.loadBuiltinLootTable
    @Nullable
    private LootTable loadCustomLootTable(ResourceLocation name) {
        String resourceName = "/assets/" + name.getResourceDomain() + "/loot_tables/" + name.getResourcePath() + ".json";
        URL url = LootTableManager.class.getResource(resourceName);

        if (url == null) {
            logger.warn("Couldn't load loot table {} from {}", name, resourceName);
            return null;
        }

        Exception exception;

        try {
            String data = Resources.toString(url, Charsets.UTF_8);
            return ForgeHooks.loadLootTable(GSON_INSTANCE, name, data, false);
        } catch (IOException e) {
            exception = e;
        } catch (JsonParseException e) {
            exception = e;
        }

        logger.warn("Couldn't load loot table {} from {}", name, url, exception);
        return LootTable.EMPTY_LOOT_TABLE;
    }
}
