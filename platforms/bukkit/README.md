# FoliaTerra Bukkit platform: what's left

Status as of 2026-11-18.

## Building

- Gradle must run on JDK 25. `./inspect.sh` sets this itself; override the JDK path with `JAVA_HOME_25`.
- `./inspect.sh` rebuilds `:platforms:bukkit` from scratch and reports 0 errors, 0 warnings.
- The bundled Overworld, ReimagEND and Tartarus packs are built from upstream commits pinned in `buildSrc/src/main/kotlin/Versions.kt`. They're pinned to the last commits before upstream's "port to Terra 26.3 environment attributes", which needs a `config-attributes` addon this fork doesn't have.
- Infinites (`WISP SMP/Source/Infinites`) pins Overworld the same way in `build.sh`.

## Tested

- **Paper 26.3-244, 2026-11-18, pushed build (FoliaTerra `9ea5ec42c`, Infinites `451f595`):**
  - all 30 Infinites packs load and all 30 worlds are created;
  - 256 chunks force-generated per world, 26–93 s each, no exceptions;
  - 0 corrupt chunks and 0 vanilla biomes in all 30 worlds. Skybound's empty chunks are its intended void gaps.
  - Surface checks fit the descriptions:
    - Wildflower: 60% flowers, 1% water, y=62–75;
    - Murkwater: 63% water;
    - Skybound: 67% void;
    - Tropical: 53% sea with islands;
    - Ashen has lava rivers, Crimson has dry canyons, and Hollow's sinkholes reach y=−1.
- **Canvas (Folia) 1.21.11, 2026-11-16:** Terra injects, and Infinites creates its worlds on the global region thread. Only spawn chunks were checked.

## Known limits

- The **autumn** pack only works on 26.x: `minecraft:poplar_log` doesn't exist in 1.21.11, so the pack is skipped there.
- Creating many worlds on Folia stalls the global region while each one prepares its spawn. Infinites creates one world per tick, which spreads the stall out but doesn't remove it.
- When the server stops while chunks are still generating, Paper logs "Accessing poi chunk off-main" errors from Terra structures. This only happens during shutdown.
- ReimagEND and Tartarus warn that they want `biome-provider-image` 1.x while 2.0.0-BETA is installed. They load and generate anyway.
- The original vanilla patches couldn't be reproduced: every scan on the current build came back clean.

## Still to do

1. **Alien Frontier fix (uncommitted in Infinites):** `packs/alien/biomes/infinite/alien_infinite.yml` now has `tags: []` instead of `LAND_CAVES`. The extruded cave biomes kept sea level 62 and flooded the craters, making the world 58% water. Rebuild with `./build.sh`, boot one test server with only that pack, force-load an area and check the craters are dry. Then commit ("Keep alien craters dry") and push.
2. **Volcanic spawn:** the whole 256×256 area around spawn sits on the volcano (surface y=126–249, no grass). If players should spawn on the green lower slopes, shrink the cone (`dist / 0.45` in `volcanic_infinite.yml`) or raise the cellular frequency.
3. **Folia large-area test:** vanilla `forceload` only marks chunks on Folia. Pregenerate with a Folia-aware tool (for example Chunky) on Canvas and rescan.
4. **In-game screenshots:** none taken yet. Join with the Prism `26.3` instance and grab one view per world.
5. **Infinites Discord webhook:** none set up. It needs a webhook URL from the channel settings, with the `push` event.

## Testing rules

- Run one test server at a time, with `-Xmx4G` or less, and stop it as soon as the check is done.
- Before starting one, make sure no other `java ... server.jar` is running.
- Region scanner: in 26.x chunks, palette names are under `id`, and blocks without properties use an empty key. Read both, or plain blocks (flowers, grass) look like air.
