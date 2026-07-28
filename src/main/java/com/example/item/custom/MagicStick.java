package com.example.item.custom;

import com.example.cosmic_energy.CosmicEnergyManager;
import com.example.util.PlayerHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class MagicStick extends Item {

    public static final int DURABILITY = 256;
    public static final String ITEM_NAME = "magic_stick";
    public static final int FIRE_TICKS = 100;
    public static final Logger LOGGER = LoggerFactory.getLogger("Magic Stick");
    protected static float baseDamage = 20.0f;
    protected static float healAmount = 5.0f;

    private static final String MODE_KEY = "MagicStickMode";

    public MagicStick(Properties properties) {
        super(properties);
    }

    public static float getDamage() {
        return baseDamage;
    }

    public static Mode getMode(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null || !customData.contains(MODE_KEY)) {
            return Mode.FIRE;
        }

        try {
            return Mode.valueOf(customData.copyTag().getString(MODE_KEY));
        } catch (IllegalArgumentException e) {
            return Mode.FIRE;
        }
    }

    public static void setMode(ItemStack stack, Mode mode) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(MODE_KEY, mode.name()));
    }

    public void cycleMode(ItemStack stack) {
        Mode current = getMode(stack);
        Mode next = switch (current) {
            case FIRE -> Mode.HEAL;
            case HEAL -> Mode.FIRE;
        };
        setMode(stack, next);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (level.isClientSide()) {
            return InteractionResultHolder.pass(player.getItemInHand(interactionHand));
        }

        ItemStack stack = player.getItemInHand(interactionHand);
        Mode mode = getMode(stack);

        if (mode == Mode.HEAL) {
            if (player.getHealth() < player.getMaxHealth()) {
                player.heal(healAmount);
                player.playSound(SoundEvents.PLAYER_LEVELUP, 0.7f, 1.0f);

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ParticleTypes.HEART,
                            player.getX(),
                            player.getY() + 1.0,
                            player.getZ(),
                            8, 0.5, 0.5, 0.5, 0.0);
                }

                stack.setDamageValue(stack.getDamageValue() + 10);

                if (stack.getDamageValue() >= stack.getMaxDamage()) {
                    stack.shrink(1);
                }

                return InteractionResultHolder.success(stack);
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
        list.add(Component.translatable("item.modid.magic_stick.tooltip").withStyle(ChatFormatting.GRAY));
        list.add(Component.literal("Current Mode: " + getMode(itemStack).name()).withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return getMode(stack) == Mode.FIRE;
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
        if (effect == MobEffects.POISON.value() || effect == MobEffects.WITHER.value()) {
            return baseDamage * 0.5f;
        } else if (effect == MobEffects.WEAKNESS.value() || effect == MobEffects.MOVEMENT_SLOWDOWN.value()) {
            return baseDamage * 0.3f;
        }
        return baseDamage * 0.1f;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (stack.isEmpty())
            return false;

        Mode mode = getMode(stack);

        switch (mode) {
            case FIRE -> {
                target.setRemainingFireTicks(FIRE_TICKS);
                float finalDamage = baseDamage;

                if (attacker instanceof Player player) {
                    List<MobEffectInstance> negativeEffects = PlayerHelper.getNegativeEffects(player);

                    if (!negativeEffects.isEmpty()) {
                        float penaltyDamage = negativeEffects.stream()
                                .map((MobEffectInstance val) -> getPenaltyForEffect(val.getEffect().value()))
                                .reduce(0.0f, Float::sum);

                        finalDamage = Math.max(1.0f, baseDamage - penaltyDamage);
                    }
                }

                Random r = new Random();
                float randMana = 0.1f + r.nextFloat() * (0.5f - 0.1f);
                CosmicEnergyManager.getInstance().addEnergy(Math.round(randMana * 10.0f) / 10.0f);

                target.setHealth(target.getHealth() - finalDamage);
            }
            case HEAL -> {
                LOGGER.info("HEAL MODE (CURRENT DAMAGE 1)");
            }
        }

        stack.setDamageValue(stack.getDamageValue() + 1);
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            stack.shrink(1);
        }

        return true;
    }

    public enum Mode {
        FIRE,
        HEAL,
    }
}
