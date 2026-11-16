# FoliaTerra Bukkit platform: what's left

Status as of 2026-11-16.

## Building

- Gradle must run on JDK 25. `./inspect.sh` sets this itself; override the JDK path with `JAVA_HOME_25`.
- `./inspect.sh` rebuilds `:platforms:bukkit` from scratch and reports 0 errors, 0 warnings.
- The bundled Overworld, ReimagEND and Tartarus packs are built from upstream commits pinned in `buildSrc/src/main/kotlin/Versions.kt`. They're pinned to the last commits before upstream's "port to Terra 26.3 environment attributes", which needs a `config-attributes` addon this fork doesn't have.
- Infinites pins Overworld the same way in `build.sh`.

## Tested

- **Paper 26.3-244:**
  - all 18 packs load: the 15 Infinites packs plus the 3 bundled ones;
  - 3,840 chunks force-generated across 15 worlds;
  - 0 corrupt chunks, 0 empty chunks, 0 vanilla biomes in fully generated chunks.
- **Canvas (Folia) 1.21.11:**
  - Terra injects into the main world, and Infinites creates the other worlds on the global region thread;
  - the spawn chunks of 6 worlds have 0 corrupt chunks and 0 vanilla biomes;
  - Wildflower Fields came out at 77% flowers, no trees, surface y=65–70.

## Known limits

- The **autumn** pack only works on 26.x: `minecraft:poplar_log` doesn't exist in 1.21.11, so the pack is skipped there.
- On Folia, vanilla `forceload` only marks chunks, so large-area generation wasn't stress-tested there. Use a Folia-aware pregenerator (for example Chunky) to test it.
- Creating many worlds on Folia stalls the global region while each one prepares its spawn. Infinites now creates one world per tick, which spreads the stall out but doesn't remove it.
- When the server stops while chunks are still generating, Paper logs "Accessing poi chunk off-main" errors from Terra structures. This only happens during shutdown.
- ReimagEND and Tartarus log a warning that they want `biome-provider-image` 1.x while 2.0.0-BETA is installed. They load and generate anyway.
- The cause of the original vanilla patches couldn't be reproduced on the current build: every scan came back clean. If it shows up again, run the region scanner on the affected world and attach the server log.

## Still to do

1. Add a Discord webhook for Infinites, so its pushes show in Discord. It needs a webhook URL from the channel's settings.
2. Pregenerate a large Folia area with Chunky and rescan it.
