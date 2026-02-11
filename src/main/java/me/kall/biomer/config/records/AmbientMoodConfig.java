package me.kall.biomer.config.records;

import net.minecraft.resources.ResourceLocation;

public record AmbientMoodConfig(ResourceLocation soundEvent, int tickDelay, int blockSearchExtent, double soundPositionOffset) {}
