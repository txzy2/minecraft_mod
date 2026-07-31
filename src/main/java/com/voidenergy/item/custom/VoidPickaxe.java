package com.voidenergy.item.custom;

import net.minecraft.world.item.PickaxeItem;

public class VoidPickaxe extends PickaxeItem {

    public static final int DURABILITY = 5120;
    public static final String ITEM_NAME = "void_pickaxe";

    public VoidPickaxe(Properties properties) {
        super(VoidPickaxeMaterial.INSTANCE, properties);
    }
}
