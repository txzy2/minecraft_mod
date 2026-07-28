package com.example.item;

import com.example.ExampleMod;
import com.example.item.custom.MagicStick;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItems {
    // Регистрируем предмет
    public static final Item MAGIC_STICK = new MagicStick(
            new Item.Properties().durability(MagicStick.DURABILITY));

    // Метод register() для вызова из ExampleMod
    public static void register() {
        Registry.register(
                BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, MagicStick.ITEM_NAME),
                MAGIC_STICK);
    }

    // Альтернативный метод init()
    public static void init() {
        register();
    }
}
