package com.voidenergy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.voidenergy.cosmic_energy.CosmicEnergyManager;
import com.voidenergy.item.ModItems;
import com.voidenergy.network.ModeSwitchPacket;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ExampleMod implements ModInitializer {
	public static final String MOD_ID = "voidenergy";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final SimpleParticleType SPARKLE_PARTICLE = FabricParticleTypes.simple();

	@Override
	public void onInitialize() {
		CosmicEnergyManager.getInstance();

		ModItems.register();
		ModeSwitchPacket.register();

		Registry.register(BuiltInRegistries.PARTICLE_TYPE,
				ResourceLocation.fromNamespaceAndPath(MOD_ID, "sparkle_particle"), SPARKLE_PARTICLE);
	}
}
