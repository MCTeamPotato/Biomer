package me.kall.biomer.config.records;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

public record AmbientMoodData(ResourceLocation soundEvent, int tickDelay, int blockSearchExtent, double soundPositionOffset) {
    public SoundEvent getSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(this.soundEvent);
    }
}
