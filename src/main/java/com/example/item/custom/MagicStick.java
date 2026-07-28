package com.example.item.custom;

import com.example.cosmic_energy.CosmicEnergyComponent;
import com.example.cosmic_energy.CosmicEnergyManager;
import com.example.util.PlayerHelper;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagicStick extends Item {

    public static final int DURABILITY = 256;
    public static final String ITEM_NAME = "magic_stick";

    protected static float baseDamage = 20.0f;
    public static final int FIRE_TICKS = 100;

    private enum Mode {
        FIRE,
        HEAL,
    }

    private Mode currentMode = Mode.FIRE;

    public static final Logger LOGGER = LoggerFactory.getLogger("Magic Stick");

    public MagicStick(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
        Level level,
        Player player,
        InteractionHand interactionHand
    ) {
        if (level.isClientSide()) {
            return InteractionResultHolder.pass(
                player.getItemInHand(interactionHand)
            );
        }

        ItemStack stack = player.getItemInHand(interactionHand);

        if (currentMode == Mode.HEAL) {
            float healAmount = 4.0f;

            if (player.getHealth() < player.getMaxHealth()) {
                player.heal(healAmount);
                player.playSound(SoundEvents.PLAYER_LEVELUP, 0.7f, 1.0f);

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                        ParticleTypes.HEART,
                        player.getX(),
                        player.getY() + 1.0,
                        player.getZ(),
                        8,
                        0.5,
                        0.5,
                        0.5,
                        0.0
                    );
                }

                stack.setDamageValue(stack.getDamageValue() + 1);

                if (stack.getDamageValue() >= stack.getMaxDamage()) {
                    stack.shrink(1);
                }

                return InteractionResultHolder.success(stack);
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    public void cycleMode() {
        switch (currentMode) {
            case FIRE -> currentMode = Mode.HEAL;
            case HEAL -> currentMode = Mode.FIRE;
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide()) {
            return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    private float getPenaltyForEffect(MobEffect effect) {
        if (
            effect == MobEffects.POISON.value() ||
            effect == MobEffects.WITHER.value()
        ) {
            return baseDamage * 0.5f; // 50% от урона
        } else if (effect == MobEffects.WEAKNESS.value()) {
            return baseDamage * 0.3f; // 30% от урона
        }
        return baseDamage * 0.1f; // 10% от урона (для любых других эффектов)
    }

    @Override
    public boolean hurtEnemy(
        ItemStack stack,
        LivingEntity target,
        LivingEntity attacker
    ) {
        if (stack.isEmpty()) return false;

        switch (currentMode) {
            case FIRE -> {
                target.setRemainingFireTicks(FIRE_TICKS);

                float finalDamage = baseDamage;

                if (attacker instanceof Player player) {
                    List<MobEffectInstance> negativeEffects =
                        PlayerHelper.getNegativeEffects(player);

                    if (!negativeEffects.isEmpty()) {
                        float penaltyDamage = negativeEffects
                            .stream()
                            .map((MobEffectInstance val) -> getPenaltyForEffect(val.getEffect().value()))
                            .reduce(0.0f, Float::sum);

                        finalDamage = Math.max(1.0f, baseDamage - penaltyDamage);
                    }
                }

                if (attacker instanceof Player player) {
                    // CosmicEnergyComponent energy = ModComponents.ENERGY.get(
                    //     player
                    // );

                    CosmicEnergyManager.getInstance().addEnergy(0.5f);
                }

                target.setHealth(target.getHealth() - finalDamage);
            }
            case HEAL -> LOGGER.info("HEAL MODE (CURRENT DAMAGE 2)");
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

    public Mode getCurrentMode() {
        return currentMode;
    }
}
