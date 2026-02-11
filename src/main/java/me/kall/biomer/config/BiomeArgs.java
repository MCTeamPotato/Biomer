package me.kall.biomer.config;

import it.unimi.dsi.fastutil.objects.*;
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

import java.util.*;

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

    public boolean hasPrecipitation(ResourceLocation biomeId) {
        return this.hasPrecipitation.getBoolean(biomeId);
    }

    public double getTemperature(ResourceLocation biomeId) {
        return this.temperature.getDouble(biomeId);
    }

    public double getDownfall(ResourceLocation biomeId) {
        return this.downfall.getDouble(biomeId);
    }

    public Biome.TemperatureModifier getTemperatureModifier(ResourceLocation biomeId) {
        return this.temperatureModifier.get(biomeId);
    }

    public int getFogColor(ResourceLocation biomeId) {
        return this.fogColor.getInt(biomeId);
    }

    public int getWaterColor(ResourceLocation biomeId) {
        return this.waterColor.getInt(biomeId);
    }

    public int getWaterFogColor(ResourceLocation biomeId) {
        return this.waterFogColor.getInt(biomeId);
    }

    public int getSkyColor(ResourceLocation biomeId) {
        return this.skyColor.getInt(biomeId);
    }

    public Optional<Integer> getFoliageColorOverride(ResourceLocation biomeId) {
        return this.foliageColorOverride.containsKey(biomeId) ? Optional.of(this.foliageColorOverride.getInt(biomeId)) : Optional.empty();
    }

    public Optional<Integer> getGrassColorOverride(ResourceLocation biomeId) {
        return this.grassColorOverride.containsKey(biomeId) ? Optional.of(this.grassColorOverride.getInt(biomeId)) : Optional.empty();
    }

    public BiomeSpecialEffects.GrassColorModifier getGrassColorModifier(ResourceLocation biomeId) {
        return this.grassColorModifier.get(biomeId);
    }

    public Optional<ResourceLocation> getAmbientLoopSoundEvent(ResourceLocation biomeId) {
        return Optional.ofNullable(this.ambientLoopSoundEvent.get(biomeId));
    }

    public Optional<AmbientParticleConfig> getAmbientParticleConfig(ResourceLocation biomeId) {
        return Optional.ofNullable(this.ambientParticleSettings.get(biomeId));
    }

    public Optional<ResourceLocation> getParticleOptions(ResourceLocation biomeId) {
        AmbientParticleConfig config = this.ambientParticleSettings.get(biomeId);
        return config != null ? Optional.of(config.particleOptions()) : Optional.empty();
    }

    public Optional<Float> getParticleProbability(ResourceLocation biomeId) {
        AmbientParticleConfig config = this.ambientParticleSettings.get(biomeId);
        return config != null ? Optional.of(config.probability()) : Optional.empty();
    }

    public Optional<AmbientMoodConfig> getAmbientMoodConfig(ResourceLocation biomeId) {
        return Optional.ofNullable(this.ambientMoodSettings.get(biomeId));
    }

    public Optional<ResourceLocation> getMoodSoundEvent(ResourceLocation biomeId) {
        AmbientMoodConfig config = this.ambientMoodSettings.get(biomeId);
        return config != null ? Optional.of(config.soundEvent()) : Optional.empty();
    }

    public Optional<Integer> getMoodTickDelay(ResourceLocation biomeId) {
        AmbientMoodConfig config = this.ambientMoodSettings.get(biomeId);
        return config != null ? Optional.of(config.tickDelay()) : Optional.empty();
    }

    public Optional<Integer> getMoodBlockSearchExtent(ResourceLocation biomeId) {
        AmbientMoodConfig config = this.ambientMoodSettings.get(biomeId);
        return config != null ? Optional.of(config.blockSearchExtent()) : Optional.empty();
    }

    public Optional<Double> getMoodSoundPositionOffset(ResourceLocation biomeId) {
        AmbientMoodConfig config = this.ambientMoodSettings.get(biomeId);
        return config != null ? Optional.of(config.soundPositionOffset()) : Optional.empty();
    }

    public Optional<AmbientAdditionsConfig> getAmbientAdditionsConfig(ResourceLocation biomeId) {
        return Optional.ofNullable(this.ambientAdditionsSettings.get(biomeId));
    }

    public Optional<ResourceLocation> getAdditionsSoundEvent(ResourceLocation biomeId) {
        AmbientAdditionsConfig config = this.ambientAdditionsSettings.get(biomeId);
        return config != null ? Optional.of(config.soundEvent()) : Optional.empty();
    }

    public Optional<Double> getAdditionsTickChance(ResourceLocation biomeId) {
        AmbientAdditionsConfig config = this.ambientAdditionsSettings.get(biomeId);
        return config != null ? Optional.of(config.tickChance()) : Optional.empty();
    }

    public Optional<MusicConfig> getMusicConfig(ResourceLocation biomeId) {
        return Optional.ofNullable(this.backgroundMusic.get(biomeId));
    }

    public Optional<ResourceLocation> getMusicSoundEvent(ResourceLocation biomeId) {
        MusicConfig config = this.backgroundMusic.get(biomeId);
        return config != null ? Optional.of(config.soundEvent()) : Optional.empty();
    }

    public Optional<Integer> getMusicMinDelay(ResourceLocation biomeId) {
        MusicConfig config = this.backgroundMusic.get(biomeId);
        return config != null ? Optional.of(config.minDelay()) : Optional.empty();
    }

    public Optional<Integer> getMusicMaxDelay(ResourceLocation biomeId) {
        MusicConfig config = this.backgroundMusic.get(biomeId);
        return config != null ? Optional.of(config.maxDelay()) : Optional.empty();
    }

    public Optional<Boolean> getMusicReplacesCurrent(ResourceLocation biomeId) {
        MusicConfig config = this.backgroundMusic.get(biomeId);
        return config != null ? Optional.of(config.replaceCurrentMusic()) : Optional.empty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BiomeArgs {\n");

        Set<ResourceLocation> allBiomes = this.hasPrecipitation.keySet();
        List<ResourceLocation> sortedBiomes = new ArrayList<>(allBiomes);
        sortedBiomes.sort(Comparator.comparing(ResourceLocation::toString));

        sb.append("  \"biomes\": [\n");

        int biomeCount = 0;
        for (ResourceLocation biomeId : sortedBiomes) {
            if (biomeCount > 0) {
                sb.append(",\n");
            }
            sb.append("    {\n");
            sb.append("      \"id\": \"").append(biomeId).append("\",\n");

            sb.append("      \"climate\": {\n");
            sb.append("        \"hasPrecipitation\": ").append(this.hasPrecipitation.getBoolean(biomeId)).append(",\n");
            sb.append("        \"temperature\": ").append(this.temperature.getDouble(biomeId)).append(",\n");
            sb.append("        \"downfall\": ").append(this.downfall.getDouble(biomeId)).append(",\n");
            sb.append("        \"temperatureModifier\": \"").append(this.temperatureModifier.get(biomeId)).append("\"\n");
            sb.append("      },\n");

            sb.append("      \"colors\": {\n");
            sb.append("        \"fogColor\": \"").append(String.format("0x%06X", this.fogColor.getInt(biomeId))).append("\",\n");
            sb.append("        \"waterColor\": \"").append(String.format("0x%06X", this.waterColor.getInt(biomeId))).append("\",\n");
            sb.append("        \"waterFogColor\": \"").append(String.format("0x%06X", this.waterFogColor.getInt(biomeId))).append("\",\n");
            sb.append("        \"skyColor\": \"").append(String.format("0x%06X", this.skyColor.getInt(biomeId))).append("\",\n");

            if (this.foliageColorOverride.containsKey(biomeId)) {
                sb.append("        \"foliageColorOverride\": \"").append(String.format("0x%06X", this.foliageColorOverride.getInt(biomeId))).append("\",\n");
            }
            if (this.grassColorOverride.containsKey(biomeId)) {
                sb.append("        \"grassColorOverride\": \"").append(String.format("0x%06X", this.grassColorOverride.getInt(biomeId))).append("\",\n");
            }

            sb.append("        \"grassColorModifier\": \"").append(this.grassColorModifier.get(biomeId)).append("\"\n");
            sb.append("      },\n");

            sb.append("      \"ambientEffects\": {\n");

            List<String> ambientItems = new ArrayList<>();

            if (this.ambientParticleSettings.containsKey(biomeId)) {
                AmbientParticleConfig particleConfig = this.ambientParticleSettings.get(biomeId);
                String particles = "        \"particles\": {\n" +
                        "          \"type\": \"" + particleConfig.particleOptions() + "\",\n" +
                        "          \"probability\": " + particleConfig.probability() + "\n" +
                        "        }";
                ambientItems.add(particles);
            }

            if (this.ambientLoopSoundEvent.containsKey(biomeId)) {
                ambientItems.add("        \"loopSound\": \"" + this.ambientLoopSoundEvent.get(biomeId) + "\"");
            }

            if (this.ambientMoodSettings.containsKey(biomeId)) {
                AmbientMoodConfig moodConfig = this.ambientMoodSettings.get(biomeId);
                String mood = "        \"mood\": {\n" +
                        "          \"soundEvent\": \"" + moodConfig.soundEvent() + "\",\n" +
                        "          \"tickDelay\": " + moodConfig.tickDelay() + ",\n" +
                        "          \"blockSearchExtent\": " + moodConfig.blockSearchExtent() + ",\n" +
                        "          \"soundPositionOffset\": " + moodConfig.soundPositionOffset() + "\n" +
                        "        }";
                ambientItems.add(mood);
            }

            if (this.ambientAdditionsSettings.containsKey(biomeId)) {
                AmbientAdditionsConfig additionsConfig = this.ambientAdditionsSettings.get(biomeId);
                String additions = "        \"additions\": {\n" +
                        "          \"soundEvent\": \"" + additionsConfig.soundEvent() + "\",\n" +
                        "          \"tickChance\": " + additionsConfig.tickChance() + "\n" +
                        "        }";
                ambientItems.add(additions);
            }

            if (this.backgroundMusic.containsKey(biomeId)) {
                MusicConfig musicConfig = this.backgroundMusic.get(biomeId);
                String music = "        \"music\": {\n" +
                        "          \"soundEvent\": \"" + musicConfig.soundEvent() + "\",\n" +
                        "          \"minDelay\": " + musicConfig.minDelay() + ",\n" +
                        "          \"maxDelay\": " + musicConfig.maxDelay() + ",\n" +
                        "          \"replaceCurrentMusic\": " + musicConfig.replaceCurrentMusic() + "\n" +
                        "        }";
                ambientItems.add(music);
            }

            for (int i = 0; i < ambientItems.size(); i++) {
                sb.append(ambientItems.get(i));
                if (i < ambientItems.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }

            sb.append("      }\n");
            sb.append("    }");
            biomeCount++;
        }

        sb.append("\n  ]\n");
        sb.append("}");

        return sb.toString();
    }

    public record AmbientAdditionsConfig(ResourceLocation soundEvent, double tickChance) {}
    public record AmbientMoodConfig(ResourceLocation soundEvent, int tickDelay, int blockSearchExtent, double soundPositionOffset) {}
    public record AmbientParticleConfig(ResourceLocation particleOptions, float probability) {}
    public record MusicConfig(ResourceLocation soundEvent, int minDelay, int maxDelay, boolean replaceCurrentMusic) {}
}