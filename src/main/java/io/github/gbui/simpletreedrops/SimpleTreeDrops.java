package io.github.gbui.simpletreedrops;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Logger;

@Mod(modid = SimpleTreeDrops.MODID, name = SimpleTreeDrops.NAME, version = SimpleTreeDrops.VERSION, updateJSON = SimpleTreeDrops.UPDATE_JSON, guiFactory = SimpleTreeDrops.GUI_FACTORY, acceptedMinecraftVersions = "*")
public class SimpleTreeDrops {
    public static final String MODID = "simpletreedrops";
    public static final String NAME = "Simple Tree Drops";
    public static final String VERSION = "1.0.4";
    public static final String UPDATE_JSON = "https://github.com/gbui/SimpleTreeDrops/raw/updateJSON/updates.json";
    public static final String GUI_FACTORY = "io.github.gbui.simpletreedrops.GuiFactory";

    public static final ResourceLocation[] CUSTOM_LOOT_TABLE_NAMES = {
            LootTableList.CHESTS_IGLOO_CHEST,
            LootTableList.CHESTS_SPAWN_BONUS_CHEST,
            LootTableList.CHESTS_STRONGHOLD_CORRIDOR,
            LootTableList.CHESTS_STRONGHOLD_CROSSING,
            LootTableList.CHESTS_VILLAGE_BLACKSMITH
    };

    private static Logger logger;

    public static Logger getLogger() {
        return logger;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        ConfigHelper.initConfig(event.getSuggestedConfigurationFile());

        VillagerTradeHelper.addVillagerTrade("minecraft:farmer", 0, 3, new ReplaceAppleWithFruitTrade());

        for (ResourceLocation lootName : CUSTOM_LOOT_TABLE_NAMES) {
            CustomLootHelper.registerCustomLootTable(lootName);
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {}

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {}

    @SubscribeEvent
    public void onItemRegistry(RegistryEvent.Register<Item> event) {
        for (FruitType fruitType : FruitType.values()) {
            Item fruitItem = fruitType.getItem();
            event.getRegistry().register(fruitItem);
            if (ConfigHelper.shouldAddFruitsToOreDict()) {
                for (String oreDictName : fruitType.getOreDictNames()) {
                    OreDictionary.registerOre(oreDictName, fruitItem);
                }
            }
        }
    }

    @SubscribeEvent
    public void onModelRegistry(ModelRegistryEvent event) {
        for (FruitType fruitType : FruitType.values()) {
            ModelResourceLocation model = new ModelResourceLocation(MODID + ":" + fruitType.getName(), "inventory");
            ModelLoader.setCustomModelResourceLocation(fruitType.getItem(), 0, model);
        }
    }

    @SubscribeEvent
    public void onConfigChanged(OnConfigChangedEvent event) {
        ConfigHelper.syncConfig(event.getModID());
    }

    @SubscribeEvent
    public void onLoadLootTable(LootTableLoadEvent event) {
        CustomLootHelper.loadCustomLoot(event.getName(), event.getTable());
    }

    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        DropHelper.addDrops(event.getWorld(), event.getState(), event.getFortuneLevel(), event.getDrops());
    }
}
