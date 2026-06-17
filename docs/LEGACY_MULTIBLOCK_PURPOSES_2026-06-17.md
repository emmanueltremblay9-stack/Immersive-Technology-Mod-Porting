# Legacy Multiblock Machine Purposes - 2026-06-17

## Scope

This report documents the five multiblock machine IDs that existed in `upstream/1.20.1-2.0-Dev` but are not active in the current `port/1.21.1-neoforge` branch.

- Source baseline: `upstream/1.20.1-2.0-Dev`
- Current branch: `port/1.21.1-neoforge`
- Current active coverage before this report: 13 active multiblocks
- Older 1.20.1 active count: 18 active multiblocks

The five older machine IDs are:

1. `advanced_coke_oven`
2. `electrolytic_crucible_battery`
3. `melting_crucible`
4. `radiator`
5. `radiator_horizontal`

## Summary Table

| Machine ID | Player-facing purpose | Main inputs | Main outputs | Power/heat behavior | Current 1.21.1 status |
| --- | --- | --- | --- | --- | --- |
| `advanced_coke_oven` | Faster coke and creosote production based on Immersive Engineering coke oven recipes | Coke oven item inputs, usually coal-family inputs | Coke item output plus creosote | No direct process energy; optional powered baseheaters speed it up | Removed/inactive |
| `electrolytic_crucible_battery` | Electrolysis of molten salt or similar fluids into multiple fluid outputs | Molten salt fluid | Chlorine plus heated salt slurry in the shipped recipe | Uses FE/RF-style energy per recipe | Removed/inactive |
| `melting_crucible` | Electrically heats/melts compatible fluids into molten or transformed outputs | Heated slurry fluids through fluid input | Molten salt or lava in shipped recipes | Uses energy to raise/maintain heat and requires recipe temperature | Removed/inactive |
| `radiator` | Vertical compact cooler for hot fluids/gasses | Exhaust steam or hot water | Distilled water or water | No energy recipe cost; speed depends on biome temperature; disabled in Nether | Removed/inactive except stale `radiator.nbt` |
| `radiator_horizontal` | Horizontal form of the same radiator process | Same radiator recipes | Same radiator outputs | Same no-energy cooling behavior as vertical radiator | Removed/inactive |

## Machine Details

### Advanced Coke Oven

Purpose:

- A larger/faster coke oven variant.
- Processes Immersive Engineering coke oven inputs into coke-style item output.
- Produces creosote as fluid output.
- Reuses/copies Immersive Engineering `CokeOvenRecipe` entries when no dedicated Immersive Technology recipe overrides them.

Operation from old manual:

- Items go in from the top.
- Outputs are collected from the front.
- Baseheaters can speed up processing.

Technical behavior from old code:

- Recipe class: `AdvancedCokeOvenRecipe`
- Process class: `AdvancedCokeOvenProcess`
- Logic class: `AdvancedCokeOvenLogic`
- Recipe energy: `0`
- Internal output tank capacity: `12` buckets.
- Output fluid: Immersive Engineering creosote.
- Optional helper block: `advanced_coke_oven_baseheater`
- Baseheater consumes energy and contributes to process speed if active.

Reconstruction implication:

- Restoring this machine is not only a multiblock restore. It also needs the baseheater block, block entity, renderer/model, GUI/menu, sound entries, IE coke recipe adapter, JEI category, and NeoForge 1.21.1 capability migration.

### Electrolytic Crucible Battery

Purpose:

- Performs electrolysis on molten salts and other compatible fluids.
- Designed as a high-efficiency multi-output fluid processor.

Operation from old manual:

- Fluid is supplied from the side.
- Energy is supplied from the side.
- Fluid/item outputs are collected from multiple sides.

Shipped 1.20.1 recipe example:

- Input: `1000 mB` molten salt tag.
- Energy: `512000`.
- Time: `250` ticks.
- Output 0: `1000 mB` chlorine.
- Output 1: `1000 mB` heated salt slurry.

Technical behavior from old code:

- Recipe class: `ElectrolyticCrucibleBatteryRecipe`
- Process class: `ElectrolyticCrucibleBatteryProcess`
- Logic class: `ElectrolyticCrucibleBatteryLogic`
- Consumes input fluid at process start.
- Requires enough energy per tick before processing.
- Supports up to three fluid outputs in process completion logic.

Reconstruction implication:

- Restoring this needs recipe serializer/codecs, energy capability, input/output fluid capability positions, JEI category, sound, model, and multi-output tank sync.

### Melting Crucible

Purpose:

- An electric melting/heating machine for fluid recipes.
- Takes compatible input fluids and converts them into hotter/molten outputs when the machine reaches the required temperature.

Operation from old manual:

- Fluids input on the back.
- Molten fluids output on the bottom front.
- Energy inputs on the side.

Shipped 1.20.1 melting recipe examples:

- Heated salt slurry -> molten salt.
- Heated gravel slurry -> lava.
- Both examples use `1000 mB` input, `500 mB` output, `20` ticks, and `1000.0` required temperature.

Technical behavior from old code:

- Recipe class: `MeltingRecipe`
- Process class: `MeltingCrucibleProcess`
- Logic class: `MeltingCrucibleLogic`
- Uses energy to heat up and maintain temperature.
- Has a heat loss calculation based on biome temperature.
- Only processes when `heatLevel >= recipe.requiredTemp`.

Reconstruction implication:

- This machine is tied to the shared old `melting` recipe type used by both the Melting Crucible and Solar Melter paths. Restoring it requires deciding whether to preserve the old shared recipe type or map it onto the current 1.21.1 solar/melting recipe structure.

### Radiator

Purpose:

- A compact cooling multiblock for hot fluids and gasses.
- Functionally overlaps with the Cooling Tower but is smaller and orientation-specific.

Operation from old manual:

- Hot fluids/gasses input on one end.
- Cooled fluids/gasses output on the opposite end.
- Vertical structure size: `1x7x9`.
- Works in vacuum but with worse efficiency according to the manual.

Shipped 1.20.1 radiator recipe examples:

- `500 mB` exhaust steam -> `250 mB` distilled water in `80` ticks.
- `1000 mB` hot water -> `800 mB` water in `20` ticks.

Technical behavior from old code:

- Recipe class: `RadiatorRecipe`
- Process class: `RadiatorProcess`
- Logic class: `RadiatorLogic`
- Recipe energy: `0`
- Queue size: up to two processes.
- Process speed uses a biome temperature multiplier.
- Nether dimension returns a speed multiplier of `0.0`.

Reconstruction implication:

- Restoring it requires fluid capability migration, redstone control, environment-speed logic, model/split model restore, JEI category, and recipe serializer/codecs.

### Radiator Horizontal

Purpose:

- Horizontal orientation of the radiator.
- Same cooling recipe family as the vertical radiator.

Operation from old manual:

- Horizontal structure size: `9x1x7`.
- Uses the same input/output concept as vertical radiator, but laid out horizontally.

Technical behavior from old code:

- Logic class: `RadiatorHorizontalLogic`
- Process class: `RadiatorHorizontalProcess`
- Recipe class: same `RadiatorRecipe`
- Uses the same no-energy cooling process and biome temperature multiplier behavior.

Reconstruction implication:

- This should be treated as a paired restore with `radiator`, not as an independent recipe system. Both orientations should share recipe type and player-facing manual entry unless there is a design reason to split them.

## Current Branch Notes

The current 1.21.1 branch does not actively register these five machines.

Known leftovers in the current branch:

- `src/main/resources/data/immersivetechnology/structures/multiblocks/radiator.nbt`
- sound/lang entries for `electrolytic_crucible_battery`
- sound/lang entries for `melting_crucible`

No active generated model or blockstate currently points at these stale entries.

## Restore Priority Assessment

If these machines are restored later, recommended order is:

1. `radiator` + `radiator_horizontal`: smallest recipe surface and no energy recipe cost, but two orientation models.
2. `melting_crucible`: shares melting recipe semantics with existing solar/thermal flow.
3. `electrolytic_crucible_battery`: more complex due to multi-output fluids and energy.
4. `advanced_coke_oven`: broadest restore because it includes the powered baseheater auxiliary block and IE coke recipe compatibility.

This order is based on expected NeoForge 1.21.1 port complexity, not player importance.
