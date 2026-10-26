package com.instanttravel;

import net.minecraftforge.common.ForgeConfigSpec;

public final class TravelConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec.IntValue WARM_RADIUS;
    public static final ForgeConfigSpec.BooleanValue DEBUG_TIMING;

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec.BooleanValue SKIP_LOADING_SCREEN;

    static {
        ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();
        WARM_RADIUS = common
                .comment("Radius (in chunks) kept loaded around the door you just left, for 10 minutes.",
                        "Bigger = safer for a smooth return trip, but uses a little more memory.")
                .defineInRange("warmRadius", 3, 1, 6);
        DEBUG_TIMING = common
                .comment("Log how long the server takes to have the arrival chunk ready after each dimension change.",
                        "Useful to tell whether remaining lag is server-side (chunks) or client-side (rendering).")
                .define("debugTiming", false);
        COMMON_SPEC = common.build();

        ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();
        SKIP_LOADING_SCREEN = client
                .comment("Skip the 'Loading terrain' screen when changing dimension.")
                .define("skipLoadingScreen", true);
        CLIENT_SPEC = client.build();
    }

    private TravelConfig() {}
}
