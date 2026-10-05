package com.dfsek.terra.bukkit.world.block;

import org.bukkit.World;
import org.bukkit.generator.LimitedRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;


public final class BlockEntityNBT {
    private static final Logger logger = LoggerFactory.getLogger(BlockEntityNBT.class);
    private static final AtomicBoolean warn = new AtomicBoolean(true);
    private static volatile Applicator applicator;

    private BlockEntityNBT() {
    }

    public static void register(Applicator impl) {
        applicator = impl;
    }

    public static void applyToRegion(LimitedRegion region, int x, int y, int z, String snbt) {
        Applicator impl = applicator;
        if(impl == null) {
            warnUnsupported(snbt);
            return;
        }
        impl.applyToRegion(region, x, y, z, snbt);
    }

    public static void applyToWorld(World world, int x, int y, int z, String snbt) {
        Applicator impl = applicator;
        if(impl == null) {
            warnUnsupported(snbt);
            return;
        }
        impl.applyToWorld(world, x, y, z, snbt);
    }

    private static void warnUnsupported(String snbt) {
        if(warn.getAndSet(false)) {
            logger.warn("No NBT applicator registered; block entity NBT data will be ignored: {}", snbt);
        }
    }


    public interface Applicator {
        void applyToRegion(LimitedRegion region, int x, int y, int z, String snbt);

        void applyToWorld(World world, int x, int y, int z, String snbt);
    }
}
