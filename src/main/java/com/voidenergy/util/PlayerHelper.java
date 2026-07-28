package com.voidenergy.util;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class PlayerHelper {
    public static List<MobEffectInstance> getNegativeEffects(Player player) {
        return player.getActiveEffects().stream()
                .filter(effectInstance -> effectInstance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL)
                .collect(Collectors.toList());
    }
}
