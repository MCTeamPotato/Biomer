package me.kall.biomer.config;

import com.google.gson.*;
import it.unimi.dsi.fastutil.objects.*;
import me.kall.biomer.config.records.AmbientAdditionsData;
import me.kall.biomer.config.records.AmbientMoodData;
import me.kall.biomer.config.records.AmbientParticleData;
import me.kall.biomer.config.records.MusicData;
import me.kall.biomer.mixin.AmbientParticleSettingsAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BiomeArgs {
    public static @Nullable BiomeArgs gameBiomeArgs;

    public final Object2BooleanMap<ResourceLocation> hasPrecipitation = new Object2BooleanOpenHashMap<>();
    public final Object2DoubleMap<ResourceLocation> temperature = new Object2DoubleOpenHashMap<>(), downfall = new Object2DoubleOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, Biome.TemperatureModifier> temperatureModifier = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, BiomeSpecialEffects.GrassColorModifier> grassColorModifier = new Object2ObjectOpenHashMap<>();
    public final Object2IntMap<ResourceLocation> fogColor = new Object2IntOpenHashMap<>(), waterColor = new Object2IntOpenHashMap<>(), waterFogColor = new Object2IntOpenHashMap<>(), skyColor = new Object2IntOpenHashMap<>(), foliageColorOverride = new Object2IntOpenHashMap<>(), grassColorOverride = new Object2IntOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, AmbientParticleData> ambientParticleSettings = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, ResourceLocation> ambientLoopSoundEvent = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, AmbientMoodData> ambientMoodSettings = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, AmbientAdditionsData> ambientAdditionsSettings = new Object2ObjectOpenHashMap<>();
    public final Object2ObjectMap<ResourceLocation, MusicData> backgroundMusic = new Object2ObjectOpenHashMap<>();

    private @Nullable String stringCache;

    private static int parseHexColor(@NotNull String hexColor) {
        return Integer.parseInt(hexColor.replace("0x", ""), 16);
    }

    public BiomeArgs() {}

    public BiomeArgs(@NotNull MinecraftServer server) {
        server.registryAccess().registry(Registries.BIOME).ifPresent(biomeRegistry -> {
            for (Map.Entry<ResourceKey<Biome>, Biome> entry : biomeRegistry.entrySet()) {
                Biome biome = entry.getValue();
                ResourceLocation id = entry.getKey().location();

                Biome.ClimateSettings climateSettings = biome.getModifiedClimateSettings();
                BiomeSpecialEffects specialEffects = biome.getModifiedSpecialEffects();

                this.hasPrecipitation.put(id, climateSettings.hasPrecipitation());
                this.temperature.put(id, climateSettings.temperature());
                this.temperatureModifier.put(id, climateSettings.temperatureModifier());
                this.downfall.put(id, climateSettings.downfall());

                this.fogColor.put(id, specialEffects.getFogColor());
                this.waterColor.put(id, specialEffects.getWaterColor());
                this.waterFogColor.put(id, specialEffects.getWaterFogColor());
                this.skyColor.put(id, specialEffects.getSkyColor());
                specialEffects.getFoliageColorOverride().ifPresent(value -> this.foliageColorOverride.put(id, value.intValue()));
                specialEffects.getGrassColorOverride().ifPresent(value -> this.grassColorOverride.put(id, value.intValue()));
                this.grassColorModifier.put(id, specialEffects.getGrassColorModifier());
                specialEffects.getAmbientParticleSettings().ifPresent(settings -> this.ambientParticleSettings.put(id, new AmbientParticleData(ForgeRegistries.PARTICLE_TYPES.getKey(settings.getOptions().getType()), ((AmbientParticleSettingsAccessor)settings).getProbability())));
                specialEffects.getAmbientLoopSoundEvent().ifPresent(soundEventHolder -> this.ambientLoopSoundEvent.put(id, soundEventHolder.value().getLocation()));
                specialEffects.getAmbientMoodSettings().ifPresent(mood -> this.ambientMoodSettings.put(id, new AmbientMoodData(mood.getSoundEvent().value().getLocation(), mood.getTickDelay(), mood.getBlockSearchExtent(), mood.getSoundPositionOffset())));
                specialEffects.getAmbientAdditionsSettings().ifPresent(additions -> this.ambientAdditionsSettings.put(id, new AmbientAdditionsData(additions.getSoundEvent().value().getLocation(), additions.getTickChance())));
                specialEffects.getBackgroundMusic().ifPresent(music -> this.backgroundMusic.put(id, new MusicData(music.getEvent().value().getLocation(), music.getMinDelay(), music.getMaxDelay(), music.replaceCurrentMusic())));
            }
        });
    }

    public static @NotNull BiomeArgs fromString(String json) {
        BiomeArgs biomeArgs = new BiomeArgs();
        for (JsonElement biomeElement : JsonParser.parseString(json).getAsJsonObject().getAsJsonArray("biomes")) {
            JsonObject biomeObj = biomeElement.getAsJsonObject();

            ResourceLocation biomeId = ResourceLocation.parse(biomeObj.get("id").getAsString());

            JsonObject climate = biomeObj.getAsJsonObject("climate");
            biomeArgs.hasPrecipitation.put(biomeId, climate.get("hasPrecipitation").getAsBoolean());
            biomeArgs.temperature.put(biomeId, climate.get("temperature").getAsDouble());
            biomeArgs.downfall.put(biomeId, climate.get("downfall").getAsDouble());
            biomeArgs.temperatureModifier.put(biomeId, Biome.TemperatureModifier.valueOf(climate.get("temperatureModifier").getAsString()));

            JsonObject colors = biomeObj.getAsJsonObject("colors");
            biomeArgs.fogColor.put(biomeId, parseHexColor(colors.get("fogColor").getAsString()));
            biomeArgs.waterColor.put(biomeId, parseHexColor(colors.get("waterColor").getAsString()));
            biomeArgs.waterFogColor.put(biomeId, parseHexColor(colors.get("waterFogColor").getAsString()));
            biomeArgs.skyColor.put(biomeId, parseHexColor(colors.get("skyColor").getAsString()));

            if (colors.has("foliageColorOverride")) biomeArgs.foliageColorOverride.put(biomeId, parseHexColor(colors.get("foliageColorOverride").getAsString()));
            if (colors.has("grassColorOverride")) biomeArgs.grassColorOverride.put(biomeId, parseHexColor(colors.get("grassColorOverride").getAsString()));

            biomeArgs.grassColorModifier.put(biomeId, BiomeSpecialEffects.GrassColorModifier.valueOf(colors.get("grassColorModifier").getAsString()));

            JsonObject ambientEffects = biomeObj.getAsJsonObject("ambientEffects");

            if (ambientEffects.has("particles")) {
                JsonObject particles = ambientEffects.getAsJsonObject("particles");
                biomeArgs.ambientParticleSettings.put(biomeId, new AmbientParticleData(ResourceLocation.parse(particles.get("type").getAsString()), particles.get("probability").getAsFloat()));
            }

            if (ambientEffects.has("loopSound")) {
                biomeArgs.ambientLoopSoundEvent.put(biomeId, ResourceLocation.parse(ambientEffects.get("loopSound").getAsString()));
            }

            if (ambientEffects.has("mood")) {
                JsonObject mood = ambientEffects.getAsJsonObject("mood");
                biomeArgs.ambientMoodSettings.put(biomeId, new AmbientMoodData(ResourceLocation.parse(mood.get("soundEvent").getAsString()), mood.get("tickDelay").getAsInt(), mood.get("blockSearchExtent").getAsInt(), mood.get("soundPositionOffset").getAsDouble()));
            }

            if (ambientEffects.has("additions")) {
                JsonObject additions = ambientEffects.getAsJsonObject("additions");
                biomeArgs.ambientAdditionsSettings.put(biomeId, new AmbientAdditionsData(ResourceLocation.parse(additions.get("soundEvent").getAsString()), additions.get("tickChance").getAsDouble()));
            }

            if (ambientEffects.has("music")) {
                JsonObject music = ambientEffects.getAsJsonObject("music");
                biomeArgs.backgroundMusic.put(biomeId, new MusicData(ResourceLocation.parse(music.get("soundEvent").getAsString()), music.get("minDelay").getAsInt(), music.get("maxDelay").getAsInt(), music.get("replaceCurrentMusic").getAsBoolean()));
            }
        }

        return biomeArgs;
    }

    @Override
    public String toString() {
        if (this.stringCache != null) return this.stringCache;

        JsonObject root = new JsonObject();
        JsonArray biomesArray = new JsonArray();

        List<ResourceLocation> biomes = new ObjectArrayList<>(this.hasPrecipitation.keySet());
        biomes.sort(Comparator.comparing(ResourceLocation::toString));

        for (ResourceLocation biomeId : biomes) {
            JsonObject biomeObj = new JsonObject();
            biomeObj.addProperty("id", biomeId.toString());

            JsonObject climate = new JsonObject();
            climate.addProperty("hasPrecipitation", this.hasPrecipitation.getBoolean(biomeId));
            climate.addProperty("temperature", this.temperature.getDouble(biomeId));
            climate.addProperty("downfall", this.downfall.getDouble(biomeId));
            climate.addProperty("temperatureModifier", this.temperatureModifier.get(biomeId).toString());
            biomeObj.add("climate", climate);

            JsonObject colors = new JsonObject();
            colors.addProperty("fogColor", String.format("0x%06X", this.fogColor.getInt(biomeId)));
            colors.addProperty("waterColor", String.format("0x%06X", this.waterColor.getInt(biomeId)));
            colors.addProperty("waterFogColor", String.format("0x%06X", this.waterFogColor.getInt(biomeId)));
            colors.addProperty("skyColor", String.format("0x%06X", this.skyColor.getInt(biomeId)));

            if (this.foliageColorOverride.containsKey(biomeId)) {
                colors.addProperty("foliageColorOverride", String.format("0x%06X", this.foliageColorOverride.getInt(biomeId)));
            }
            if (this.grassColorOverride.containsKey(biomeId)) {
                colors.addProperty("grassColorOverride", String.format("0x%06X", this.grassColorOverride.getInt(biomeId)));
            }

            colors.addProperty("grassColorModifier", this.grassColorModifier.get(biomeId).toString());
            biomeObj.add("colors", colors);

            JsonObject ambientEffects = new JsonObject();

            if (this.ambientParticleSettings.containsKey(biomeId)) {
                AmbientParticleData particleConfig = this.ambientParticleSettings.get(biomeId);
                JsonObject particles = new JsonObject();
                particles.addProperty("type", particleConfig.particleOptions().toString());
                particles.addProperty("probability", particleConfig.probability());
                ambientEffects.add("particles", particles);
            }

            if (this.ambientLoopSoundEvent.containsKey(biomeId)) {
                ambientEffects.addProperty("loopSound", this.ambientLoopSoundEvent.get(biomeId).toString());
            }

            if (this.ambientMoodSettings.containsKey(biomeId)) {
                AmbientMoodData moodConfig = this.ambientMoodSettings.get(biomeId);
                JsonObject mood = new JsonObject();
                mood.addProperty("soundEvent", moodConfig.soundEvent().toString());
                mood.addProperty("tickDelay", moodConfig.tickDelay());
                mood.addProperty("blockSearchExtent", moodConfig.blockSearchExtent());
                mood.addProperty("soundPositionOffset", moodConfig.soundPositionOffset());
                ambientEffects.add("mood", mood);
            }

            if (this.ambientAdditionsSettings.containsKey(biomeId)) {
                AmbientAdditionsData additionsConfig = this.ambientAdditionsSettings.get(biomeId);
                JsonObject additions = new JsonObject();
                additions.addProperty("soundEvent", additionsConfig.soundEvent().toString());
                additions.addProperty("tickChance", additionsConfig.tickChance());
                ambientEffects.add("additions", additions);
            }

            if (this.backgroundMusic.containsKey(biomeId)) {
                MusicData musicData = this.backgroundMusic.get(biomeId);
                JsonObject music = new JsonObject();
                music.addProperty("soundEvent", musicData.soundEvent().toString());
                music.addProperty("minDelay", musicData.minDelay());
                music.addProperty("maxDelay", musicData.maxDelay());
                music.addProperty("replaceCurrentMusic", musicData.replaceCurrentMusic());
                ambientEffects.add("music", music);
            }

            biomeObj.add("ambientEffects", ambientEffects);
            biomesArray.add(biomeObj);
        }

        root.add("biomes", biomesArray);

        this.stringCache = new GsonBuilder().setPrettyPrinting().create().toJson(root);
        return this.stringCache;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof BiomeArgs biomeArgs)) return false;

        return this.hasPrecipitation.equals(biomeArgs.hasPrecipitation) && this.temperature.equals(biomeArgs.temperature) && this.downfall.equals(biomeArgs.downfall) && this.temperatureModifier.equals(biomeArgs.temperatureModifier) && this.grassColorModifier.equals(biomeArgs.grassColorModifier) && this.fogColor.equals(biomeArgs.fogColor) && this.waterColor.equals(biomeArgs.waterColor) && this.waterFogColor.equals(biomeArgs.waterFogColor) && this.skyColor.equals(biomeArgs.skyColor) && this.foliageColorOverride.equals(biomeArgs.foliageColorOverride) && this.grassColorOverride.equals(biomeArgs.grassColorOverride) && this.ambientParticleSettings.equals(biomeArgs.ambientParticleSettings) && this.ambientLoopSoundEvent.equals(biomeArgs.ambientLoopSoundEvent) && this.ambientMoodSettings.equals(biomeArgs.ambientMoodSettings) && this.ambientAdditionsSettings.equals(biomeArgs.ambientAdditionsSettings) && this.backgroundMusic.equals(biomeArgs.backgroundMusic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.hasPrecipitation, this.temperature, this.downfall, this.temperatureModifier, this.grassColorModifier, this.fogColor, this.waterColor, this.waterFogColor, this.skyColor, this.foliageColorOverride, this.grassColorOverride, this.ambientParticleSettings, this.ambientLoopSoundEvent, this.ambientMoodSettings, this.ambientAdditionsSettings, this.backgroundMusic);
    }
}