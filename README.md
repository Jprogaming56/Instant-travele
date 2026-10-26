# Instant Travel (Forge 1.20.1)

Cuts the wait when going through AiT TARDIS doors (or any dimension change).

- **Warm return trip (server):** every time a player changes dimension, the chunks around the spot they just left stay loaded for 10 minutes. So exterior -> interior -> exterior (and back) finds everything already loaded after the first cycle.
- **Skip "Loading terrain" (client):** cancels the loading screen on dimension changes. Toggle in `instanttravel-client.toml` (`skipLoadingScreen`).
- **Timing log:** set `debugTiming = true` in `instanttravel-common.toml` to log how long the arrival chunk takes server-side.

No dependency on AiT or AmbleKit; it only watches dimension changes, so it can't break when they update.

## Build
Push to GitHub. The Actions workflow builds the jar: Actions tab -> latest run -> `instanttravel-jar` artifact.

## Limits
The first-ever trip into a given interior is still cold. Rendering the new dimension on the client still takes a moment.
