package com.dfsek.terra.bukkit.nms;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.generator.CraftLimitedRegion;
import org.bukkit.generator.LimitedRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dfsek.terra.bukkit.world.block.BlockEntityNBT;


public class NMSNBTApplicator implements BlockEntityNBT.Applicator {
    private static final Logger logger = LoggerFactory.getLogger(NMSNBTApplicator.class);

    @Override
    public void applyToRegion(LimitedRegion region, int x, int y, int z, String snbt) {
        apply(((CraftLimitedRegion) region).getHandle(), x, y, z, snbt);
    }

    @Override
    public void applyToWorld(World world, int x, int y, int z, String snbt) {
        apply(((CraftWorld) world).getHandle(), x, y, z, snbt);
    }

    private static void apply(LevelAccessor level, int x, int y, int z, String snbt) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity == null) {
            logger.warn("No block entity at ({}, {}, {}); cannot apply NBT data: {}", x, y, z, snbt);
            return;
        }
        try {
            CompoundTag tag = TagParser.parseCompoundFully(snbt);
            blockEntity.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), tag));
            blockEntity.setChanged();
        } catch(Exception e) {
            logger.warn("Failed to apply NBT data \"{}\" at ({}, {}, {}): {}", snbt, x, y, z, e.toString());
        }
    }
}
