# Void Energy — AI Agent Context

## Project Overview

**Void Energy** is a Minecraft 1.21.1 Fabric mod that adds a **Cosmic Energy system** and magical items. The mod introduces a server-wide energy pool (0–100) persisted via Minecraft's `SavedData` mechanism, consumed and replenished through combat and item usage.

**Core features:**
- Cosmic Energy system (server-authoritative, NBT-persisted)
- Magic Stick (2-mode weapon/tool: FIRE and HEAL)
- Void Orb (placeholder), Void Rune (crafting ingredient)
- Energy HUD overlay (purple bar + text)
- Custom sparkle particle on mode switch

## Build System

- **Gradle 9.5.1** with **Fabric Loom 1.17-SNAPSHOT** plugin
- Java target: **21**
- Split environment source sets (`splitEnvironmentSourceSets()`)

```bash
# Build the mod JAR
./gradlew build

# Run game client (dev)
./gradlew runClient

# Run game server (dev)
./gradlew runServer

# Clean build
./gradlew clean build

# Build output
build/libs/voidenergy-1.0.0.jar
```

## Dependencies

| Dependency | Version |
|---|---|
| Minecraft | 1.21.1 |
| Fabric Loader | 0.19.3 |
| Fabric API | 0.116.14+1.21.1 |
| Fabric Loom | 1.17-SNAPSHOT |
| Java | 21 |
| Gradle | 9.5.1 |
| Mappings | Mojang official (not Yarn) |

Libraries used: SLF4J (logging), LWJGL/GLFW (key bindings), SpongePowered Mixin.

## Architecture

### Source Sets

```
src/main/   → Common code (server + client)
src/client/ → Client-only code (rendering, key bindings, HUD)
src/test/   → Tests (empty)
```

### Package Structure

```
com.voidenergy
├── ExampleMod.java                    # Entrypoint (ModInitializer) — registers items, packets, particles
├── constants/Constants.java           # Stack size constants
├── cosmic_energy/
│   ├── CosmicEnergyManager.java       # Singleton energy state (server delegates to SavedData, client holds local copy)
│   └── CosmicEnergySavedData.java     # Persistent NBT storage (SavedData pattern)
├── item/
│   ├── ModItems.java                  # Item registry (3 items in Creative tab)
│   └── custom/
│       ├── MagicStick.java            # Main item — 2 modes, combat, healing, energy generation
│       └── Void.java                  # Placeholder item
├── mixin/ExampleMixin.java            # Server-side mixin (MinecraftServer.loadLevel hook, currently empty)
├── network/ModeSwitchPacket.java      # C2S packet for mode cycling (TAB key)
└── util/PlayerHelper.java             # Negative effect extraction

com.voidenergy.client
├── ExampleModClient.java              # Particle factory registration
├── KeyBindings.java                   # TAB key → sends ModeSwitchPacket
├── ClientEvents.java                  # Empty placeholder
├── mixin/ExampleClientMixin.java      # Client mixin (Minecraft.run hook, currently empty)
└── cosmic_energy/
    ├── CosmicEnergyClient.java        # Registers HUD render callback
    └── client/EnergyHud.java          # Renders energy bar + text (top-left)
```

### Resources

```
src/main/resources/
├── fabric.mod.json                    # Mod descriptor
├── voidenergy.mixins.json             # Server mixin config
└── assets/voidenergy/
    ├── lang/{en_us,ru_ru}.json        # Localization (EN + RU)
    ├── models/item/{magic_stick,void,void_rune}.json   # Blockbench 3D models
    ├── textures/item/{magic_stick,void,void_rune}.png  # Item textures
    ├── particles/sparkle_particle.json
    └── data/voidenergy/recipe/{magic_stick,void_rune}.json  # Crafting recipes

src/client/resources/
└── voidenergy.client.mixins.json      # Client mixin config
```

## Data Flow

### Mode Switch (Client → Server)
```
[Client] Player presses TAB
  → KeyBindings detects MagicStick in hand
  → Sends ModeSwitchPacket (C2S) to server

[Server] ModeSwitchPacket handler
  → Calls MagicStick.cycleMode() on item stack
  → Sends sparkle particles to client
  → Sends chat message with new mode
```

### Combat → Energy Generation
```
[Server] Player attacks entity
  → MagicStick.hurtEnemy() runs
  → FIRE mode: 20 base damage + fire (5s) + slowness, generates 0.1–0.5 energy
  → HEAL mode: heals peaceful player 3 HP, generates 5 energy
```

### Energy HUD
```
[Client] EnergyHud.tick()
  → Reads energy from CosmicEnergyManager
  → Draws purple bar + "Cosmic energy: X/100" text (top-left)
```

### Persistence
```
[Server] CosmicEnergyManager
  → Reads/writes CosmicEnergySavedData (Overworld level storage, NBT)
  → Energy clamped to [0, 100]

[Client] Holds clientEnergy fallback for HUD when server ref unavailable
```

## Conventions

- **Mappings:** Mojang official (not Yarn) — this is the modern standard for Fabric 1.20.5+
- **Java 21:** Use modern features (switch expressions, records, pattern matching) where appropriate
- **No comments** in code unless explicitly requested
- **Item state:** Use NBT `CustomData` (not components) for MagicStick mode persistence
- **Energy bounds:** Always clamp to [0, 100] at both manager and saved data levels
- **Split source sets:** Client-only code goes in `src/client/`, common code in `src/main/`
- **Naming:** Follow Fabric/Minecraft conventions — `ModItems`, `ModBlocks` pattern for registries
- **Logging:** SLF4J via `org.slf4j.LoggerFactory`
- **Networking:** Fabric Networking API v1 (not the old packet system)

## Key Files to Know

| File | Role |
|---|---|
| `ExampleMod.java` | Mod entrypoint, all registration |
| `MagicStick.java` | Core item logic (combat, healing, modes) |
| `CosmicEnergyManager.java` | Energy singleton (server + client) |
| `CosmicEnergySavedData.java` | NBT persistence for energy |
| `ModeSwitchPacket.java` | Client→server mode switch networking |
| `KeyBindings.java` | TAB key binding (client) |
| `EnergyHud.java` | HUD overlay rendering |
| `build.gradle` | Build config, Fabric Loom setup |
| `gradle.properties` | Version pins, mod metadata |
| `fabric.mod.json` | Mod descriptor (entrypoints, mixins) |

## Pitfalls

- **Energy can go negative** if `generateEnergy()` / `consumeEnergy()` are called in wrong order — always clamp
- **MagicStick mode** is stored in NBT `CustomData`, not in a Fabric Component — check `getOrCreateCustomData()` pattern
- **SavedData** is per-world (Overworld) — energy is shared across all players on the server
- **Split source sets** mean `src/client/` code is NOT available in `src/main/` — avoid cross-references
- **Mixin configs** are separate: `voidenergy.mixins.json` (server) and `voidenergy.client.mixins.json` (client)
- **CI** builds with JDK 25 on Ubuntu 24.04 — Java 21 target is cross-compiled
