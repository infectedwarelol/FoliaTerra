# FoliaTerra Bukkit platform: pending work

Status as of 2026-11-16. Read this before resuming.

## Context

- Broken worlds: incomplete terrain, corrupted chunks, patches of vanilla terrain mixed in.
- The packs live in the Infinites repo, not here: `/home/jules/Documents/Travaille/Minecraft/Servers/SMPs/WISP SMP/Infinites` (GitHub `infectedwarelol/Infinites`).
  - Pack sources: `packs/<name>/`. They are overlays merged onto TerraOverworldConfig by `build.sh`, which writes `dist/<ID>_INFINITE[.zip]`.
  - Each pack is registered in `build.sh`, `bukkit.yml` and `Infinites/src/main/resources/config.yml`.
- Pack audit: all YAML parses and every reference resolves. The packs themselves look fine.

## Building

- Gradle must run on JDK 25. The paperclip patch for 26.1+ refuses Java 21, which is the system default.
- `./inspect.sh` sets this itself; override the JDK path with `JAVA_HOME_25`.
- `./inspect.sh` builds `:platforms:bukkit` only. Upstream `common/addons` javac warnings are out of scope.

## Done and pushed to origin/master

- `ba1a9865c` Make world injection atomic and log failures (`NMSInjectListener`).
- `f8be51cb5` Update block populator pack on reload (`BukkitChunkGeneratorWrapper.setPack`).
- `713693c11` Treat NMS max height as exclusive like Bukkit (`NMSWorldProperties`, both `NMSChunkGeneratorDelegate`, `NMSStructureBeardifier`).
- `1bcfc1629` Let the NMS aggregator bundle Java 25 bindings (`disableAutoTargetJvm`). The full Bukkit build had been broken since `1bffa58ce`.
- `a15b9fa0e` and `9408eae2b` inspect.sh.
- Biome cache no longer preallocates about 1M slots per thread (`CachingBiomeProvider`).

## Done in Infinites (pushed to origin/main)

- New packs, one commit each: wildflower (Wildflower Fields), archipelago, mesa, tundra, jungle, dunes, cliffs, delta, sequoia, volcanic.
- `inspect.sh` added: it builds the packs, checks references and registration, builds the plugin, and checks sources.
- These packs have not been loaded on a live server yet. The first boot shows whether Terra accepts them.

## Ruled out

- 26.3 delegate missing its `applyCarvers`/`buildSurface` overrides. In Leaf 26.3, vanilla noise, surface and carvers only run when the `shouldGenerate*` flags allow it, and Terra returns false for all three.

## Still to do

1. Find the real vanilla-patch root cause. This needs a server log from an affected world (full startup and the first chunk errors), plus the server software and MC version. Leads, unconfirmed:
   - After `/terra reload`, the NMS delegate keeps the old pack for height lookups and the beardifier until restart.
   - `BukkitProtoWorld.access` silently drops populator writes outside the `LimitedRegion` and reads air there.
   - `NoiseChunkGenerator3D.getBlock` ignores carvers, while `generateChunkData` applies them.
   - The pack.yml `addons:` version ranges are possibly never checked against the installed addons.
2. Boot a server with the new Infinites jar and check for config errors in all 15 packs. Tune Wildflower Fields: flower density is `threshold: 0.45` in `wildflower_meadow_cover.yml`, and pond frequency comes from `base`/`amplitude` in `wildflower_infinite.yml`.
