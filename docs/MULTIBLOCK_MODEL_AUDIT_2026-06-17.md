# Multiblock And Model Audit - 2026-06-17

## Scope

This audit checks the current `port/1.21.1-neoforge` branch for multiblock and model coverage.

- Current audit commit before this report: `77a4f3f5`
- Current same-version baseline: `upstream/1.21.1-3.0-Dev`
- Older comparison baseline: `upstream/1.20.1-2.0-Dev`

The checks covered:

- active `ITMultiblockProvider.registerMB(...)` entries
- Java multiblock classes and logic classes
- `assets/immersivetechnology/multiblocks/*.json`
- `data/immersivetechnology/structures/multiblocks/*.nbt`
- manual JSON and English manual text pages
- generated blockstates, item models, dynamic models, and split models
- OBJ/MTL model files and texture references

## Active 1.21.1 Multiblocks

The current branch registers 13 active multiblocks:

1. `alternator`
2. `boiler_liquid`
3. `boiler_solid`
4. `boiler_tank`
5. `cooling_tower`
6. `distiller`
7. `gas_turbine`
8. `heat_exchanger`
9. `solar_melter`
10. `solar_reflector`
11. `solar_tower`
12. `steam_turbine`
13. `steel_sheetmetal_tank`

## Coverage Result

For the 13 active multiblocks, coverage is complete across the checked surfaces.

| Surface | Result |
| --- | ---: |
| Java multiblock class | 13 / 13 |
| Java logic class | 13 / 13 |
| Multiblock asset JSON | 13 / 13 |
| Structure NBT | 13 / 13 |
| Manual JSON | 13 / 13 |
| English manual text | 13 / 13 |
| Generated blockstate | 13 / 13 |
| Generated item model | 13 / 13 |
| Generated dynamic model | 13 / 13 |
| Generated split model | 13 / 13 |
| OBJ model folder | 13 / 13 |

## Model Reference Validation

No broken model reference was found.

| Check | Result |
| --- | ---: |
| JSON parse errors | 0 |
| Missing model refs | 0 |
| Missing OBJ refs | 0 |
| Missing JSON texture refs | 0 |
| Missing MTL files | 0 |
| Missing MTL texture refs | 0 |

Notes:

- `cooling_tower` does not use a single `textures/multiblock/stone/cooling_tower.png` sheet. It uses separate `cooling_tower_top`, `cooling_tower_bottom`, and `cooling_tower_connections` textures. Model texture reference validation found no missing cooling tower texture.
- Extra model assets such as `rotor`, `rotor_east_west`, `solar_reflector_support`, and `solar_reflector_mirror` are support models, not missing multiblock registrations.

## Active Multiblock Model Matrix

| Multiblock | Blockstate | Item model | Dynamic model | Split models | OBJ files |
| --- | ---: | ---: | ---: | ---: | ---: |
| `alternator` | yes | yes | yes | 1 | 1 |
| `boiler_liquid` | yes | yes | yes | 1 | 1 |
| `boiler_solid` | yes | yes | yes | 2 | 1 |
| `boiler_tank` | yes | yes | yes | 1 | 1 |
| `cooling_tower` | yes | yes | yes | 2 | 1 |
| `distiller` | yes | yes | yes | 2 | 2 |
| `gas_turbine` | yes | yes | yes | 2 | 2 |
| `heat_exchanger` | yes | yes | yes | 2 | 2 |
| `solar_melter` | yes | yes | yes | 1 | 1 |
| `solar_reflector` | yes | yes | yes | 1 | 3 |
| `solar_tower` | yes | yes | yes | 1 | 1 |
| `steam_turbine` | yes | yes | yes | 2 | 2 |
| `steel_sheetmetal_tank` | yes | yes | yes | 1 | 1 |

## Orphan Or Legacy Signals

One structure file is present without an active 1.21.1 registration:

- `src/main/resources/data/immersivetechnology/structures/multiblocks/radiator.nbt`

This file is also present in the same-version upstream `1.21.1-3.0-Dev` baseline, so it is not a new regression introduced by this port branch. It appears to be a stale or reserved structure asset.

There are also stale sound/localization references for old removed machines:

- `electrolytic_crucible_battery`
- `melting_crucible`

These are present in sound registration/datagen/lang resources, but no active generated model or blockstate points at them.

## Comparison Against 1.20.1 Branch

The older `upstream/1.20.1-2.0-Dev` branch registered 18 multiblocks. The current 1.21.1 branch registers 13.

The 5 older multiblocks not active in the current branch are:

- `advanced_coke_oven`
- `electrolytic_crucible_battery`
- `melting_crucible`
- `radiator`
- `radiator_horizontal`

Current active multiblock inventory compared to the 1.20.1 registered list is therefore **13 / 18 = 72.22%** by count.

Current active multiblock inventory compared to the same-version 1.21.1 upstream active list is **13 / 13 = 100.00%** by count.

## Conclusion

For the current 1.21.1 port target, the active multiblock and model layer is complete by file/reference audit:

- **100.00% active multiblock coverage against the 1.21.1 baseline**
- **0 missing model, OBJ, MTL, or texture references**
- **0 missing manual pages for active multiblocks**

The only risks found are legacy or stale assets from older branch history, not broken references in the active 1.21.1 model graph.
