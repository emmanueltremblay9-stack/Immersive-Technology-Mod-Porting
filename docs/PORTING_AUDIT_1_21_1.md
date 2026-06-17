# Porting Audit 1.21.1

Date: 2026-06-16
Branch: `port/1.21.1-neoforge`
Base source: `tgstyle/MCT-Immersive-Technology`, branch `1.21.1-3.0-Dev`, commit `999cf213`

## Initial State

This workspace was empty except for `.git` before the upstream source import. The working branch now tracks `upstream/1.21.1-3.0-Dev`.

Root layout:

- `.github/`
- `gradle/`
- `mb_shapes/`
- `src/`
- `build.gradle`
- `gradle.properties`
- `settings.gradle`
- `version.properties`
- `README.md`
- `LICENSE`

Source/resource counts at audit time:

- Main Java files: 299
- Datagen Java files: 21
- Main resource files: 212
- Generated resource files: 254

The upstream branch is already a partial Minecraft 1.21.1 NeoForge attempt. `./gradlew.bat tasks` passes, which confirms that the Gradle wrapper, NeoGradle plugin, NeoForge run types, and configured dependency repositories can initialize.

`./gradlew.bat clean compileJava` did not reach Java compilation because `clean` failed to delete Gradle configuration-cache report files under `build/reports/configuration-cache`. This appears to be a Windows/OneDrive file lock rather than a source issue.

`./gradlew.bat --no-configuration-cache compileJava` reaches Java compilation and fails on real porting issues:

- Remaining `net.minecraftforge.*` imports.
- Legacy Forge capability API usage (`Capability`, `ForgeCapabilities`, `LazyOptional`).
- Legacy Forge networking (`SimpleChannel`, `NetworkHooks`, `NetworkEvent`, `NetworkDirection`).
- Missing `com.immersiveconvergence.*` heat and mechanical API classes.
- Immersive Engineering API drift, including missing `blusunrize.immersiveengineering.api.utils.CapabilityReference` in the resolved IE 1.21.1 artifact.

## Build Metadata

Detected values:

- Mod id: `immersivetechnology`
- Java toolchain: 21
- Minecraft: `1.21.1`
- NeoForge: `21.1.208`
- Parchment: `2024.11.17`
- JEI: `19.27.0.339`
- Immersive Engineering version property: `1.21.1-12.4.2-194`
- Immersive Engineering resolved dependency: `curse.maven:immersive-engineering-231951:6733669`
- Jade resolved dependency: `curse.maven:jade-324717:7545219`

Gradle plugins:

- `java-library`
- `maven-publish`
- `net.neoforged.moddev` `2.0.140`
- `idea`
- `org.jetbrains.gradle.plugin.idea-ext` `1.1.6`
- `eclipse`

Run configurations:

- `runClient`
- `runServer`
- `runData`
- `runGameTestServer`

Source sets:

- `main`
- `datagen`
- `gametest`

Resources include both `src/main/resources` and `src/generated/resources`.

## License And Metadata Issues

The repository contains GPLv3 text in `LICENSE`, and `README.md` says the project is licensed under GNU GPL Version 3. However:

- `gradle.properties` currently has `mod_license=LGPL-3.0`.
- `src/main/resources/META-INF/mods.toml` currently has `license="LGPL-3.0"`.

This conflicts with the project prompt and source license requirement. The port must use GPLv3 compatible metadata, preferably `GPL-3.0-only` to match the prompt.

Additional metadata problems:

- `mods.toml` still declares dependency `modId="forge"` with Forge loader range `[47.1.106,)`.
- `mods.toml` still declares mandatory `immersiveconvergence` dependency using `${version_ic}`, but `version_ic` is not defined in `version.properties`.
- `mods.toml` description still says "for 1.20.1".
- Mixin config uses `compatibilityLevel: "JAVA_17"` despite the Java 21 target.
- Access transformer references `net.minecraftforge.client.model.generators.BlockStateProvider`, which should be migrated or removed if obsolete under NeoForge datagen.

## Dependencies

Required/current:

- NeoForge 1.21.1
- Immersive Engineering 1.21.1 NeoForge
- JEI 1.21.1 API/runtime
- Jade runtime

Problematic or unavailable:

- Immersive Convergence 1.21.1 NeoForge is referenced in code and metadata but is not present in the configured Gradle dependencies.
- Compile output shows all `com.immersiveconvergence.*` imports unresolved.
- The existing `immersiveconvergence` `mods.toml` dependency is currently mandatory, so the mod cannot load without either a valid dependency or an internal compatibility replacement.

## Major Systems

Core entry and lifecycle:

- `ImmersiveTechnology`
- `CommonProxy`
- `ClientProxy`
- `ITClientConfig`
- `ITCommonConfig`
- `ITServerConfig`
- `ITPacketHandler`

Registries:

- `ITBlocks`
- `ITItems`
- `ITFluids`
- `ITSounds`
- `ITParticles`
- `ITBlockEntities`
- `ITMenuTypes`
- `ITRecipeTypes`
- `ITCreativeTab`
- `ITMultiblockProvider`

Major multiblocks registered in code:

- Alternator
- Boiler Liquid
- Boiler Solid
- Boiler Tank
- Cooling Tower
- Distiller
- Gas Turbine
- Heat Exchanger
- Solar Melter
- Solar Reflector
- Solar Tower
- Steam Turbine
- Steel Sheetmetal Tank

Structure files present:

- `alternator`
- `boiler_liquid`
- `boiler_solid`
- `boiler_tank`
- `cooling_tower`
- `distiller`
- `gas_turbine`
- `heat_exchanger`
- `radiator`
- `solar_melter`
- `solar_reflector`
- `solar_tower`
- `steam_turbine`
- `steel_sheetmetal_tank`

Notable mismatch:

- `radiator.nbt` exists, but no active `Radiator` multiblock registration was found in `ITMultiblockProvider`.

Manual/documentation assets are present for the active registered multiblocks, but no radiator manual page was found.

## Incomplete Or Risk Signals

Search terms from the prompt found no `TODO` or `FIXME` hits. The meaningful hits are:

- `ITServerConfig` documents `TEMPLATE_BLOCKS` as placeholder-block disassembly behavior.
- `SolarReflectorLogic` has a client config path to disable reflector dance animation.
- `SteelSheetmetalTankLogic` initializes redstone state as disabled by default.

The bigger incompleteness signals are structural:

- 187 files still contain `net.minecraftforge` references.
- 11 files still contain `com.immersiveconvergence` references.
- 14 files contain legacy Forge networking patterns.
- Capability and networking systems need real 1.21.1 NeoForge API migration rather than only import rewriting.
- Recipes still use legacy serializer shapes in several classes and may need Codec/StreamCodec migration after the namespace pass.

## Porting Risks

1. Capability migration risk: NeoForge 1.21.1 no longer uses classic `LazyOptional` capability overrides in the same way. Block entities and IE multiblock logic need a coordinated migration to the NeoForge capability registration model.
2. Immersive Engineering API drift: the resolved IE artifact does not expose the old `api.utils.CapabilityReference` path used by the source.
3. Immersive Convergence dependency gap: heat and mechanical APIs are central to boilers, turbines, creative heat, and rotor behavior. A GPL-compatible internal compatibility layer may be required if no 1.21.1 NeoForge dependency exists.
4. Networking risk: `SimpleChannel` and `NetworkHooks.openScreen` usages need migration to modern NeoForge payload/menu opening APIs.
5. Metadata risk: current mod metadata still declares Forge and mandatory Immersive Convergence.
6. License risk: Gradle/mod metadata currently says LGPL even though repository license and prompt require GPLv3.
7. Datagen risk: datagen still imports Forge model/data helpers and access transformers reference Forge datagen classes.
8. Runtime risk: even after compile passes, multiblock formation, disassembly, fluid/energy I/O, GUI sync, and generated data need dedicated manual QA.

## Execution Plan

1. Normalize project metadata:
   - Set license metadata to `GPL-3.0-only`.
   - Replace Forge loader dependency with NeoForge metadata.
   - Remove or soften mandatory Immersive Convergence metadata until a valid 1.21.1 dependency exists.
   - Update description and Java mixin compatibility.

2. Mechanical NeoForge namespace pass:
   - Move event bus annotations and events to NeoForge package names.
   - Move registries from `RegistryObject` to `DeferredHolder` where needed.
   - Move fluids, item handlers, energy, tags, datagen, model helpers, and client event classes to their NeoForge package names.

3. Compile and triage:
   - Run `./gradlew.bat --no-configuration-cache compileJava`.
   - Fix the next smallest class of compile errors.
   - Keep notes in dependency and known-issues docs.

4. Immersive Convergence isolation:
   - Identify every heat/mechanical API use.
   - Decide whether to implement a small GPL-compatible internal layer or gate affected systems behind a clear config/documented limitation.

5. Capability and networking migration:
   - Replace classic `getCapability`/`LazyOptional` block entity patterns with NeoForge capability registration.
   - Replace legacy packet channel and menu-opening calls with NeoForge 1.21.1 APIs.

6. Datagen and asset audit:
   - Repair datagen source imports and providers.
   - Generate or verify blockstates, models, recipes, loot, tags, lang, and manual data.
   - Produce `docs/CONTENT_COMPLETION_MATRIX.md`.

7. QA and final reports:
   - Run `runData`, `test`, `build`, and `runClient` where possible.
   - Produce `docs/DEPENDENCY_PORTING_REPORT.md`, `docs/MANUAL_QA_CHECKLIST.md`, `docs/LICENSE_COMPLIANCE.md`, `docs/PORTING_REPORT_1_21_1.md`, and `KNOWN_ISSUES.md` as needed.

## Commands Run

- `git ls-remote --heads https://github.com/tgstyle/MCT-Immersive-Technology.git`
- `git fetch upstream 1.21.1-3.0-Dev 1.20.1-2.0-Dev`
- `git switch -c port/1.21.1-neoforge upstream/1.21.1-3.0-Dev`
- `./gradlew.bat tasks` - PASS
- `./gradlew.bat clean compileJava` - FAIL at `clean` due to Windows/OneDrive file lock in `build/reports/configuration-cache`
- `./gradlew.bat --no-configuration-cache compileJava` - FAIL at Java compilation due to Forge/API migration issues
