package com.voidenergy.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private static void onDropResources(BlockState state, Level level, BlockPos pos, BlockEntity blockEntity,
            net.minecraft.world.entity.Entity entity, ItemStack tool, CallbackInfo ci) {
        // Проверяем, что это игрок и это сервер
        if (!(entity instanceof Player player))
            return;
        if (level.isClientSide())
            return;

        // Получаем дроп с помощью статического метода getDrops
        List<ItemStack> drops = Block.getDrops(state, (ServerLevel) level, pos, blockEntity, entity, tool);

        for (ItemStack drop : drops) {
            if (!drop.isEmpty()) {
                if (!player.getInventory().add(drop)) {
                    player.drop(drop, false);
                }
            }
        }

        ci.cancel();
    }
}