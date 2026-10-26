package com.instanttravel;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Watches every player once per server tick. When a player's dimension changes (TARDIS door,
 * portal, anything), the area they just left gets a long-lived chunk ticket. Doors almost always
 * lead straight back to the same place, so the return trip finds its chunks already loaded.
 */
public class WarmTracker {
    private static final int WARM_TICKS = 20 * 60 * 10; // 10 minutes

    private static final TicketType<ChunkPos> WARM = TicketType.create(
            "instanttravel_warm", Comparator.comparingLong(ChunkPos::toLong), WARM_TICKS);

    private record Last(ResourceKey<Level> dim, ChunkPos chunk) {}

    private record Pending(long startNanos, int startTick, ResourceKey<Level> dim) {}

    private final Map<UUID, Last> last = new HashMap<>();
    private final Map<UUID, Pending> pending = new HashMap<>();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        MinecraftServer server = event.getServer();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            UUID id = player.getUUID();
            ServerLevel level = player.serverLevel();
            ResourceKey<Level> dim = level.dimension();
            ChunkPos now = player.chunkPosition();

            Last prev = last.get(id);
            if (prev != null && !prev.dim().equals(dim)) {
                ServerLevel origin = server.getLevel(prev.dim());
                if (origin != null) {
                    warm(origin, prev.chunk());
                }
                if (TravelConfig.DEBUG_TIMING.get()) {
                    pending.put(id, new Pending(System.nanoTime(), server.getTickCount(), dim));
                }
            }
            last.put(id, new Last(dim, now));

            Pending p = pending.get(id);
            if (p != null) {
                boolean ready = level.getChunkSource().hasChunk(now.x, now.z);
                boolean timedOut = server.getTickCount() - p.startTick() > 400;
                if (ready || timedOut) {
                    long ms = (System.nanoTime() - p.startNanos()) / 1_000_000L;
                    InstantTravelMod.LOGGER.info("[instanttravel] {} arrived in {}: arrival chunk {} after {} ms ({} ticks)",
                            player.getGameProfile().getName(), p.dim().location(),
                            ready ? "ready" : "NOT ready (timed out)", ms, server.getTickCount() - p.startTick());
                    pending.remove(id);
                }
            }
        }
    }

    private static void warm(ServerLevel level, ChunkPos center) {
        level.getChunkSource().addRegionTicket(WARM, center, TravelConfig.WARM_RADIUS.get(), center);
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        last.remove(event.getEntity().getUUID());
        pending.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public void onStopping(ServerStoppingEvent event) {
        last.clear();
        pending.clear();
    }
}
