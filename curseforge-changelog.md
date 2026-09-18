### 1.2.2 (2026-09-18)

- Fix Penetration: thrown tridents now really deal the extra `1.5 + 0.5 x (level - 1)` damage. The target's damage cooldown was discarding the bonus hit entirely.
- Fix Drop Inventory (undying_drop): the dropped inventory now scatters around the player like a vanilla death drop instead of piling up in a single block.
- Add the 9 enchantment-table enchantments to `#minecraft:in_enchanting_table`, so they can actually be offered by the enchanting table.
- NeoForge: declare `iconFile` instead of the deprecated `logoFile`, so the 128x128 square icon is shown in the mod list instead of being stretched into the wide banner slot.
- Includes the upstream EnchantLib fix for the "world uses experimental settings" warning shown on every world open.
