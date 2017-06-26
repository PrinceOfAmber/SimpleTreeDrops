package io.github.gbui.simpletreedrops;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {}

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new ConfigGui(parentScreen);
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ConfigGui.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Nullable
    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

    public static class ConfigGui extends GuiConfig {
        public ConfigGui(GuiScreen parentScreen) {
            super(parentScreen, getConfigElements(), SimpleTreeDrops.MODID, false, false, I18n.format(ConfigHelper.LANGUAGE_KEY_PREFIX + "title"));
        }

        private static List<IConfigElement> getConfigElements() {
            List<IConfigElement> list = new ArrayList<IConfigElement>();
            Configuration config = ConfigHelper.getConfig();
            for (String categoryName : ConfigHelper.GENERAL_CATEGORIES) {
                ConfigElement configElement = new ConfigElement(config.getCategory(categoryName));
                list.addAll(configElement.getChildElements());
            }
            for (String categoryName : ConfigHelper.SPECIALIZED_CATEGORIES) {
                list.add(new ConfigElement(config.getCategory(categoryName)));
            }
            return list;
        }
    }
}
