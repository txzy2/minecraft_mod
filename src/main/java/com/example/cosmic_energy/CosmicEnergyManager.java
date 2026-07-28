package com.example.cosmic_energy;

public class CosmicEnergyManager {

    private static final CosmicEnergyManager INSTANCE =
        new CosmicEnergyManager();

    private float energy = 0;
    private final float maxEnergy = 100;

    private CosmicEnergyManager() {}

    public static CosmicEnergyManager getInstance() {
        return INSTANCE;
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = Math.max(0, Math.min(maxEnergy, energy));
    }

    public void addEnergy(float amount) {
        setEnergy(energy + amount);
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }
}
