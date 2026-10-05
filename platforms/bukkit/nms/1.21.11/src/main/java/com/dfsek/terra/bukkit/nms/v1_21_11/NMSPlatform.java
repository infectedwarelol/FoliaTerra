package com.dfsek.terra.bukkit.nms.v1_21_11;

import com.dfsek.terra.bukkit.TerraBukkitPlugin;
import com.dfsek.terra.bukkit.nms.NMSPlatformBase;


public final class NMSPlatform extends NMSPlatformBase {
    public NMSPlatform(TerraBukkitPlugin plugin) {
        super(plugin, new Bindings());
    }
}
