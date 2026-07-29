package com.voidenergy.item;

import com.voidenergy.ExampleMod;
import com.voidenergy.item.custom.MagicStick;
import com.voidenergy.item.custom.Void;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModItems {
    // Регистрируем предмет
    public static final Item MAGIC_STICK = new MagicStick(
            new Item.Properties().durability(MagicStick.DURABILITY));

    public static final Item VOID = new Void(
            new Item.Properties().durability(256));

    // Метод register() для вызова из ExampleMod
    public static void register() {
        Registry.register(
                BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, MagicStick.ITEM_NAME),
                MAGIC_STICK);

        Registry.register(
                BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, "void"),
                VOID);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(new ItemStack(MAGIC_STICK));
            entries.accept(new ItemStack(VOID));
        });
    }

    // Альтернативный метод init()
    public static void init() {
        register();
    }
}
