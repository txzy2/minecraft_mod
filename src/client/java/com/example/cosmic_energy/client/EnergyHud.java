package com.example.cosmic_energy.client;

import com.example.cosmic_energy.CosmicEnergyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

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

        // черный фон
        guiGraphics.fill(x, y, x + width, y + height, 0xFF000000);

        // заполнение энергии
        float progress = energy / maxEnergy;
        progress = Math.max(0, Math.min(1, progress));
        int filled = (int) (progress * width);

        guiGraphics.fill(x, y, x + filled, y + height, 0xFF00FFFF);

        String text = String.format("Energy: %.1f/%d", energy, (int) maxEnergy);
        guiGraphics.drawString(minecraft.font, text, x, y - 12, 0xFFFFFFFF);
    }
}
