package com.voidenergy.client;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.voidenergy.item.custom.MagicStick;
import com.voidenergy.network.ModeSwitchPacket;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.item.ItemStack;

public class KeyBindings implements ClientModInitializer {
    public static KeyMapping modeSwitchKey;
    public static final Logger LOGGER = LoggerFactory.getLogger("KEYS");

    @Override
    public void onInitializeClient() {
        // 1. Регистрируем клавишу TAB
        modeSwitchKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.voidenergy.mode_switch",
            GLFW.GLFW_KEY_TAB,
            "key.category.voidenergy"
        ));

        // 2. Добавляем обработчик нажатия
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Проверяем, что игрок есть, и клавиша была нажата (только один раз)
            while (modeSwitchKey.consumeClick() && client.player != null) {
                ItemStack mainHand = client.player.getMainHandItem();

                LOGGER.info("HAND: {}", mainHand.getItem());

                // Проверяем, что в руке наша палочка
                if (mainHand.getItem() instanceof MagicStick magicStick) {
                    magicStick.cycleMode(mainHand);
                    ClientPlayNetworking.send(new ModeSwitchPacket.ModeSwitchPayload());
                }
            }
        });
    }
}
