package me.kall.biomer.config;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.kall.biomer.config.records.AmbientAdditionsConfig;
import me.kall.biomer.config.records.AmbientMoodConfig;
import me.kall.biomer.config.records.AmbientParticleConfig;
import me.kall.biomer.config.records.MusicConfig;
import me.kall.biomer.mixin.AmbientParticleSettingsAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.*;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Map;
import java.util.Optional;

public class BiomeConfig {
    public final ForgeConfigSpec biomeConfig;
    public final Object2ObjectMap<ResourceLocation, ForgeConfigSpec.BooleanValue> hasPrecipitation = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, ForgeConfigSpec.DoubleValue> temperature = new Object2ObjectOpenHashMap<>(), downfall = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, ForgeConfigSpec.EnumValue<Biome.TemperatureModifier>> temperatureModifier = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, ForgeConfigSpec.EnumValue<BiomeSpecialEffects.GrassColorModifier>> grassColorModifier = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, ForgeConfigSpec.IntValue> fogColor = new Object2ObjectOpenHashMap<>(), waterColor = new Object2ObjectOpenHashMap<>(), waterFogColor = new Object2ObjectOpenHashMap<>(), skyColor = new Object2ObjectOpenHashMap<>(), foliageColorOverride = new Object2ObjectOpenHashMap<>(), grassColorOverride = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, ForgeConfigSpec.ConfigValue<AmbientParticleConfig>> ambientParticleSettings = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, ForgeConfigSpec.ConfigValue<ResourceLocation>> ambientLoopSoundEvent = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, ForgeConfigSpec.ConfigValue<AmbientMoodConfig>> ambientMoodSettings = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, ForgeConfigSpec.ConfigValue<AmbientAdditionsConfig>> ambientAdditionsSettings = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, ForgeConfigSpec.ConfigValue<MusicConfig>> backgroundMusic = new Object2ObjectOpenHashMap<>();

    public BiomeConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("Biomer");

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        server.registryAccess().registry(Registries.BIOME).ifPresent(biomeRegistry -> {
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

                builder.push(id.toString());
                builder.push("ClimateSettings");
                this.hasPrecipitation.put(id, builder.define("HasPrecipitation", hasPrecipitation));
                this.temperature.put(id, builder.defineInRange("Temperature", temperature, Float.MIN_VALUE, Float.MAX_VALUE));
                this.temperatureModifier.put(id, builder.defineEnum("TemperatureModifier", temperatureModifier));
                this.downfall.put(id, builder.defineInRange("Downfall", downfall, Float.MIN_VALUE, Float.MAX_VALUE));
                builder.pop();
                builder.push("SpecialEffects");
                this.fogColor.put(id, builder.defineInRange("FogColor", fogColor, Integer.MIN_VALUE, Integer.MAX_VALUE));
                this.waterColor.put(id, builder.defineInRange("WaterColor", waterColor, Integer.MIN_VALUE, Integer.MAX_VALUE));
                this.waterFogColor.put(id, builder.defineInRange("WaterFogColor", waterFogColor, Integer.MIN_VALUE, Integer.MAX_VALUE));
                this.skyColor.put(id, builder.defineInRange("SkyColor", skyColor, Integer.MIN_VALUE, Integer.MAX_VALUE));
                foliageColorOverride.ifPresent(value -> this.foliageColorOverride.put(id, builder.defineInRange("FoliageColorOverride", value, Integer.MIN_VALUE, Integer.MAX_VALUE)));
                grassColorOverride.ifPresent(value -> this.grassColorOverride.put(id, builder.defineInRange("GrassColorOverride", value, Integer.MIN_VALUE, Integer.MAX_VALUE)));
                this.grassColorModifier.put(id, builder.defineEnum("GrassColorModifier", grassColorModifier));
                ambientParticleSettings.ifPresent(settings -> this.ambientParticleSettings.put(id, builder.define("AmbientParticleSettings", new AmbientParticleConfig(ForgeRegistries.PARTICLE_TYPES.getKey(settings.getOptions().getType()), ((AmbientParticleSettingsAccessor)settings).getProbability()))));
                ambientLoopSoundEvent.ifPresent(soundEventHolder -> this.ambientLoopSoundEvent.put(id, builder.define("AmbientLoopSoundEvent", soundEventHolder.value().getLocation())));
                ambientMoodSettings.ifPresent(mood -> this.ambientMoodSettings.put(id, builder.define("AmbientMoodSettings", new AmbientMoodConfig(mood.getSoundEvent().value().getLocation(), mood.getTickDelay(), mood.getBlockSearchExtent(), mood.getSoundPositionOffset()))));
                ambientAdditionsSettings.ifPresent(additions -> this.ambientAdditionsSettings.put(id, builder.define("AmbientAdditionsSettings", new AmbientAdditionsConfig(additions.getSoundEvent().value().getLocation(), additions.getTickChance()))));
                backgroundMusic.ifPresent(music -> this.backgroundMusic.put(id, builder.define("BackgroundMusic", new MusicConfig(music.getEvent().value().getLocation(), music.getMinDelay(), music.getMaxDelay(), music.replaceCurrentMusic()))));
                builder.pop();
                builder.pop();
            }
        });
        builder.pop();
        this.biomeConfig = builder.build();
    }
}
