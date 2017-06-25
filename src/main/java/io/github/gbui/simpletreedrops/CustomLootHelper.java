package io.github.gbui.simpletreedrops;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLootHelper {
    private static final int ROLLS_SKIP_VALUE = 0;

    private static final Gson GSON_INSTANCE = ReflectionHelper.getPrivateValue(LootTableManager.class, null, "field_186526_b", "GSON_INSTANCE");
    private static final Field POOLS_FIELD = ReflectionHelper.findField(LootTable.class, "field_186466_c", "pools");
    private static final Field LOOT_ENTRIES_FIELD = ReflectionHelper.findField(LootPool.class, "field_186453_a", "lootEntries");

    private static final Map<ResourceLocation, ResourceLocation> CUSTOM_LOOT_TABLE_MAP = new HashMap<ResourceLocation, ResourceLocation>();

    public static void registerCustomLootTable(ResourceLocation originalName) {
        CUSTOM_LOOT_TABLE_MAP.put(originalName, new ResourceLocation(SimpleTreeDrops.MODID, originalName.getResourcePath()));
    }

    public static void loadCustomLoot(ResourceLocation originalName, LootTable originalTable) {
        if (!ConfigHelper.shouldAddCustomLoot(originalName)) {
            return;
        }

        ResourceLocation customName = CUSTOM_LOOT_TABLE_MAP.get(originalName);
        if (customName == null) {
            return;
        }

        LootTable customTable = loadCustomLootTable(customName);
        if (customTable == null || customTable == LootTable.EMPTY_LOOT_TABLE) {
            return;
        }

        try {
            List<LootPool> customPools = (List<LootPool>) POOLS_FIELD.get(customTable);
            for (LootPool customPool : customPools) {
                LootPool originalPool = originalTable.getPool(customPool.getName());

                if (originalPool == null) {
                    originalTable.addPool(customPool);
                    return;
                }

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
            }
        } catch (IllegalAccessException e) {
            SimpleTreeDrops.getLogger().warn("Couldn't add custom loot from loot table {} to {}", customName, originalName);
        }
    }

    // See LootTableManager.loadBuiltinLootTable
    @Nullable
    private static LootTable loadCustomLootTable(ResourceLocation name) {
        String resourceName = "/assets/" + name.getResourceDomain() + "/loot_tables/" + name.getResourcePath() + ".json";
        URL url = LootTableManager.class.getResource(resourceName);

        if (url == null) {
            SimpleTreeDrops.getLogger().warn("Couldn't load loot table {} from {}", name, resourceName);
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

        SimpleTreeDrops.getLogger().warn("Couldn't load loot table {} from {}", name, url, exception);
        return LootTable.EMPTY_LOOT_TABLE;
    }
}
