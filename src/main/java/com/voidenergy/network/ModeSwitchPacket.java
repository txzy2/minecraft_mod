package com.voidenergy.network;

import com.voidenergy.ExampleMod;
import com.voidenergy.item.custom.MagicStick;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

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
        PayloadTypeRegistry.playC2S().register(TYPE, net.minecraft.network.codec.StreamCodec.unit(new ModeSwitchPayload()));

        ServerPlayNetworking.registerGlobalReceiver(TYPE, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayer player = context.player();
                if (player != null) {
                    var stack = player.getMainHandItem();
                    if (stack.getItem() instanceof MagicStick magicStick) {
                        magicStick.cycleMode(stack);
                    }
                }
            });
        });
    }
}
