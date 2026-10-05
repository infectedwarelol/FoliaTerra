/*
 * This file is part of Terra.
 *
 * Terra is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Terra is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Terra.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.dfsek.terra.bukkit.handles;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.entity.EntityType;
import com.dfsek.terra.api.handle.WorldHandle;
import com.dfsek.terra.bukkit.util.BukkitUtils;
import com.dfsek.terra.bukkit.world.block.data.BukkitBlockState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;


public class BukkitWorldHandle implements WorldHandle {
    private static final Logger logger = LoggerFactory.getLogger(BukkitWorldHandle.class);
    private static final Map<String, String> LEGACY_BLOCK_IDS = Map.of(
        "minecraft:chain", "minecraft:iron_chain",
        "chain", "iron_chain"
    );
    private final BlockState air;

    public BukkitWorldHandle() {
        this.air = BukkitBlockState.newInstance(Material.AIR.createBlockData());
    }

    @Override
    public synchronized @NotNull BlockState createBlockState(@NotNull String data) {
        String nbt = null;
        String stripped = data;
        int nbtStart = data.indexOf('{');
        if(nbtStart != -1) {
            nbt = data.substring(nbtStart);
            stripped = data.substring(0, nbtStart);
        }
        String blockData = remapLegacyId(stripped);
        try {
            org.bukkit.block.data.BlockData bukkitData = Bukkit.createBlockData(blockData);
            return BukkitBlockState.newInstance(bukkitData, nbt);
        } catch (Exception ignored) {
            try {
                Class<?> hacks = Class.forName("com.dfsek.terra.bukkit.nms.AwfulBukkitHacks");
                Method method = hacks.getMethod("createBlockState", String.class);
                Object result = method.invoke(null, blockData);
                return BukkitBlockState.newInstance((BlockData) result, nbt);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("Invalid block state data: " + data);
            }
        }
    }

    private static String remapLegacyId(String data) {
        int idEnd = data.length();
        for(int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            if(c == '[' || c == '{') {
                idEnd = i;
                break;
            }
        }
        String id = data.substring(0, idEnd).trim();
        String remapped = LEGACY_BLOCK_IDS.get(id);
        if(remapped == null) return data;
        logger.debug("Remapped legacy block id {} to {}", id, remapped);
        return remapped + data.substring(idEnd);
    }

    @Override
    public @NotNull BlockState air() {
        return air;
    }

    @Override
    public @NotNull EntityType getEntity(@NotNull String id) {
        return BukkitUtils.getEntityType(id);
    }
}
