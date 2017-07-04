package io.github.gbui.simpletreedrops;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLootHelper {
    private static final int ROLLS_SKIP_VALUE = 0;

    private static final Field POOLS_FIELD = ReflectionHelper.findField(LootTable.class, "field_186466_c", "pools");
    private static final Field LOOT_ENTRIES_FIELD = ReflectionHelper.findField(LootPool.class, "field_186453_a", "lootEntries");

    private static final Map<ResourceLocation, ResourceLocation> CUSTOM_LOOT_TABLE_MAP = new HashMap<>();

    public static void registerCustomLootTable(ResourceLocation originalName) {
        CUSTOM_LOOT_TABLE_MAP.put(originalName, new ResourceLocation(SimpleTreeDrops.MODID, originalName.getResourcePath()));
    }

    public static void loadCustomLoot(ResourceLocation originalName, LootTable originalTable, LootTableManager lootTableManager) {
        if (!ConfigHelper.getCustomLootList().contains(originalName)) {
            return;
        }

        ResourceLocation customName = CUSTOM_LOOT_TABLE_MAP.get(originalName);
        if (customName == null) {
            return;
        }

        LootTable customTable = lootTableManager.getLootTableFromLocation(customName);
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
}
