package me.kall.biomer.config;

import it.unimi.dsi.fastutil.objects.*;
import me.kall.biomer.config.records.AmbientAdditionsConfig;
import me.kall.biomer.config.records.AmbientMoodConfig;
import me.kall.biomer.config.records.AmbientParticleConfig;
import me.kall.biomer.config.records.MusicConfig;
import me.kall.biomer.mixin.AmbientParticleSettingsAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.*;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Map;
import java.util.Optional;

public class BiomeArgs {
    public final Object2BooleanMap<ResourceLocation> hasPrecipitation = new Object2BooleanOpenHashMap<>();
    public final Object2DoubleMap<ResourceLocation> temperature = new Object2DoubleOpenHashMap<>(), downfall = new Object2DoubleOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, Biome.TemperatureModifier> temperatureModifier = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, BiomeSpecialEffects.GrassColorModifier> grassColorModifier = new Object2ObjectOpenHashMap<>();
    public final Object2IntMap<ResourceLocation> fogColor = new Object2IntOpenHashMap<>(), waterColor = new Object2IntOpenHashMap<>(), waterFogColor = new Object2IntOpenHashMap<>(), skyColor = new Object2IntOpenHashMap<>(), foliageColorOverride = new Object2IntOpenHashMap<>(), grassColorOverride = new Object2IntOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, AmbientParticleConfig> ambientParticleSettings = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, ResourceLocation> ambientLoopSoundEvent = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, AmbientMoodConfig> ambientMoodSettings = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, AmbientAdditionsConfig> ambientAdditionsSettings = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, MusicConfig> backgroundMusic = new Object2ObjectOpenHashMap<>();

    public BiomeArgs() {
        ServerLifecycleHooks.getCurrentServer().registryAccess().registry(Registries.BIOME).ifPresent(biomeRegistry -> {
            for (Map.Entry<ResourceKey<Biome>, Biome> entry : biomeRegistry.entrySet()) {
                Biome biome = entry.getValue();
                ResourceLocation id = entry.getKey().location();

                Biome.ClimateSettings climateSettings = biome.getModifiedClimateSettings();
                boolean hasPrecipitation = climateSettings.hasPrecipitation();
                float temperature = climateSettings.temperature();
                Biome.TemperatureModifier temperatureModifier = climateSettings.temperatureModifier();
                float downfall = climateSettings.downfall();

                BiomeSpecialEffects specialEffects = biome.getModifiedSpecialEffects();
                int fogColor = specialEffects.getFogColor();
                int waterColor = specialEffects.getWaterColor();
                int waterFogColor = specialEffects.getWaterFogColor();
                int skyColor = specialEffects.getSkyColor();
                Optional<Integer> foliageColorOverride = specialEffects.getFoliageColorOverride();
                Optional<Integer> grassColorOverride = specialEffects.getGrassColorOverride();
                BiomeSpecialEffects.GrassColorModifier grassColorModifier = specialEffects.getGrassColorModifier();
                Optional<AmbientParticleSettings> ambientParticleSettings = specialEffects.getAmbientParticleSettings();
                Optional<Holder<SoundEvent>> ambientLoopSoundEvent = specialEffects.getAmbientLoopSoundEvent();
                Optional<AmbientMoodSettings> ambientMoodSettings = specialEffects.getAmbientMoodSettings();
                Optional<AmbientAdditionsSettings> ambientAdditionsSettings = specialEffects.getAmbientAdditionsSettings();
                Optional<Music> backgroundMusic = specialEffects.getBackgroundMusic();


                this.hasPrecipitation.put(id, hasPrecipitation);
                this.temperature.put(id, temperature);
                this.temperatureModifier.put(id, temperatureModifier);
                this.downfall.put(id, downfall);

                this.fogColor.put(id, fogColor);
                this.waterColor.put(id, waterColor);
                this.waterFogColor.put(id, waterFogColor);
                this.skyColor.put(id, skyColor);
                foliageColorOverride.ifPresent(value -> this.foliageColorOverride.put(id, value.intValue()));
                grassColorOverride.ifPresent(value -> this.grassColorOverride.put(id, value.intValue()));
                this.grassColorModifier.put(id, grassColorModifier);
                ambientParticleSettings.ifPresent(settings -> this.ambientParticleSettings.put(id, new AmbientParticleConfig(ForgeRegistries.PARTICLE_TYPES.getKey(settings.getOptions().getType()), ((AmbientParticleSettingsAccessor)settings).getProbability())));
                ambientLoopSoundEvent.ifPresent(soundEventHolder -> this.ambientLoopSoundEvent.put(id, soundEventHolder.value().getLocation()));
                ambientMoodSettings.ifPresent(mood -> this.ambientMoodSettings.put(id, new AmbientMoodConfig(mood.getSoundEvent().value().getLocation(), mood.getTickDelay(), mood.getBlockSearchExtent(), mood.getSoundPositionOffset())));
                ambientAdditionsSettings.ifPresent(additions -> this.ambientAdditionsSettings.put(id, new AmbientAdditionsConfig(additions.getSoundEvent().value().getLocation(), additions.getTickChance())));
                backgroundMusic.ifPresent(music -> this.backgroundMusic.put(id, new MusicConfig(music.getEvent().value().getLocation(), music.getMinDelay(), music.getMaxDelay(), music.replaceCurrentMusic())));
            }
        });
    }
}
