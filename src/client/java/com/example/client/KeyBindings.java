package com.example.client;

import org.lwjgl.glfw.GLFW;

import com.example.item.custom.MagicStick;
import com.example.network.ModeSwitchPacket;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class KeyBindings implements ClientModInitializer {
    public static KeyMapping modeSwitchKey;

    @Override
    public void onInitializeClient() {
        // 1. Регистрируем клавишу TAB
        modeSwitchKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.modid.mode_switch", // Ключ для локализации
            GLFW.GLFW_KEY_TAB,       // Код клавиши (Tab)
            "key.category.modid"     // Категория (для настроек)
        ));

        // 2. Добавляем обработчик нажатия
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Проверяем, что игрок есть, и клавиша была нажата (только один раз)
            while (modeSwitchKey.consumeClick() && client.player != null) {
                ItemStack mainHand = client.player.getMainHandItem();

                // Проверяем, что в руке наша палочка
                if (mainHand.getItem() instanceof MagicStick magicStick) {
                    magicStick.cycleMode();
                    // Отправляем запрос на сервер
                    ModeSwitchPacket.send();
                } else {
                    client.player.sendSystemMessage(Component.literal("Держите палочку в руке!"));
                }
            }
        });
    }
}
