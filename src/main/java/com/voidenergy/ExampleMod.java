package com.voidenergy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.voidenergy.item.ModItems;
import com.voidenergy.network.ModeSwitchPacket;

import net.fabricmc.api.ModInitializer;

public class ExampleMod implements ModInitializer {
	public static final String MOD_ID = "voidenergy";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ModItems.register();
		ModeSwitchPacket.register();
		LOGGER.info("Мой предмет зарегистрирован!");
	}
}
