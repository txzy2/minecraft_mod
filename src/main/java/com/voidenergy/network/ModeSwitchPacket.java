package com.voidenergy.network;

import com.voidenergy.ExampleMod;
import com.voidenergy.item.custom.MagicStick;
import com.voidenergy.item.custom.MagicStick.Mode;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class ModeSwitchPacket {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ExampleMod.MOD_ID, "mode_switch");
    public static final CustomPacketPayload.Type<ModeSwitchPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    public record ModeSwitchPayload() implements CustomPacketPayload {
        @Override
        public Type<ModeSwitchPayload> type() {
            return TYPE;
        }
    }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(TYPE,
                net.minecraft.network.codec.StreamCodec.unit(new ModeSwitchPayload()));

        ServerPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayer player = context.player();

                if (player != null) {
                    var stack = player.getMainHandItem();

                    if (stack.getItem() instanceof MagicStick magicStick) {
                        magicStick.cycleMode(stack);

                        ServerLevel world = player.serverLevel();
                        Vec3 pos = player.position().add(0, 1.5, 0);

                        world.sendParticles(player, ExampleMod.SPARKLE_PARTICLE, false, pos.x, pos.y, pos.z, 40, 1.5,
                                1.5, 1.5, 0.3);

                        Component text = Component.literal("Mode was changed -> " + MagicStick.getMode(stack).name())
                                .withColor((MagicStick.getMode(stack) == Mode.FIRE) ? 0xAA44FF : 0xFFFFE5B4);

                        player.sendSystemMessage(text);
                    }
                }
            });
        });
    }
}
