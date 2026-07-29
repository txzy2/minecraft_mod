package com.voidenergy.client;

import com.voidenergy.ExampleMod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.EndRodParticle;

public class ExampleModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ParticleFactoryRegistry.getInstance().register(ExampleMod.SPARKLE_PARTICLE, EndRodParticle.Provider::new);
	}
}
