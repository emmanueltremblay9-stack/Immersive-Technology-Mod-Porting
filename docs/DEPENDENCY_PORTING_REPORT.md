# Dependency Porting Report

## Immersive Convergence

The 1.21.1 NeoForge build does not declare or resolve an Immersive Convergence artifact. The old Forge-era API surface used by this project is small:

- `HeatCapabilities.MAX_HEAT`
- heat provider and consumer block capabilities
- `MechanicalCapabilities.MAX_RPM`
- mechanical provider and consumer block capabilities
- provider/consumer interfaces for heat and rotational systems

The unavailable API was replaced with `mctmods.immersivetechnology.api.convergence`. A temporary inspection of the GPLv3 Immersive Convergence 1.20.1 CurseMaven artifact (`curse.maven:immersive-convergence-1465236:7642971`) showed the default constants:

- `max_heat = 2000.0`
- `max_rpm = 7200`

The local replacement keeps those defaults and exposes NeoForge `BlockCapability` instances so the systems can be registered through NeoForge's 1.21 capability API.
