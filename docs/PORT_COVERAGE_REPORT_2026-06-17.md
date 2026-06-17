# Immersive Technology Reforged Port Coverage Report - 2026-06-17

## Scope

This audit measures the current port against upstream repository snapshots by file identity, file path continuity, and text-line retention.

- Current branch: `port/1.21.1-neoforge`
- Current HEAD: `99ec69c529cfef798435b9a06537a1911daba9ff`
- Primary baseline: `upstream/1.21.1-3.0-Dev` at `999cf21321fef7bea9049b7377b43e3ae884736b`
- Secondary baseline: `upstream/1.20.1-2.0-Dev` at `f4d757c5bf2a92a6c112d2cf24ab4cab2e0f53e2`
- Legacy baseline: `upstream/1.12.2-1.10-Release` at `8c2266974d53d878689a15e7590dbd393be4e513`

## Definitions

- Traceable file: current file exists at the same path in the baseline, is an exact moved copy, or is a same-basename text file with at least 80% line similarity.
- Exact copied file: current file is byte-identical to a baseline file at the same path or another path.
- Updated same-path file: current file exists at the same path in the baseline but differs by content.
- New or port-only file: current file is not traceable to the baseline by the rules above.
- Text-line retention: weighted line similarity for modified same-path text files only.

This is an exact audit for the above metrics. It is not a semantic gameplay parity score.

## Primary Result: Upstream 1.21.1 Baseline

Against `upstream/1.21.1-3.0-Dev`, the current port is **98.06% traceable by file inventory**.

If "copied from original" means byte-for-byte identical, the current port is **67.44% exact copied overall**.

| Metric | Count | Percent |
| --- | ---: | ---: |
| Current files | 823 | 100.00% |
| Baseline files | 808 | - |
| Traceable to baseline | 807 / 823 | 98.06% |
| Exact copied from baseline | 555 / 823 | 67.44% |
| Updated at same path | 252 / 823 | 30.62% |
| New or port-only | 16 / 823 | 1.94% |
| Baseline removed or unrepresented | 1 / 808 | 0.12% |

## Category Breakdown

| Category | Current files | Traceable | Exact copied | Updated same path | New/port-only | Removed from baseline | Modified text retention |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| Java code | 333 | 96.10% | 30.93% | 217 | 13 | 0 | 88.40% |
| Binary assets/media | 91 | 100.00% | 100.00% | 0 | 0 | 0 | n/a |
| Asset text resources | 102 | 100.00% | 72.55% | 28 | 0 | 0 | 93.85% |
| Data resources | 128 | 100.00% | 100.00% | 0 | 0 | 0 | n/a |
| Build config | 7 | 100.00% | 71.43% | 2 | 0 | 0 | 96.34% |
| Mod metadata | 3 | 66.67% | 33.33% | 1 | 1 | 1 | 86.67% |
| Docs | 5 | 60.00% | 40.00% | 1 | 2 | 0 | 88.24% |
| Other | 154 | 100.00% | 98.05% | 3 | 0 | 0 | 96.77% |

## Java Code Result

Java code is **96.10% traceable** to the same-version upstream baseline.

- 103 Java files are byte-identical at the same path.
- 217 Java files are same-path updates.
- 13 Java files are new or port-only compatibility/support code.
- Modified same-path Java files retain **88.40%** of their text lines by weighted line comparison.

The new Java files are compatibility and port-support additions:

- `src/main/java/mctmods/immersivetechnology/api/convergence/HeatCapabilities.java`
- `src/main/java/mctmods/immersivetechnology/api/convergence/MechanicalCapabilities.java`
- `src/main/java/mctmods/immersivetechnology/api/convergence/capability/IHeatConsumer.java`
- `src/main/java/mctmods/immersivetechnology/api/convergence/capability/IHeatProvider.java`
- `src/main/java/mctmods/immersivetechnology/api/convergence/capability/IMechanicalEnergyConsumer.java`
- `src/main/java/mctmods/immersivetechnology/api/convergence/capability/IMechanicalEnergyProvider.java`
- `src/main/java/mctmods/immersivetechnology/common/multiblocks/metal/recipe/serializer/ITRecipeSerializerCodecs.java`
- `src/main/java/mctmods/immersivetechnology/core/compat/ie/IEFinishedRecipe.java`
- `src/main/java/mctmods/immersivetechnology/core/compat/ie/ItemNBTHelper.java`
- `src/main/java/mctmods/immersivetechnology/core/util/ITFluidIngredients.java`
- `src/main/java/mctmods/immersivetechnology/core/util/ITFluidStacks.java`
- `src/main/java/mctmods/immersivetechnology/core/util/capability/CapabilityReference.java`
- `src/main/java/mctmods/immersivetechnology/core/util/capability/StoredCapability.java`

## Assets And Resources Result

Assets and resources are effectively fully ported from the same-version upstream baseline by inventory.

- Binary assets/media: **91 / 91 files exact copied**, 100.00%.
- Data resources: **128 / 128 files exact copied**, 100.00%.
- Asset text resources: **102 / 102 files traceable**, with 74 exact copied and 28 updated.
- Modified asset text resources retain **93.85%** of their text lines.

This means textures/media/data resources were preserved exactly, while text asset resources were updated where required for the port.

## Metadata Changes

One original metadata file is intentionally unrepresented:

- Removed/replaced: `src/main/resources/META-INF/mods.toml`
- New NeoForge metadata: `src/main/resources/META-INF/neoforge.mods.toml`

This is expected for NeoForge 1.21.1 metadata layout.

## Secondary Baseline Context

| Baseline | Traceable | Exact copied | Updated same path | New/port-only | Baseline unrepresented |
| --- | ---: | ---: | ---: | ---: | ---: |
| `upstream/1.21.1-3.0-Dev` | 98.06% | 67.44% | 30.62% | 1.94% | 0.12% |
| `upstream/1.20.1-2.0-Dev` | 95.38% | 56.87% | 38.40% | 4.62% | 33.42% |
| `upstream/1.12.2-1.10-Release` | 5.47% | 2.67% | 2.43% | 94.53% | 92.53% |

The 1.12.2 result is not a fair current-port quality score because the mod layout, Minecraft APIs, package structure, data generation, resources, and loader metadata changed heavily between 1.12.2 and 1.21.1.

## Conclusion

For the current 1.21.1 NeoForge branch, the precise file-inventory port score is:

- **98.06% traceable to the same-version upstream original**
- **67.44% byte-for-byte copied from the same-version upstream original**
- **30.62% updated in place for the port**
- **1.94% new or port-only support files**

The asset and data side is fully preserved by inventory. The Java side is mostly same-path ported code with compatibility updates, not a broad rewrite.
