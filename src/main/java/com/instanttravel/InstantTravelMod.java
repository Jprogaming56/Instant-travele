package com.instanttravel;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(InstantTravelMod.MOD_ID)
public class InstantTravelMod {
    public static final String MOD_ID = "instanttravel";
    public static final Logger LOGGER = LogUtils.getLogger();

    public InstantTravelMod() {
        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, TravelConfig.COMMON_SPEC);
        ctx.registerConfig(ModConfig.Type.CLIENT, TravelConfig.CLIENT_SPEC);
        MinecraftForge.EVENT_BUS.register(new WarmTracker());
    }
}
