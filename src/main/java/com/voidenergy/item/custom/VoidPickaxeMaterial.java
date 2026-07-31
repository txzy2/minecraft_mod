package com.voidenergy.item.custom;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class VoidPickaxeMaterial implements Tier {
    public static final float SPEED = 100.5f;

    public static final VoidPickaxeMaterial INSTANCE = new VoidPickaxeMaterial();

    @Override
    public int getUses() {
        return VoidPickaxe.DURABILITY;
    }

    @Override
    public float getSpeed() {
        return SPEED;
    }

    @Override
    public float getAttackDamageBonus() {
        return 0;
    }

    @Override
    public int getEnchantmentValue() {
        return 22;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of();
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
    }
}
