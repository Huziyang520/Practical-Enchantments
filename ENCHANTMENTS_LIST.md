# Practical Enchantments — Enchantment List

Every enchantment this mod adds, in one place. Ids are `practical_enchantments:<id>`, usable in
commands, in data packs and in MerlinLib's item editor; `/merlinlib list` prints the live registry.

**Columns**

- **Applies to** — the item family the enchantment accepts.
- **Obtained by** — the sources that exist in this build: the enchanting table, librarian trades, and
  the structure chests this mod injects enchanted books into, with the chance per chest.
  *Not obtainable* means creative mode / `/merlinlib book` only, by design.
- **Conflicts with** — enforced by the anvil and by the enchanting table alike.

## At a glance

| Enchantment | id | Applies to | Levels | Obtained by | Conflicts with |
|---|---|---|---|---|---|
| Aerial Haste | `aerial_haste` | Helmets | single | table · trade · loot | — |
| Beheading | `beheading` | Swords, axes | Ⅰ–Ⅲ | trade · loot | — |
| Bright | `bright` | Helmets | single | table · trade · loot | — |
| Cat Attraction | `cat_attraction` | Leggings | single | trade · loot | — |
| Cat Charm | `cat_charm` | Helmets | single | trade · loot | — |
| Chinese | `chinese` | Chestplates | single | loot | — |
| Demonic Pact | `demonic_pact` | Chestplates | Ⅰ–Ⅲ | loot | Undying Drop |
| Destruction | `destruction` | Mining tools | single | trade · loot | Fortune, Silk Touch |
| Disguise | `disguise` | Heads (7 kinds) | single | trade · loot | — |
| Execute | `execute` | Swords, axes | single | not obtainable | — |
| Frenzy | `frenzy` | Swords, axes | Ⅰ–Ⅱ | trade · loot | — |
| Gentle Descent | `gentle_descent` | Boots | single | not obtainable | Feather Falling |
| Incinerate | `incinerate` | Swords, axes | single | trade · loot | Looting |
| Life Steal | `life_steal` | Swords, axes | Ⅰ–Ⅴ | table · trade · loot | — |
| Lumberjack | `lumberjack` | Axes | single | table · trade · loot | Fortune, Silk Touch |
| Penetration | `penetration` | Tridents | Ⅰ–Ⅴ | table · loot | Riptide |
| Powerful | `powerful` | Crossbows | Ⅰ–Ⅴ | table · trade · loot | Multishot |
| Smashing | `smashing` | Tridents | Ⅰ–Ⅴ | table · loot | — |
| Soul Siphon | `soul_siphon` | Swords, axes | Ⅰ–Ⅴ | table · trade · loot | — |
| Undying Drop | `undying_drop` | Armour (any piece) | Ⅰ–Ⅲ | not obtainable | Demonic Pact |
| Venom | `venom` | Swords | Ⅰ–Ⅱ | table · trade · loot | Wither Aspect, Fire Aspect |
| Wither Aspect | `wither_aspect` | Swords | Ⅰ–Ⅱ | trade · loot | Venom, Fire Aspect |

## Details

### Aerial Haste (`aerial_haste`)

- **Applies to:** helmets · **Levels:** single
- **Obtained by:** enchanting table, librarian trades, ruined portal 30%, shipwreck treasure 20%, end city 10%, ancient city 8%
- **Effect:** removes the airborne mining penalty — mining in mid-air is as fast as mining on the ground. Underwater mining is not affected.

### Beheading (`beheading`)

- **Applies to:** swords and axes · **Levels:** Ⅰ–Ⅲ
- **Obtained by:** librarian trades, woodland mansion 12%, pillager outpost 8%
- **Effect:** killed mobs are more likely to drop their head: **+2.5 % per level**. Only mobs that own a head item are affected.

### Bright (`bright`)

- **Applies to:** helmets · **Levels:** single
- **Obtained by:** enchanting table, librarian trades, armorer trade (18 emeralds for an enchanted diamond helmet), abandoned mineshaft 10%, dungeon 8%, shipwreck treasure 7%, igloo 6%
- **Effect:** permanent Night Vision while worn. The helmet loses one point of durability per second during the night and stops doing so during the day; taking the helmet off ends the effect at once.

### Cat Attraction (`cat_attraction`)

- **Applies to:** leggings · **Levels:** single
- **Obtained by:** librarian trades, igloo 10%, shipwreck supply 10%, dungeon 6%, abandoned mineshaft 6%
- **Effect:** cats and ocelots no longer run away from you — they can be fed and tamed normally.

### Cat Charm (`cat_charm`)

- **Applies to:** helmets · **Levels:** single
- **Obtained by:** librarian trades, jungle temple 10%, shipwreck treasure 10%, desert pyramid 8%, dungeon 6%
- **Effect:** creepers and phantoms keep their distance and actively move away from you.

### Chinese (`chinese`)

- **Applies to:** chestplates · **Levels:** single
- **Obtained by:** ancient city 10% (nothing else)
- **Effect:** unlocks creative flight while the chestplate is worn. Nothing else from creative mode is unlocked, and the flight ends the moment the chestplate comes off. Durability is spent three times as fast while you are hit in mid-air.

### Demonic Pact (`demonic_pact`)

- **Applies to:** chestplates · **Levels:** Ⅰ–Ⅲ
- **Obtained by:** ancient city 14%, buried treasure 10%
- **Conflicts with:** Undying Drop
- **Effect:** on lethal damage, emeralds are taken from your inventory to keep you alive, exactly like a totem of undying, and you are given Darkness.
  The price is **64 − 8 × (level − 1)** emeralds (Ⅰ 64, Ⅱ 56, Ⅲ 48) — never less than 16 —
  the Darkness lasts **20 − 4 × (level − 1)** seconds, and the enchantment then goes on a **60 second
  cooldown**. Too few emeralds means no rescue and a normal death.

### Destruction (`destruction`)

- **Applies to:** mining tools · **Levels:** single
- **Obtained by:** librarian trades, nether fortress 20%, woodland mansion 15%, bastion remnant 10%, pillager outpost 10%
- **Conflicts with:** Fortune, Silk Touch
- **Effect:** blocks you mine drop no items; you get experience instead — 2 points per block, 5 for ores. Only blocks the tool could actually mine are affected.

### Disguise (`disguise`)

- **Applies to:** skeleton, wither skeleton, zombie, creeper, piglin, dragon and player heads · **Levels:** single
- **Obtained by:** librarian trades (24 emeralds for the book), woodland mansion 12%, bastion treasure 10%, stronghold library 10%, end city 8%, pillager outpost 8%
- **Effect:** wearing a head stops the matching mobs from attacking you on their own. A piglin head also covers piglin brutes; a wither skeleton head does **not** stop the wither. Attacking a mob still provokes it — the disguise hides you, it does not make you immune.

### Execute (`execute`)

- **Applies to:** swords and axes · **Levels:** single
- **Obtained by:** ancient city 20%, end city 15%, woodland mansion 10% (no other source by design)
- **Effect:** extra damage against targets that are already hurt. Between 10 % and 35 % health the
  damage is **×1.5**, below 10 % it is **×3** — and **×2 / ×4** when the weapon is an axe. Above 35 %
  nothing changes. The multiplier applies to the final damage, so it stacks with Sharpness.

### Frenzy (`frenzy`)

- **Applies to:** swords and axes · **Levels:** Ⅰ–Ⅱ
- **Obtained by:** librarian trades, woodland mansion 18%, pillager outpost 14%
- **Effect:** killing something grants Speed — Ⅱ for 6 seconds at level Ⅰ, Ⅲ for 8 seconds at level Ⅱ.
  On an axe the duration is doubled. Each kill restarts the timer.

### Gentle Descent (`gentle_descent`)

- **Applies to:** boots · **Levels:** single
- **Obtained by:** not obtainable in survival (creative / commands)
- **Conflicts with:** Feather Falling
- **Effect:** permanent Slow Falling while worn, with the fall damage that comes with it. Sneaking
  switches it off, so a quick descent is one key away.

### Incinerate (`incinerate`)

- **Applies to:** swords and axes · **Levels:** single
- **Obtained by:** librarian trades, jungle temple 35%, desert pyramid 20%, large underwater ruin 20%, woodland mansion 10%
- **Conflicts with:** Looting
- **Effect:** mobs you kill drop nothing and give **double experience**. Players are not affected.

### Life Steal (`life_steal`)

- **Applies to:** swords and axes · **Levels:** Ⅰ–Ⅴ
- **Obtained by:** enchanting table, librarian trades, stronghold library 18%
- **Effect:** every hit heals you for **4 % × level** of the health the target *actually lost* — hitting
  a 2-health mob with a 6-damage weapon heals as if you had dealt 2.

### Lumberjack (`lumberjack`)

- **Applies to:** axes · **Levels:** single
- **Obtained by:** enchanting table, librarian trades, village toolsmith 8%, abandoned mineshaft 6%, pillager outpost 5%, dungeon 4%
- **Conflicts with:** Fortune, Silk Touch
- **Effect:** felling one log breaks the whole connected tree — logs and leaves — up to **64 blocks**
  per swing. Leaves drop saplings and apples at vanilla rates. With Destruction on the same axe the
  tree drops nothing and pays experience instead. Durability is spent per block broken.

### Penetration (`penetration`)

- **Applies to:** tridents · **Levels:** Ⅰ–Ⅴ
- **Obtained by:** enchanting table, shipwreck treasure 22%
- **Conflicts with:** Riptide
- **Effect:** a thrown trident deals **1.5 + 0.5 × (level − 1)** extra damage. With Loyalty as well,
  the creature the trident hit is dragged back to you; the ender dragon and the wither are immune.

### Powerful (`powerful`)

- **Applies to:** crossbows · **Levels:** Ⅰ–Ⅴ
- **Obtained by:** enchanting table, librarian trades, pillager outpost 12%, village fletcher 8%, dungeon 6%, bastion remnant 5%
- **Conflicts with:** Multishot
- **Effect:** crossbow bolts deal **+25 % damage per level**, following the same curve as vanilla
  Power. Bows are not affected.

### Smashing (`smashing`)

- **Applies to:** tridents · **Levels:** Ⅰ–Ⅴ
- **Obtained by:** enchanting table, large underwater ruin 20%, shipwreck treasure 14%, abandoned mineshaft 12%
- **Effect:** a thrown trident breaks the block it hits. The level decides which pickaxe it counts as —
  Ⅰ wood, Ⅱ stone, Ⅲ iron, Ⅳ diamond, Ⅴ netherite — and blocks above that tier are left alone rather
  than destroyed without dropping. Unbreakable blocks such as bedrock are never touched. With
  Penetration **and** Loyalty the drops fly back to you.

### Soul Siphon (`soul_siphon`)

- **Applies to:** swords and axes · **Levels:** Ⅰ–Ⅴ
- **Obtained by:** enchanting table, librarian trades, stronghold library 24%
- **Effect:** each hit produces experience orbs worth **damage × 0.2 × level**, capped at
  **2 × level** per hit. The base is the damage before armour reduction.

### Undying Drop (`undying_drop`)

- **Applies to:** any armour piece · **Levels:** Ⅰ–Ⅲ
- **Obtained by:** not obtainable in survival (creative / commands)
- **Conflicts with:** Demonic Pact
- **Effect:** on lethal damage you survive as if by a totem, but **every item in your inventory is
  dropped on the ground and the enchanted armour piece is destroyed**. Darkness lasts
  **20 − 4 × (level − 1)** seconds. Deliberately a joke enchantment: the escape costs more than the death.

### Venom (`venom`)

- **Applies to:** swords · **Levels:** Ⅰ–Ⅱ
- **Obtained by:** enchanting table, librarian trades, jungle temple 28%, shipwreck treasure 16%
- **Conflicts with:** Wither Aspect, Fire Aspect
- **Effect:** hits poison the target — level Ⅰ for 6 seconds, level Ⅱ for 12. Undead are immune, as with
  every poison in the game.

### Wither Aspect (`wither_aspect`)

- **Applies to:** swords · **Levels:** Ⅰ–Ⅱ
- **Obtained by:** librarian trades, nether fortress 22%, bastion remnant 15%, ancient city 12%
- **Conflicts with:** Venom, Fire Aspect
- **Effect:** hits wither the target — 6 seconds at level Ⅰ, 12 at level Ⅱ — at the cost of weapon
  damage: **−1** at level Ⅰ and **−0.5** at level Ⅱ. Wither hurts everything, undead included, and can
  kill.
