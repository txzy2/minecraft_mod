package com.voidenergy.cosmic_energy;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class CosmicEnergyManager {

    private static final CosmicEnergyManager INSTANCE = new CosmicEnergyManager();

    private volatile MinecraftServer server;
    private volatile float clientEnergy = 0;

    private CosmicEnergyManager() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            CosmicEnergySavedData data = CosmicEnergySavedData.get(server);
            if (data != null) {
                clientEnergy = data.getEnergy();
            }
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            CosmicEnergySavedData data = getSavedData();
            if (data != null) {
                clientEnergy = data.getEnergy();
            }
            this.server = null;
        });
    }

    public static CosmicEnergyManager getInstance() {
        return INSTANCE;
    }

    private CosmicEnergySavedData getSavedData() {
        if (server == null) {
            return null;
        }
        return CosmicEnergySavedData.get(server);
    }

    public float getEnergy() {
        CosmicEnergySavedData data = getSavedData();
        if (data != null) {
            return data.getEnergy();
        }
        return clientEnergy;
    }

    public void setEnergy(float energy) {
        CosmicEnergySavedData data = getSavedData();
        if (data != null) {
            data.setEnergy(energy);
        } else {
            clientEnergy = energy;
        }
    }

    public void addEnergy(float amount) {
        CosmicEnergySavedData data = getSavedData();
        if (data != null) {
            data.addEnergy(amount);
        } else {
            clientEnergy = Math.max(0, Math.min(100, clientEnergy + amount));
        }
    }

    public void setClientEnergy(float energy) {
        this.clientEnergy = energy;
    }

    public float getMaxEnergy() {
        return CosmicEnergySavedData.getMaxEnergy();
    }
}
