package me.kall.biomer.config.records;

import net.minecraft.resources.ResourceLocation;

public record MusicConfig(ResourceLocation soundEvent, int minDelay, int maxDelay, boolean replaceCurrentMusic) {}
