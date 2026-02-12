package me.kall.biomer.config.records;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public record AmbientParticleData(ResourceLocation particleOptions, float probability) {
    public ParticleType<?> getParticle() {
        return ForgeRegistries.PARTICLE_TYPES.getValue(this.particleOptions);
    }
}
