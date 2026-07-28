package com.voidenergy.cosmic_energy;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class CosmicEnergySavedData extends SavedData {

    private static final String DATA_NAME = "voidenergy_cosmic_energy";

    private float energy = 0;

    public CosmicEnergySavedData() {
    }

    public static CosmicEnergySavedData load(CompoundTag tag, HolderLookup.Provider provider) {
        CosmicEnergySavedData data = new CosmicEnergySavedData();
        data.energy = tag.getFloat("Energy");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putFloat("Energy", energy);
        return tag;
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
        setDirty();
    }

    public void addEnergy(float amount) {
        setEnergy(Math.max(0, Math.min(100, this.energy + amount)));
    }

    public static CosmicEnergySavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(CosmicEnergySavedData::new, CosmicEnergySavedData::load, null),
                DATA_NAME
        );
    }

    public static CosmicEnergySavedData get(MinecraftServer server) {
        return get(server.overworld());
    }
}
