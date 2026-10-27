package com.instanttravel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Closes the "Loading terrain" screen on dimension changes. It never touches the screens shown
 * while joining a world: it only starts working once the player has actually been in the world
 * with no screen open.
 */
@Mod.EventBusSubscriber(modid = InstantTravelMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LoadingScreenSkipper {
    private static boolean hasBeenInGame = false;

    private LoadingScreenSkipper() {}

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        hasBeenInGame = false;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null || mc.player == null) {
            hasBeenInGame = false;
            return;
        }
        if (mc.screen == null) {
            hasBeenInGame = true;
            return;
        }
        if (!hasBeenInGame || !TravelConfig.SKIP_LOADING_SCREEN.get()) return;

        if (mc.screen instanceof ReceivingLevelScreen) {
            mc.setScreen(null);
        }
    }
}
