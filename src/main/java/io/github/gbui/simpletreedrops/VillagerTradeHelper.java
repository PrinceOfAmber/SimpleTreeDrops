package io.github.gbui.simpletreedrops;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public class VillagerTradeHelper {
    public static void addVillagerTrade(String name, int careerID, int careerLevel, EntityVillager.ITradeList... trades) {
        IForgeRegistry<VillagerProfession> registry = VillagerRegistry.instance().getRegistry();
        VillagerProfession profession = registry.getValue(new ResourceLocation(name));
        VillagerCareer career = profession.getCareer(careerID);
        career.addTrade(careerLevel, trades);
    }
}
