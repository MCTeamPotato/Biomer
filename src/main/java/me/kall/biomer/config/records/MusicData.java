package me.kall.biomer.config.records;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

public record MusicData(ResourceLocation soundEvent, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
    public SoundEvent getSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(this.soundEvent);
    }
}
