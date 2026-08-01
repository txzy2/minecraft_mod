package com.voidenergy.item.custom;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;

public class VoidPickaxe extends PickaxeItem {

    public static final int DURABILITY = 5120;
    public static final String ITEM_NAME = "void_pickaxe";

    public VoidPickaxe(Properties properties) {
        super(VoidPickaxeMaterial.INSTANCE, properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list,
            TooltipFlag tooltipFlag) {

        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);

        list.add(Component.literal("Void pickaxe").withStyle(ChatFormatting.DARK_PURPLE));
        list.add(Component.literal("123").withStyle(ChatFormatting.GRAY));

    }
}
