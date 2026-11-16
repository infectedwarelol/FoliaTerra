# FoliaTerra Bukkit platform: what's left

Status as of 2026-11-16.

## Building

- Gradle must run on JDK 25. `./inspect.sh` sets this itself; override the JDK path with `JAVA_HOME_25`.
- `./inspect.sh` rebuilds `:platforms:bukkit` from scratch and currently reports 0 errors, 0 warnings.

## Verified on a live server (Paper 26.3-244, local test)

- All 15 Infinites packs load without config errors.
- All 15 worlds are created and Terra injects into each of them.
- Not verified: chunk contents (vanilla patches, empty sections), and whether Wildflower Fields looks right in game.

## Still to do

1. **Find what causes the vanilla patches and corrupted chunks.**
   - Next step: on the local test server, force-load an area in each world (`execute in minecraft:<world> run forceload add -128 -128 127 127`).
   - Then read the region files and look for vanilla biome ids (anything that isn't `terra:`) or empty chunk sections.
   - A log from the real server would help most.
2. **Test on Folia.** Only Paper has been tested. Boot Canvas (`~/Downloads/canvas-build.794.jar`, 1.21.11) with one Infinites world as `level-name`. Folia can't create worlds at runtime, so it's one world per boot.
3. **Bundled default packs fail to load.** Overworld, Tartarus and ReimagEND need the `config-attributes` addon, which the fork doesn't ship. Either add that addon or stop Terra downloading those packs.
4. **Tune Wildflower Fields in game.**
   - Flower density: `threshold: 0.45` in `wildflower_meadow_cover.yml`.
   - Ponds and hills: `base` and `amplitude` in `wildflower_infinite.yml`.
5. **Discord webhook for Infinites.** It has none, so its pushes don't show in Discord.

## Done this session

- World injection is atomic and failures are logged.
- On reload, the populator and the NMS generator use the current pack.
- NMS max height is treated as exclusive, so the top block is no longer skipped.
- Single-block lookups now account for caves, so height checks match the generated chunk.
- Terra warns when a pack wants a different addon version.
- The biome cache no longer preallocates about 1M slots per thread.
- Deprecated APIs replaced: Adventure for command messages, the registry for enchantments, and schedulers fetched from the server when needed.
- The NMS aggregator can bundle the Java 25 bindings.
- inspect.sh added.
- Infinites: 10 new packs and its own inspect.sh.
