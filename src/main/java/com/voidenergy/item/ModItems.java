package com.voidenergy.item;

import com.voidenergy.ExampleMod;
import com.voidenergy.item.custom.MagicStick;
import com.voidenergy.item.custom.Void;
import com.voidenergy.item.custom.VoidPickaxe;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModItems {
    public static final Item MAGIC_STICK = new MagicStick(new Item.Properties().durability(MagicStick.DURABILITY));
    public static final Item VOID = new Void(new Item.Properties().durability(256));
    public static final Item VOID_RUNE = new Item(new Item.Properties());
    public static final Item VOID_PICKAXE = new VoidPickaxe(new Item.Properties().durability(VoidPickaxe.DURABILITY));

    public static void register() {
        Registry.register(
                BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, MagicStick.ITEM_NAME),
                MAGIC_STICK);

        Registry.register(
                BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, "void"),
                VOID);

        Registry.register(
                BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, "void_rune"),
                VOID_RUNE);

        Registry.register(
                BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, VoidPickaxe.ITEM_NAME),
                VOID_PICKAXE);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(new ItemStack(MAGIC_STICK));
            entries.accept(new ItemStack(VOID));
            entries.accept(new ItemStack(VOID_RUNE));
            entries.accept(new ItemStack(VOID_PICKAXE));
        });
    }

    public static void init() {
        register();
    }
}
