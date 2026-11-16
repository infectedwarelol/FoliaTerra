package com.dfsek.terra.bukkit.nms;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.dfsek.terra.api.config.ConfigPack;
import com.dfsek.terra.bukkit.generator.BukkitChunkGeneratorWrapper;


public class NMSInjectListener implements Listener {
    private static final Logger LOGGER = LoggerFactory.getLogger(NMSInjectListener.class);
    private static final Set<UUID> INJECTED = ConcurrentHashMap.newKeySet();
    private final NMSVersionBindings bindings;

    public NMSInjectListener(NMSVersionBindings bindings) {
        this.bindings = bindings;
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        if(!(event.getWorld().getGenerator() instanceof BukkitChunkGeneratorWrapper bukkitChunkGeneratorWrapper)) {
            return;
        }
        if(!INJECTED.add(event.getWorld().getUID())) {
            return;
        }
        try {
            LOGGER.info("Preparing to take over the world: {}", event.getWorld().getName());
            CraftWorld craftWorld = (CraftWorld) event.getWorld();
            ServerLevel serverWorld = craftWorld.getHandle();

            ConfigPack pack = bukkitChunkGeneratorWrapper.getPack();

            ChunkGenerator vanilla = serverWorld.getChunkSource().getGenerator();
            ChunkMap chunkMap = serverWorld.getChunkSource().chunkMap;
            WorldGenContext worldGenContext = Reflection.CHUNKMAP.getWorldGenContext(chunkMap);
            Reflection.CHUNKMAP.setWorldGenContext(chunkMap, new WorldGenContext(
                worldGenContext.level(),
                bindings.createChunkGenerator(vanilla, bukkitChunkGeneratorWrapper, bindings.createBiomeSource(pack.getBiomeProvider(), craftWorld.getSeed()),
                    craftWorld.getSeed()),
                worldGenContext.structureManager(),
                worldGenContext.lightEngine(),
                worldGenContext.mainThreadExecutor(),
                worldGenContext.unsavedListener()
            ));

            LOGGER.info("Successfully injected into world.");
        } catch(RuntimeException exception) {
            INJECTED.remove(event.getWorld().getUID());
            LOGGER.error("Failed to inject into world {}.", event.getWorld().getName(), exception);
        }
    }
}
