package com.instanttravel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Cancels the "Loading terrain" screen on dimension changes. The very first one after joining a
 * world is left alone, so the initial world load behaves exactly like vanilla.
 */
@Mod.EventBusSubscriber(modid = InstantTravelMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LoadingScreenSkipper {
    private static long loginMillis = 0L;

    private LoadingScreenSkipper() {}

    @SubscribeEvent
    public static void onLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        loginMillis = System.currentTimeMillis();
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (!TravelConfig.SKIP_LOADING_SCREEN.get()) return;
        if (!(event.getNewScreen() instanceof ReceivingLevelScreen)) return;
        if (Minecraft.getInstance().level == null) return;
        if (System.currentTimeMillis() - loginMillis < 5000L) return;
        event.setCanceled(true);
    }
}
