package com.example.item.custom;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class MagicStick extends Item {
    public static final int DURABILITY = 256;
    public static final String ITEM_NAME = "magic_stick";

    protected static float baseDamage = 20.0f;
    public static final int FIRE_TICKS = 100;

    private enum Mode {
        FIRE, HEAL
    }

    private Mode currentMode = Mode.FIRE;

    public static final Logger LOGGER = LoggerFactory.getLogger("Magic Stick");

    public MagicStick(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (level.isClientSide()) {
            return InteractionResultHolder.pass(player.getItemInHand(interactionHand));
        }

        currentMode = switch (currentMode) {
            case FIRE -> Mode.HEAL;
            case HEAL -> Mode.FIRE;
            default -> Mode.FIRE;
        };

        player.sendSystemMessage(Component.literal("Режим: " + currentMode));

        return super.use(level, player, interactionHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide()) {
            return InteractionResult.PASS;
        }

        ItemStack stack = context.getItemInHand();

        if (currentMode == Mode.HEAL) {
            int currentDamage = stack.getDamageValue();
            stack.setDamageValue(currentDamage + 1);

            if (stack.getDamageValue() >= stack.getMaxDamage()) {
                stack.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private List<MobEffectInstance> getNegativeEffects(Player player) {
        return player.getActiveEffects().stream()
                .filter(effectInstance -> effectInstance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL)
                .collect(Collectors.toList());
    }

    private float getPenaltyForEffect(MobEffect effect) {
        if (effect == MobEffects.POISON.value() || effect == MobEffects.WITHER.value()) {
            return baseDamage * 0.5f; // 50% от урона
        } else if (effect == MobEffects.WEAKNESS.value()) {
            return baseDamage * 0.3f; // 30% от урона
        }
        return baseDamage * 0.1f; // 10% от урона (для любых других эффектов)
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (stack.isEmpty())
            return false;

        switch (currentMode) {
            case FIRE -> {
                target.setRemainingFireTicks(FIRE_TICKS);

                float finalDamage = baseDamage;

                if (attacker instanceof Player player) {
                    List<MobEffectInstance> negativeEffects = getNegativeEffects(player);

                    if (!negativeEffects.isEmpty()) {
                        float penaltyDamage = negativeEffects.stream()
                                .map((MobEffectInstance val) -> getPenaltyForEffect(val.getEffect().value()))
                                .reduce(0.0f, Float::sum);

                        finalDamage = Math.max(1.0f, baseDamage - penaltyDamage);
                    }
                }

                target.setHealth(target.getHealth() - finalDamage);
            }
            case HEAL -> LOGGER.info("HEAL MODE (CURRENT DAMAGE 1)");
        }

        stack.setDamageValue(stack.getDamageValue() + 1);
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            stack.shrink(1);
        }

        return true;
    }

    public static float getDamage() {
        return baseDamage;
    }
}
