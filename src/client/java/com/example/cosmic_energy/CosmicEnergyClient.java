package com.example.cosmic_energy;

import com.example.cosmic_energy.client.EnergyHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class CosmicEnergyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
            EnergyHud.render(guiGraphics);
        });
    }
}
