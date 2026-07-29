package com.voidenergy.cosmic_energy.client;

import com.voidenergy.cosmic_energy.CosmicEnergyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class EnergyHud {

    public static void render(GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return;
        }

        float energy = CosmicEnergyManager.getInstance().getEnergy();
        float maxEnergy = CosmicEnergyManager.getInstance().getMaxEnergy();

        int x = 20;
        int y = 20;
        int width = 100;
        int height = 10;

        guiGraphics.fill(x, y, x + width, y + height, 0xFF000000);

        float progress = energy / maxEnergy;
        progress = Math.max(0, Math.min(1, progress));
        int filled = (int) (progress * width);

        guiGraphics.fill(x, y, x + filled, y + height, 0xFF00FFFF);

        Component text = Component.translatable("hud.voidenergy.cosmic_energy", String.format("%.1f", energy), (int) maxEnergy);

        float scale = 0.7f;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1.0f);
        guiGraphics.drawString(minecraft.font, text, (int) (x / scale), (int) ((y - 12) / scale), 0xFFFFFFFF);
        guiGraphics.pose().popPose();
    }
}
