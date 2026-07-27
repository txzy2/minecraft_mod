package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.item.ModItems;

import net.fabricmc.api.ModInitializer;

public class ExampleMod implements ModInitializer {
	public static final String MOD_ID = "modid";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ModItems.register();
		LOGGER.info("Мой предмет зарегистрирован!");
	}
}
